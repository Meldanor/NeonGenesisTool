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

package de.meldanor.neongenesis.physicalReduce;

import de.meldanor.neongenesis.downsample.AbstractReductionProcess;
import de.meldanor.neongenesis.hdf5.*;
import de.meldanor.neongenesis.statisticalReduce.StatisticalDatasetReducer;
import de.meldanor.neongenesis.statisticalReduce.StatisticalReducerFactory;
import javafx.geometry.Point3D;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.Datatype;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class PhysicalReductionProcess extends AbstractReductionProcess {

    public PhysicalReductionProcess(StatisticalReducerFactory.StatisticalReducerType strategy, List<String> variableDatasetsNames, File targetDirectory) {
        super(variableDatasetsNames, targetDirectory, strategy);
    }

    @Override
    protected void reduceDatasets(StatisticalDatasetReducer reducer, Flash3Reader source, Hdf5Writer destination) throws Exception {
        Map<String, Dataset> variableMap = source.getMetaData().getVariableMap();

        Point3D dimension = getDimension(source.getMetaData());
        int xDim = (int) (dimension.getX() / 2);
        int yDim = (int) (dimension.getY() / 2);
        int zDim = (int) (dimension.getZ() / 2);

        Buffer buffer = new Buffer(source.getMetaData(), xDim, yDim, zDim);

        List<String> datasetsToReduce = this.variableDatasetsNames;
        if (datasetsToReduce.isEmpty())
            datasetsToReduce = new ArrayList<>(variableMap.keySet());

        for (String dataset : datasetsToReduce) {
            reduceDataset(dataset, reducer, source, destination, buffer, dimension);
        }
    }

    private void reduceDataset(String datasetName, StatisticalDatasetReducer reducer, Flash3Reader source, Hdf5Writer destination, Buffer buffer, Point3D dimensions) throws Exception {
        Dataset dataset = source.getMetaData().getDataset(datasetName);
        dataset.init();

        if (dataset.getDatatype().getDatatypeClass() != Datatype.CLASS_FLOAT)
            throw new IllegalArgumentException("Unsupported datatype found while reducing! Datatype: " + dataset.getDatatype());


        switch (datasetName) {
            case "temp":
                List<float[]> temperatures = reduceTemperaturDateset(source, dataset, dimensions);
                writeReducedFloatDataset(temperatures, datasetName, source, destination, buffer);
                break;
            default:
                List<float[]> floats = reducer.reduceFloatDataset(source, datasetName);
                writeReducedFloatDataset(floats, datasetName, source, destination, buffer);
                break;
        }
    }

    private List<float[]> reduceTemperaturDateset(Flash3Reader source, Dataset dataset, Point3D dimensions) throws Exception {

        Flash3MetaData metaData = source.getMetaData();
        // We need the density for calculating the mix temperature
        Dataset densityDataset = metaData.getDataset("dens");

        List<float[]> results = new ArrayList<>(metaData.getBlockCount());

        BlockTree tree = metaData.getBlockTree();

        for (Block block : tree.getAll()) {
            float[] flatOriginalTemperature = source.readFloatValues(dataset.getName(), block);
            float[] flatOriginalDensity = source.readFloatValues(densityDataset.getName(), block);
            results.add(reduceTemperateValues(flatOriginalTemperature, flatOriginalDensity, dimensions));
        }

        return results;
    }

    private float[] reduceTemperateValues(float[] flatOriginalTemperature, float[] flatOriginalDensity, Point3D dimensions) {
        float[] result = new float[flatOriginalTemperature.length / 8];
        float[] temperature = new float[8];
        float[] density = new float[8];
        int xDim = (int) dimensions.getX();
        int yDim = (int) dimensions.getY();
        int zDim = (int) dimensions.getZ();
        for (int i = 0, x = 0; x < xDim; x += 2) {
            for (int y = 0; y < yDim; y += 2) {
                for (int z = 0; z < zDim; z += 2, i++) {

                    // The temperature
                    temperature[0] = flatOriginalTemperature[index(x, y, yDim, z, zDim)];
                    temperature[1] = flatOriginalTemperature[index(x + 1, y, yDim, z, zDim)];
                    temperature[2] = flatOriginalTemperature[index(x, y + 1, yDim, z, zDim)];
                    temperature[3] = flatOriginalTemperature[index(x + 1, y + 1, yDim, z, zDim)];

                    temperature[4] = flatOriginalTemperature[index(x, y, yDim, z + 1, zDim)];
                    temperature[5] = flatOriginalTemperature[index(x + 1, y, yDim, z + 1, zDim)];
                    temperature[6] = flatOriginalTemperature[index(x, y + 1, yDim, z + 1, zDim)];
                    temperature[7] = flatOriginalTemperature[index(x + 1, y + 1, yDim, z + 1, zDim)];

                    // The density
                    density[0] = flatOriginalDensity[index(x, y, yDim, z, zDim)];
                    density[1] = flatOriginalDensity[index(x + 1, y, yDim, z, zDim)];
                    density[2] = flatOriginalDensity[index(x, y + 1, yDim, z, zDim)];
                    density[3] = flatOriginalDensity[index(x + 1, y + 1, yDim, z, zDim)];

                    density[4] = flatOriginalDensity[index(x, y, yDim, z + 1, zDim)];
                    density[5] = flatOriginalDensity[index(x + 1, y, yDim, z + 1, zDim)];
                    density[6] = flatOriginalDensity[index(x, y + 1, yDim, z + 1, zDim)];
                    density[7] = flatOriginalDensity[index(x + 1, y + 1, yDim, z + 1, zDim)];

                    // Calculate the value
                    result[i] = calculateMixTemperature(temperature, density);
                }
            }
        }

        return result;
    }

    private float calculateMixTemperature(float[] temperature, float[] density) {
        float res = 0.0F;

        float sumDensity = 0.0F;
        for (float dens : density) {
            sumDensity += dens;
        }

        for (int i = 0; i < temperature.length && i < density.length; i++) {
            float temp = temperature[i];
            float dens = density[i];

            sumDensity += ((temp * dens) / sumDensity);
        }

        return res;
    }

    private int index(int x, int y, int ySize, int z, int zSize) {
        return x * ySize * zSize + y * zSize + z;
    }
}
