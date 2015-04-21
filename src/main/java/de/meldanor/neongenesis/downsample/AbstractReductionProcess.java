/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Kilian Gärtner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.meldanor.neongenesis.downsample;

import de.meldanor.neongenesis.Core;
import de.meldanor.neongenesis.hdf5.Flash3DataTypes;
import de.meldanor.neongenesis.hdf5.Flash3MetaData;
import de.meldanor.neongenesis.hdf5.Flash3Reader;
import de.meldanor.neongenesis.hdf5.Hdf5Writer;
import de.meldanor.neongenesis.statisticalReduce.StatisticalDatasetReducer;
import de.meldanor.neongenesis.statisticalReduce.StatisticalReducerFactory;
import javafx.geometry.Point3D;
import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.Datatype;

import java.io.File;
import java.util.*;

/**
 *
 */
public abstract class AbstractReductionProcess {
    public static final String GLOBALNUMBLOCKS = "globalnumblocks";
    private static final String X_DIMENSION = "nxb";
    private static final String Y_DIMENSION = "nyb";
    private static final String Z_DIMENSION = "nzb";
    private static final String NAME_DATATYPE = "name";
    private static final String VALUE_DATATYPE = "value";
    protected final StatisticalReducerFactory.StatisticalReducerType strategy;
    protected final List<String> variableDatasetsNames;
    protected final File targetDirectory;

    public AbstractReductionProcess(List<String> variableDatasetsNames, File targetDirectory, StatisticalReducerFactory.StatisticalReducerType strategy) {
        this.variableDatasetsNames = variableDatasetsNames;
        this.targetDirectory = targetDirectory;
        this.strategy = strategy;
    }

    /**
     * Reduce a single HDF5 FLASH3 file using the given reducer strategy. The reduced file will be written to the
     * target directory
     *
     * @param file The file to reduce. Must be a HDF5 FLASH3 formatted file.
     * @throws Exception An error occurred while reducing (can't open file, file is not existing)
     */
    public File reduceFile(File file, boolean verbose) throws Exception {
        Flash3Reader reader = new Flash3Reader(file);
        if (verbose) {
            Core.logger.info("Blocks: " + reader.getMetaData().getBlockCount());
        }
        Point3D originalDimensions = getDimension(reader.getMetaData());
        StatisticalDatasetReducer reducer = new StatisticalDatasetReducer(originalDimensions, strategy);

        File newFile = new File(targetDirectory, file.getName() + "_reduced");

        Hdf5Writer writer = new Hdf5Writer(newFile);
        copyMetaData(reader, writer);
        createIntegerScalars(reader, writer);

        reduceDatasets(reducer, reader, writer);
        reader.close();
        writer.close();

        return newFile;
    }

    protected Point3D getDimension(Flash3MetaData metaData) {

        int xDimension = metaData.getIntegerSclar(X_DIMENSION);
        int yDimension = metaData.getIntegerSclar(Y_DIMENSION);
        int zDimension = metaData.getIntegerSclar(Z_DIMENSION);
        return new Point3D(xDimension, yDimension, zDimension);
    }

    private void copyMetaData(Flash3Reader source, Hdf5Writer destination) throws Exception {
        Flash3MetaData metaData = source.getMetaData();

        Map<String, Dataset> variableMap = metaData.getVariableMap();
        // Get all datasets, which are not variable data like density or pressure
        metaData.getDatasetMap().entrySet().stream()
                .filter(e -> !variableMap.containsKey(e.getKey()))
                        // Don't copy the logical scalar map - we have to modify it
                .filter(e -> !e.getKey().equals(Flash3MetaData.Flash3Dataset.INTEGER_SCALARS.getDatasetName()))
                        // Copy all metadata
                .forEach(e -> {
                    try {
                        destination.copyDataset(e.getValue());
                    } catch (Exception e1) {
                        throw new RuntimeException(e1);
                    }
                });


    }

