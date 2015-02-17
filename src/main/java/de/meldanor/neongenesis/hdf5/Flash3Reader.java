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

import ncsa.hdf.object.Dataset;

import java.io.File;
import java.util.NoSuchElementException;

/**
 * A reader for FLASH3 HDF5 files.
 *
 * @see Hdf5Reader
 */
public class Flash3Reader extends Hdf5Reader {

    private final Flash3MetaData metaData;

    /**
     * Opens the file and parse the meta data. Other data will be read by invoking the read methods
     *
     * @param file The file to open. Must exists and the file must be a FLASH3 HDF5 file
     * @throws Exception Something went wrong while reading
     * @see Hdf5Reader#Hdf5Reader(java.io.File)
     */
    public Flash3Reader(File file) throws Exception {
        super(file);
        this.metaData = new Flash3MetaData(super.getMetaData());
    }

    @Override
    public Flash3MetaData getMetaData() {
        return metaData;
    }

    /**
     * Read the values of the given dataset only for the block instead of all values.
     *
     * @param dataSetName The name of the dataset. Must hold float values
     * @param block       The block to get its values
     * @return An array containing the values
     * @throws Exception Something went wrong while reading
     * @see #readFloatValues(String)
     */
    public float[] readFloatValues(String dataSetName, Block block) throws Exception {
        Dataset dataset = metaData.getDataset(dataSetName);
        if (dataset == null)
            throw new NoSuchElementException("No dataset named '" + dataSetName + "' found!");

        dataset.init();
        selectAll(dataset);

        restrictToBlock(dataset, block);
        return (float[]) dataset.read();
    }

    /**
     * Read the values of the given dataset only for the block instead of all values.
     *
     * @param dataSetName The name of the dataset. Must hold int values
     * @param block       The block to get its values
     * @return An array containing the values
     * @throws Exception Something went wrong while reading
     * @see #readIntValues(String)
     */
    public int[] readIntValues(String dataSetName, Block block) throws Exception {
        Dataset dataset = metaData.getDataset(dataSetName);
        if (dataset == null)
            throw new NoSuchElementException("No dataset named '" + dataSetName + "' found!");

        dataset.init();
        selectAll(dataset);

        restrictToBlock(dataset, block);
        return (int[]) dataset.read();
    }

    private void restrictToBlock(Dataset dataset, Block block) {

        dataset.getSelectedDims()[0] = 1;
        dataset.getStartDims()[0] = block.getId() - 1;
    }
}
