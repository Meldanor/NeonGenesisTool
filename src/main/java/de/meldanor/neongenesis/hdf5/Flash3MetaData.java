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

package de.meldanor.neongenesis.hdf5;

import javafx.geometry.Point3D;
import ncsa.hdf.object.Dataset;

import java.util.*;

/**
 * The meta data of a FLASH3 HDF5 plot file.
 */
public class Flash3MetaData extends Hdf5MetaData {

    /**
     * The standard tables of the plot file
     */
    private enum Flash3Dataset {
        B_FLAGS("bflags"),
        BLOCK_SIZE("block size"),
        BOUNDING_BOX("bounding box"),
        COORDINATES("coordinates"),
        GID("gid"),
        INTEGER_RUNTIME_PARAMETER("integer runtime parameters"),
        INTEGER_SCALARS("integer scalars"),
        LOGICAL_RUNTIME_PARAMETER("logical runtime parameters"),
        LOGICAL_SCALARS("logical scalars"),
        NODE_TYPE("node type"),
        REAL_RUNTIME_PARAMETER("real runtime parameters"),
        REAL_SCALARS("real scalars"),
        REFINE_LEVEL("refine level"),
        STRING_RUNTIME_PARAMETER("string runtime parameters"),
        STRING_SCALARS("string scalars"),
        WHICH_CHILD("which child");

        private final String datasetName;

        Flash3Dataset(String datasetName) {

            this.datasetName = datasetName;
        }

    }

    private final BlockTree blockTree;

    private final Map<String, Dataset> variableMap;

    private final Map<String, Integer> integerRuntimeParameter;
    private final Map<String, Integer> integerSclars;

    private final Map<String, Boolean> logicalRuntimeParameter;
    private final Map<String, Boolean> logicalSclars;

    private final Map<String, Float> realRuntimeParameter;
    private final Map<String, Float> realSclars;

    private final Map<String, String> stringRuntimeParameter;
    private final Map<String, String> stringSclars;

    /**
     * Read and parse the meta data of the file
     *
     * @param data The meta data of the HDF5 file
     * @throws Exception Some error while reading
     */
    public Flash3MetaData(Hdf5MetaData data) throws Exception {
        super(data.getDatasetMap());

        this.blockTree = constructBlockTree();
        readBlockMetaData();

        this.variableMap = constructVariableMap();

        this.integerRuntimeParameter = fillIntegerInfos(Flash3Dataset.INTEGER_RUNTIME_PARAMETER);
        this.integerSclars = fillIntegerInfos(Flash3Dataset.INTEGER_SCALARS);

        this.logicalRuntimeParameter = fillBooleanInfos(Flash3Dataset.LOGICAL_RUNTIME_PARAMETER);
        this.logicalSclars = fillBooleanInfos(Flash3Dataset.LOGICAL_SCALARS);

        this.realRuntimeParameter = fillFloatInfos(Flash3Dataset.REAL_RUNTIME_PARAMETER);
        this.realSclars = fillFloatInfos(Flash3Dataset.REAL_SCALARS);

        this.stringRuntimeParameter = fillStringInfos(Flash3Dataset.STRING_RUNTIME_PARAMETER);
        this.stringSclars = fillStringInfos(Flash3Dataset.STRING_SCALARS);
    }

    private void readBlockMetaData() throws Exception {
        fillBFlags();
        fillBlockSize();
        fillBoundingBox();
        fillCoordinates();
        fillNeighbors();
        fillNodeType();
        fillWhichChild();
        fillRefineLevel();
    }

    private void fillBFlags() throws Exception {
        Dataset set = getDataset(Flash3Dataset.B_FLAGS.datasetName);
        set.init();
        int[] data = (int[]) set.read();
        for (int i = 0; i < data.length; ++i) {
            blockTree.get(i + 1).setBflags((byte) data[i]);
        }
    }

    private void fillBlockSize() throws Exception {
        Dataset set = getDataset(Flash3Dataset.BLOCK_SIZE.datasetName);
        set.init();
        // Restrict values to one dimension
        // The block size is equal in every dimension, so we need only a third of memory
        set.getSelectedDims()[1] = 1;
        float[] data = (float[]) set.read();
        for (int i = 0; i < data.length; ++i) {
            blockTree.get(i + 1).setBlockSize(data[i]);
        }
    }