    private void createIntegerScalars(Flash3Reader source, Hdf5Writer destination) throws Exception {
        Flash3MetaData metaData = source.getMetaData();
        Map<String, Integer> integerSclars = new LinkedHashMap<>(metaData.getIntegerSclars());

        // Reduce the cell dimensions to half
        integerSclars.computeIfPresent(X_DIMENSION, (key, value) -> value / 2);
        integerSclars.computeIfPresent(Y_DIMENSION, (key, value) -> value / 2);
        integerSclars.computeIfPresent(Z_DIMENSION, (key, value) -> value / 2);

        // Create data vector -> transform the map to two arrays with their content
        Vector<Object> data = new Vector<>();
        data.add(integerSclars.keySet().toArray(new String[integerSclars.size()]));
        data.add(integerSclars.values().toArray(new Integer[integerSclars.size()]));

        // Create the datatype map - for integer scalar this is a string and a value
        Map<String, Datatype> datatypes = new HashMap<>();
        datatypes.put(NAME_DATATYPE, Flash3DataTypes.VARCHAR_80.getDatatype());
        datatypes.put(VALUE_DATATYPE, Flash3DataTypes.INT.getDatatype());

        int[] memberSizes = {1, 1};

        destination.writeCompound(Flash3MetaData.Flash3Dataset.INTEGER_SCALARS.getDatasetName(), data, datatypes, memberSizes, integerSclars.size());
    }

    protected abstract void reduceDatasets(StatisticalDatasetReducer reducer, Flash3Reader source, Hdf5Writer destination) throws Exception;

    protected void writeReducedIntDataset(List<int[]> ints, String datasetName, Flash3Reader source, Hdf5Writer writer, Buffer buffer) throws Exception {

        int[] flattenArray = buffer.flattenIntArray;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < ints.size(); i++) {
            int[] values = ints.get(i);
            for (int value : values) {
                min = Math.min(min, value);
                max = Math.max(max, value);
            }
            System.arraycopy(values, 0, flattenArray, i * values.length, values.length);
        }

        Attribute maxAttribute = new Attribute("maximum", Flash3DataTypes.INT.getDatatype(), new long[]{1L});
        maxAttribute.setValue(new int[]{max});
        Attribute minAttribute = new Attribute("minimum", Flash3DataTypes.INT.getDatatype(), new long[]{1L});
        minAttribute.setValue(new int[]{max});

        Point3D dimension = getDimension(source.getMetaData());
        int xDim = (int) (dimension.getX() / 2);
        int yDim = (int) (dimension.getY() / 2);
        int zDim = (int) (dimension.getZ() / 2);

        writer.writeIntDataset(datasetName, flattenArray, Arrays.asList(maxAttribute, minAttribute), ints.size(), xDim, yDim, zDim);
    }

    protected void writeReducedFloatDataset(List<float[]> floats, String datasetName, Flash3Reader source, Hdf5Writer writer, Buffer buffer) throws Exception {

        float[] flattenArray = buffer.flattenFloatArray;
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        for (int i = 0; i < floats.size(); i++) {
            float[] values = floats.get(i);
            for (float value : values) {
                min = Math.min(min, value);
                max = Math.max(max, value);
            }
            System.arraycopy(values, 0, flattenArray, i * values.length, values.length);
        }

        Attribute maxAttribute = new Attribute("maximum", Flash3DataTypes.FLOAT.getDatatype(), new long[]{1L});
        maxAttribute.setValue(new float[]{max});
        Attribute minAttribute = new Attribute("minimum", Flash3DataTypes.FLOAT.getDatatype(), new long[]{1L});
        minAttribute.setValue(new float[]{min});

        Point3D dimension = getDimension(source.getMetaData());
        int xDim = (int) (dimension.getX() / 2);
        int yDim = (int) (dimension.getY() / 2);
        int zDim = (int) (dimension.getZ() / 2);

        writer.writeFloatDataset(datasetName, flattenArray, Arrays.asList(maxAttribute, minAttribute), floats.size(), xDim, yDim, zDim);
    }

    protected class Buffer {
        float[] flattenFloatArray;
        int[] flattenIntArray;

        public Buffer(Flash3MetaData metaData, int xDim, int yDim, int zDim) {
            flattenFloatArray = new float[metaData.getIntegerSclar(GLOBALNUMBLOCKS) * xDim * yDim * zDim];
            flattenIntArray = new int[metaData.getIntegerSclar(GLOBALNUMBLOCKS) * xDim * yDim * zDim];
        }
    }
}
