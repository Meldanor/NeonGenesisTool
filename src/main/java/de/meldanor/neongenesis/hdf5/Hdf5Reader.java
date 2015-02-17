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
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A general reader for HDF5 files.
 * <p>
 * If the reader is closed, any access on a {@link ncsa.hdf.object.Dataset} object will result in an error. Try to
 * retrieve values or maps with it will result in an IllegalStateException
 */
public class Hdf5Reader implements Closeable {

    private static final FileFormat FILE_FORMAT = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
    private final FileFormat instance;

    private final Hdf5MetaData metaData;

    /**
     * Opens the file and parse the meta data. Other data will be read by invoking the read methods
     *
     * @param file The file to open. Must exists and the file must be a FLASH3 HDF5 file
     * @throws Exception Something went wrong while reading
     */
    public Hdf5Reader(File file) throws Exception {
        instance = FILE_FORMAT.createInstance(file.getAbsolutePath(), FileFormat.READ);
        instance.open();
        Group root = (Group) ((javax.swing.tree.DefaultMutableTreeNode) instance.getRootNode()).getUserObject();
        metaData = new Hdf5MetaData(getObjectMap(root));
    }

    /**
     * Close the reader and any access will result in IllegalStateException
     *
     * @throws IOException Will not happen, but defined by interface
     */
    @Override
    public void close() throws IOException {
        try {
            getMetaData().isClosed = true;
            instance.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private Map<String, Dataset> getObjectMap(Group root) {
        return root.getMemberList().stream().filter(o -> o instanceof Dataset).map(o -> (Dataset) o)
                .collect(Collectors.toMap(HObject::getName, Function.identity()));
    }

    /**
     * @return The files meta data
     */
    public Hdf5MetaData getMetaData() {
        return metaData;
    }

    /**
     * Read ALL values of the dataset at once and store them in ONE array. Be careful, this can be very big!
     *
     * @param dataSetName The dataset name. Must contain float values
     * @return A float array containing ALL values in once.
     * @throws Exception Something went wrong while reading
     */
    public float[] readFloatValues(String dataSetName) throws Exception {
        Dataset dataset = metaData.getDataset(dataSetName);
        if (dataset == null)
            throw new NoSuchElementException("No dataset named '" + dataSetName + "' found!");

        dataset.init();
        selectAll(dataset);
        return (float[]) dataset.read();
    }

    /**
     * Read ALL values of the dataset at once and store them in ONE array. Be careful, this can be very big!
     *
     * @param dataSetName The dataset name. Must contain int values
     * @return A float array containing ALL values in once.
     * @throws Exception Something went wrong while reading
     */
    public int[] readIntValues(String dataSetName) throws Exception {
        Dataset dataset = metaData.getDataset(dataSetName);
        if (dataset == null)
            throw new NoSuchElementException("No dataset named '" + dataSetName + "' found!");

        dataset.init();
        selectAll(dataset);
        return (int[]) dataset.read();
    }

    /**
     * Helper method to select all values of a given dataset
     *
     * @param dataset The dataset to select
     */
    protected void selectAll(Dataset dataset) {
        long[] selectedDims = dataset.getSelectedDims();
        long[] dims = dataset.getDims();
        System.arraycopy(dims, 0, selectedDims, 0, selectedDims.length);
    }
}