    private void fillBoundingBox() throws Exception {
        Dataset set = getDataset(Flash3Dataset.BOUNDING_BOX.datasetName);
        set.init();
        set.getSelectedDims()[1] = 3;
        float[] data = (float[]) set.read();
        for (int i = 0, j = 0; i < data.length; i = i + 3, ++j) {
            blockTree.get(j + 1).setBoundingBox(new Point3D(data[i], data[i + 1], data[i + 2]));
        }
    }

    private void fillCoordinates() throws Exception {
        Dataset set = getDataset(Flash3Dataset.COORDINATES.datasetName);
        set.init();
        set.getSelectedDims()[1] = 3;
        float[] data = (float[]) set.read();
        for (int i = 0, j = 0; i < data.length; i = i + 3, ++j) {
            blockTree.get(j + 1).setCoordinates(new Point3D(data[i], data[i + 1], data[i + 2]));
        }
    }

    private void fillNeighbors() throws Exception {
        Dataset set = getDataset(Flash3Dataset.GID.datasetName);
        set.init();
        // Select only the neighbors(6)
        set.getSelectedDims()[1] = 6;
        int[] data = (int[]) set.read();
        int height = set.getHeight();
        for (int i = 0; i < height; i++) {
            Block b = blockTree.get(i + 1);
            List<Block> neighbors = new ArrayList<>(6);
            for (int j = 0; j < 6; ++j) {
                int neighborId = data[(i * 6) + j];
                if (neighborId > 0)
                    neighbors.add(blockTree.get(neighborId));
                else
                    neighbors.add(null);
            }
            b.setNeighbors(neighbors);
        }
    }

    private void fillNodeType() throws Exception {
        Dataset set = getDataset(Flash3Dataset.NODE_TYPE.datasetName);
        set.init();
        int[] data = (int[]) set.read();
        for (int i = 0; i < data.length; ++i) {
            blockTree.get(i + 1).setNodeType((byte) data[i]);
        }
    }

    private void fillRefineLevel() throws Exception {
        Dataset set = getDataset(Flash3Dataset.REFINE_LEVEL.datasetName);
        set.init();
        int[] data = (int[]) set.read();
        for (int i = 0; i < data.length; ++i) {
            blockTree.get(i + 1).setRefineLevel((byte) data[i]);
        }
    }

    private void fillWhichChild() throws Exception {
        Dataset set = getDataset(Flash3Dataset.WHICH_CHILD.datasetName);
        set.init();
        int[] data = (int[]) set.read();
        for (int i = 0; i < data.length; ++i) {
            blockTree.get(i + 1).setWhichChild((byte) data[i]);
        }
    }

    private Map<String, Integer> fillIntegerInfos(Flash3Dataset dataset) throws Exception {
        Map<String, Integer> map = new HashMap<>();
        Vector data = getParameterVector(dataset.datasetName);
        String[] names = (String[]) data.get(0);
        int[] values = (int[]) data.get(1);
        for (int i = 0; i < names.length; i++) {
            map.put(names[i], values[i]);
        }

        return Collections.unmodifiableMap(map);
    }

    private Map<String, Boolean> fillBooleanInfos(Flash3Dataset dataset) throws Exception {
        Map<String, Boolean> map = new HashMap<>();
        Vector data = getParameterVector(dataset.datasetName);
        String[] names = (String[]) data.get(0);
        int[] values = (int[]) data.get(1);
        for (int i = 0; i < names.length; i++) {
            map.put(names[i], values[i] == 1);
        }

        return Collections.unmodifiableMap(map);
    }

    private Map<String, Float> fillFloatInfos(Flash3Dataset dataset) throws Exception {
        Map<String, Float> map = new HashMap<>();
        Vector data = getParameterVector(dataset.datasetName);
        String[] names = (String[]) data.get(0);
        double[] values = (double[]) data.get(1);
        for (int i = 0; i < names.length; i++) {
            map.put(names[i], (float) values[i]);
        }

        return Collections.unmodifiableMap(map);
    }

    private Map<String, String> fillStringInfos(Flash3Dataset dataset) throws Exception {
        Map<String, String> map = new HashMap<>();
        Vector data = getParameterVector(dataset.datasetName);
        String[] names = (String[]) data.get(0);
        String[] values = (String[]) data.get(1);
        for (int i = 0; i < names.length; i++) {
            map.put(names[i], values[i]);
        }

        return Collections.unmodifiableMap(map);
    }

