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

package de.meldanor.neongenesis.statisticalReduce;

import de.meldanor.neongenesis.hdf5.Block;
import de.meldanor.neongenesis.hdf5.BlockTree;
import de.meldanor.neongenesis.hdf5.Flash3MetaData;
import de.meldanor.neongenesis.hdf5.Flash3Reader;
import javafx.geometry.Point3D;

import java.util.ArrayList;
import java.util.List;

/**
 * Reduce a single block by halving their cells. The strategy for summarizing the content is defined by its
 * {@link StatisticalReducer}.
 * <p>
 * This class is thread-safe!
 *
 * @see StatisticalReducer
 */
public class StatisticalDatasetReducer {


    private final int originalXDimension;
    private final int originalYDimension;
    private final int originalZDimension;

    private final StatisticalReducer reducer;

    /**
     * Construct a cell reducer to reduce an amount of cells of a {@link Block}
     *
     * @param originalDimensions The original dimensions. For example, the original resolution was 8x8x8, the target
     *                           resolution will be 4x4x4
     * @param type               The strategy to reduce an amount of cells
     */
    public StatisticalDatasetReducer(Point3D originalDimensions, StatisticalReducerFactory.StatisticalReducerType type) {
        this.originalXDimension = (int) originalDimensions.getX();
        this.originalYDimension = (int) originalDimensions.getY();
        this.originalZDimension = (int) originalDimensions.getZ();

        this.reducer = StatisticalReducerFactory.getInstance().getReducer(type);
    }

    /**
     * Reduce a dataset containing int values ordered by the block id.
     *
     * @param source      The source to access the dataset. Must be open, otherwise an exception is thrown
     * @param datasetName The name of the dataset. Must exist, otherwise an Exception is thrown
     * @return A list of reduced values. The index of the value is the (block id + 1).
     * @throws Exception The source was closed, the datasetName does not exists or something went wrong while reading
     * @see #reduceFloatDataset(Flash3Reader, String)
     */
    public List<int[]> reduceIntDataset(Flash3Reader source, String datasetName) throws Exception {

        Flash3MetaData metaData = source.getMetaData();

        List<int[]> result = new ArrayList<>(metaData.getBlockCount());
        BlockTree blockTree = metaData.getBlockTree();

        for (Block block : blockTree.getAll()) {
            int[] flatOriginal = source.readIntValues(datasetName, block);
            result.add(reduceInt(flatOriginal));
        }

        return result;
    }

    private ThreadLocal<int[]> intBuffer = new ThreadLocal<int[]>() {
        @Override
        protected int[] initialValue() {
            return new int[8];
        }
    };

    private int[] reduceInt(int[] flatOriginal) {

        // TODO: Replace with variable dimension. At the moment the dimension is 2*2*2 for a half reduction
        int[] result = new int[flatOriginal.length / 8];
        int[] array = intBuffer.get();
        for (int i = 0, x = 0; x < originalXDimension; x += 2) {
            for (int y = 0; y < originalYDimension; y += 2) {
                for (int z = 0; z < originalZDimension; z += 2, i++) {

                    // The neighbors of the cell
                    array[0] = flatOriginal[index(x, y, originalYDimension, z, originalZDimension)];
                    array[1] = flatOriginal[index(x + 1, y, originalYDimension, z, originalZDimension)];
                    array[2] = flatOriginal[index(x, y + 1, originalYDimension, z, originalZDimension)];
                    array[3] = flatOriginal[index(x + 1, y + 1, originalYDimension, z, originalZDimension)];

                    array[4] = flatOriginal[index(x, y, originalYDimension, z + 1, originalZDimension)];
                    array[5] = flatOriginal[index(x + 1, y, originalYDimension, z + 1, originalZDimension)];
                    array[6] = flatOriginal[index(x, y + 1, originalYDimension, z + 1, originalZDimension)];
                    array[7] = flatOriginal[index(x + 1, y + 1, originalYDimension, z + 1, originalZDimension)];

                    result[i] = reducer.reduce(array);
                }
            }
        }

        return result;
    }

    /**
     * Reduce a dataset containing float values ordered by the block id.
     *
     * @param source      The source to access the dataset. Must be open, otherwise an exception is thrown
     * @param datasetName The name of the dataset. Must exist, otherwise an Exception is thrown
     * @return A list of reduced values. The index of the value is the (block id + 1).
     * @throws Exception The source was closed, the datasetName does not exists or something went wrong while reading
     * @see #reduceIntDataset(Flash3Reader, String)
     */
    public List<float[]> reduceFloatDataset(Flash3Reader source, String datasetName) throws Exception {
        Flash3MetaData metaData = source.getMetaData();

        List<float[]> result = new ArrayList<>(metaData.getBlockCount());
        BlockTree blockTree = metaData.getBlockTree();

        for (Block block : blockTree.getAll()) {
            float[] flatOriginal = source.readFloatValues(datasetName, block);
            result.add(reduceFloat(flatOriginal));
        }

        return result;
    }

    private ThreadLocal<float[]> floatBuffer = new ThreadLocal<float[]>() {
        @Override
        protected float[] initialValue() {
            return new float[8];
        }
    };

    private float[] reduceFloat(float[] flatOriginal) {

        // TODO: Replace with variable dimension. At the moment the dimension is 2*2*2 for a half reduction
        float[] result = new float[flatOriginal.length / 8];
        float[] array = floatBuffer.get();
        for (int i = 0, x = 0; x < originalXDimension; x += 2) {
            for (int y = 0; y < originalYDimension; y += 2) {
                for (int z = 0; z < originalZDimension; z += 2, i++) {

                    // The neighbors of the cell
                    array[0] = flatOriginal[index(x, y, originalYDimension, z, originalZDimension)];
                    array[1] = flatOriginal[index(x + 1, y, originalYDimension, z, originalZDimension)];
                    array[2] = flatOriginal[index(x, y + 1, originalYDimension, z, originalZDimension)];
                    array[3] = flatOriginal[index(x + 1, y + 1, originalYDimension, z, originalZDimension)];

                    array[4] = flatOriginal[index(x, y, originalYDimension, z + 1, originalZDimension)];
                    array[5] = flatOriginal[index(x + 1, y, originalYDimension, z + 1, originalZDimension)];
                    array[6] = flatOriginal[index(x, y + 1, originalYDimension, z + 1, originalZDimension)];
                    array[7] = flatOriginal[index(x + 1, y + 1, originalYDimension, z + 1, originalZDimension)];

                    result[i] = reducer.reduce(array);
                }
            }
        }

        return result;
    }

    /**
     * Calculate the index of the 3D array in a flatten 1D array. The order is XYZ
     *
     * @param x     The x coordinate
     * @param y     The y coordinate
     * @param ySize The size of the y dimension(the width)
     * @param z     The z coordinate
     * @param zSize The size of the z dimension(the depth)
     * @return The index in the flatten 1D array for the 3D array
     */
    private int index(int x, int y, int ySize, int z, int zSize) {
        return x * ySize * zSize + y * zSize + z;
    }
}
