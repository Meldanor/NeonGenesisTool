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

import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Datatype;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.h5.H5CompoundDS;
import ncsa.hdf.object.h5.H5File;
import ncsa.hdf.object.h5.H5ScalarDS;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * A writer for HDF5 files. The files are always without any compression or chunks to keep it simple.
 */
public class Hdf5Writer implements Closeable {

    private static final FileFormat FILE_FORMAT = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
    public static final long[] SAME_MAX_DIMENSION = null;
    public static final long[] NO_CHUNKS = null;
    public static final int NO_COMPRESSION = 0;

    private final H5File hdf5File;
    private final Group root;

    /**
     * Opens a writer to the file and create the file, if not exists.
     *
     * @param file The file to write to
     * @throws Exception Can't create or modify the file.
     */
    public Hdf5Writer(File file) throws Exception {
        this(file.getAbsolutePath());
    }

    /**
     * Opens a writer to the files path and create the file if necessary.
     *
     * @param file The file to write to.
     * @throws Exception Can't create or modify the file.
     */
    public Hdf5Writer(String file) throws Exception {
        this.hdf5File = (H5File) FILE_FORMAT.createFile(file, FileFormat.FILE_CREATE_DELETE);
        if (hdf5File == null)
            throw new IOException("Failed to create file: " + file);
        if (hdf5File.open() == -1)
            throw new IOException("Can't open file: " + file);

        root = (Group) ((javax.swing.tree.DefaultMutableTreeNode) hdf5File.getRootNode()).getUserObject();
    }

    /**
     * Create and write a single dataset of integer. As long as there is no possibility to append data to a dataset, this
     * method need the whole data at once.
     *
     * @param name        The name of the dataset. Must be unique, otherwise an error is thrown.
     * @param flattenData The flatten data. The length must match the product of the dimensions.
     * @param dimensions  The single dimensions of the data.
     * @return The created dataset.
     * @throws Exception The writer was closed or the dataset already exists or the dimensions does not fit.
     * @see #writeIntDataset(String, int[], java.util.List, long...)
     */
    public H5ScalarDS writeIntDataset(String name, int[] flattenData, long... dimensions) throws Exception {
        H5ScalarDS dataset = (H5ScalarDS) hdf5File.createScalarDS(name, root, Flash3DataTypes.INT.getDatatype(), dimensions, SAME_MAX_DIMENSION,
                NO_CHUNKS, NO_COMPRESSION, flattenData);

        if (dataset == null)
            throw new IOException("Can't write dataset '" + name + "' to file: " + hdf5File.getName());

        return dataset;
    }

    /**
     * Create and write a single dataset of integer. As long as there is no possibility to append data to a dataset, this
     * method need the whole data at once.
     * <p>
     * This method also create attributes for the dataset. An attribute is a metadata about the data, for example the
     * maximum or minimum.
     *
     * @param name        The name of the dataset. Must be unique, otherwise an error is thrown.
     * @param flattenData The flatten data. The length must match the product of the dimensions.
     * @param attributes  A list of attributes written as metadata to the dataset.
     * @param dimensions  The single dimensions of the data.
     * @return The created dataset.
     * @throws Exception The writer was closed or the dataset already exists or the dimensions does not fit.
     * @see #writeIntDataset(String, int[], long...)
     */
    public H5ScalarDS writeIntDataset(String name, int[] flattenData, List<Attribute> attributes, long... dimensions) throws Exception {
        H5ScalarDS dataset = writeIntDataset(name, flattenData, dimensions);
        for (Attribute attribute : attributes) {
            dataset.writeMetadata(attribute);
        }

        return dataset;
    }

    /**
     * Create and write a single dataset of float. As long as there is no possibility to append data to a dataset, this
     * method need the whole data at once.
     *
     * @param name        The name of the dataset. Must be unique, otherwise an error is thrown.
     * @param flattenData The flatten data. The length must match the product of the dimensions.
     * @param dimensions  The single dimensions of the data.
     * @return The created dataset.
     * @throws Exception The writer was closed or the dataset already exists or the dimensions does not fit.
     * @see #writeIntDataset(String, int[], java.util.List, long...)
     */
    public H5ScalarDS writeFloatDataset(String name, float[] flattenData, long... dimensions) throws Exception {
        H5ScalarDS dataset = (H5ScalarDS) hdf5File.createScalarDS(name, root, Flash3DataTypes.FLOAT.getDatatype(), dimensions, SAME_MAX_DIMENSION,
                NO_CHUNKS, NO_COMPRESSION, flattenData);

        if (dataset == null)
            throw new IOException("Can't write dataset '" + name + "' to file: " + hdf5File.getName());

        return dataset;
    }

    /**
     * Create and write a single dataset of float. As long as there is no possibility to append data to a dataset, this
     * method need the whole data at once.
     * <p>
     * This method also create attributes for the dataset. An attribute is a metadata about the data, for example the
     * maximum or minimum.
     *
     * @param name        The name of the dataset. Must be unique, otherwise an error is thrown.
     * @param flattenData The flatten data. The length must match the product of the dimensions.
     * @param attributes  A list of attributes written as metadata to the dataset.
     * @param dimensions  The single dimensions of the data.
     * @return The created dataset.
     * @throws Exception The writer was closed or the dataset already exists or the dimensions does not fit.
     * @see #writeIntDataset(String, int[], long...)
     */
    public H5ScalarDS writeFloatDataset(String name, float[] flattenData, List<Attribute> attributes, long... dimensions) throws Exception {
        H5ScalarDS dataset = writeFloatDataset(name, flattenData, dimensions);
        for (Attribute attribute : attributes) {
            dataset.writeMetadata(attribute);
        }

        return dataset;
    }

    /**
     * Create a compound dataset, containing different datatypes. The data is a vector containing the different data arrays.
     * For gods sake, what have they done?
     *
     * @param name        The unique name of the dataset
     * @param data        The data itself. The vectors elements must be equal arrays of the datatypes.
     * @param datatypes   The different columns of the datatypes
     * @param memberSizes The member sizes. If a datatype is an array, use the index of the datatype in this array to
     *                    specify the length of the array.
     * @param dimensions  The dimensions of the data. Important! Every compound data is one entry in the dimension.
     *                    If you have for example an integer, a float and a string and 10 entries, the dimension is 10x1,
     *                    not 10x3!
     * @return The created dataset.
     * @throws Exception The writer was closed, the dataset is already existing or the values are not matching
     */
    public H5CompoundDS writeCompound(String name, Vector<Object> data, Map<String, Datatype> datatypes, int[] memberSizes, long... dimensions) throws Exception {

        String[] memberNames = datatypes.keySet().toArray(new String[datatypes.keySet().size()]);
        Datatype[] memberDatatypes = datatypes.values().toArray(new Datatype[datatypes.values().size()]);

        H5CompoundDS dataset = (H5CompoundDS) hdf5File.createCompoundDS(name, root, dimensions, SAME_MAX_DIMENSION,
                NO_CHUNKS, NO_COMPRESSION, memberNames, memberDatatypes, memberSizes, data);

        if (dataset == null)
            throw new IOException("Can't write compound '" + name + "' to file: " + hdf5File.getName());

        return dataset;
    }

    @Override
    public void close() throws IOException {
        try {
            hdf5File.close();
        } catch (HDF5Exception e) {
            throw new IOException(e);
        }
    }
}