    private Vector getParameterVector(String datasetName) throws Exception {
        Dataset dataset = getDataset(datasetName);
        return (Vector) dataset.getData();
    }

    private Map<String, Dataset> constructVariableMap() {
        Map<String, Dataset> map = new HashMap<>(getDatasetMap());

        for (Flash3Dataset dataset : Flash3Dataset.values()) {
            map.remove(dataset.datasetName);
        }

        return Collections.unmodifiableMap(map);
    }

    private BlockTree constructBlockTree() throws Exception {
        Dataset dataset = getDataset(Flash3Dataset.GID.datasetName);
        dataset.init();

        // Skip the neighbor dimensions
        dataset.getStartDims()[1] = 6;
        // Select only the parent(1) and children dimensions(8)
        dataset.getSelectedDims()[1] = 9;

        int[] data = (int[]) dataset.read();
        return new BlockTree(data, dataset.getHeight());
    }

    /**
     * @return Return the block tree to traverse the structure.
     */
    public BlockTree getBlockTree() {
        return blockTree;
    }

    /**
     * Get a map of datasets which are holding variable data.
     * <p>
     * Will throw an IllegalStateException if the reader was closed.
     *
     * @return Map matching their name
     */
    public Map<String, Dataset> getVariableMap() {
        if (isClosed)
            throw new IllegalStateException("Reader was closed, no access to datasets!");
        return variableMap;
    }

    /**
     * Get the value of a runtime parameter.
     *
     * @param name The name of the parameter
     * @return Its value, if present. Otherwise
     */
    public int getIntegerRuntimeParameter(String name) {
        Integer parameter = integerRuntimeParameter.get(name);
        if (parameter == null)
            throw new NoSuchElementException("There is no integer parameter with the name '" + name + "'!");
        return parameter;
    }

    /**
     * Get the value of a runtime parameter.
     *
     * @param name The name of the parameter
     * @return Its value, if present. Otherwise
     */
    public int getIntegerSclar(String name) {
        Integer parameter = integerSclars.get(name);
        if (parameter == null)
            throw new NoSuchElementException("There is no integer scalar with the name '" + name + "'!");
        return parameter;
    }

    /**
     * Get the value of a runtime parameter.
     *
     * @param name The name of the parameter
     * @return Its value, if present. Otherwise
     */
    public boolean getLogicalRuntimeParameter(String name) {
        Boolean parameter = logicalRuntimeParameter.get(name);
        if (parameter == null)
            throw new NoSuchElementException("There is no logical parameter with the name '" + name + "'!");
        return parameter;
    }

    /**
     * Get the value of a runtime parameter.
     *
     * @param name The name of the parameter
     * @return Its value, if present. Otherwise
     */
    public boolean getLogicalSclar(String name) {
        Boolean parameter = logicalSclars.get(name);
        if (parameter == null)
            throw new NoSuchElementException("There is no logical scalar with the name '" + name + "'!");
        return parameter;
    }

    /**
     * Get the value of a runtime parameter.
     *
     * @param name The name of the parameter
     * @return Its value, if present. Otherwise
     */
    public float getRealRuntimeParameter(String name) {
        Float parameter = realRuntimeParameter.get(name);
        if (parameter == null)
            throw new NoSuchElementException("There is no real parameter with the name '" + name + "'!");
        return parameter;
    }

    /**
     * Get the value of a runtime parameter.
     *
     * @param name The name of the parameter
     * @return Its value, if present. Otherwise
     */
    public float getRealSclars(String name) {
        Float parameter = realSclars.get(name);
        if (parameter == null)
            throw new NoSuchElementException("There is no real scalar with the name '" + name + "'!");
        return parameter;
    }

    /**
     * Get the value of a runtime parameter.
     *
     * @param name The name of the parameter
     * @return Its value, if present. Otherwise
     */
    public String getStringRuntimeParameter(String name) {
        String parameter = stringRuntimeParameter.get(name);
        if (parameter == null)
            throw new NoSuchElementException("There is no string parameter with the name '" + name + "'!");
        return parameter;
    }

    /**
     * Get the value of a runtime parameter.
     *
     * @param name The name of the parameter
     * @return Its value, if present. Otherwise
     */
    public String getStringSclars(String name) {
        String parameter = stringSclars.get(name);
        if (parameter == null)
            throw new NoSuchElementException("There is no string scalar with the name '" + name + "'!");
        return parameter;
    }
}
