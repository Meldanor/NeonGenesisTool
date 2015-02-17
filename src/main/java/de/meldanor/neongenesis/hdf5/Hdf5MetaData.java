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

import java.util.Collections;
import java.util.Map;

/**
 * A general class for HDF5 files metadata describing the datasets
 */
public class Hdf5MetaData {

    private Map<String, Dataset> datasetMap;

    boolean isClosed = false;

    public Hdf5MetaData(Map<String, Dataset> datasetMap) {
        this.datasetMap = Collections.unmodifiableMap(datasetMap);
    }

    /**
     * Will result in an IllegalStateException if the reader was closed
     *
     * @return an unmodifiable version of the dataset map
     */
    Map<String, Dataset> getDatasetMap() {
        if (isClosed)
            throw new IllegalStateException("Reader was closed, no access to datasets!");
        return datasetMap;
    }

    /**
     * Will result in an IllegalStateException if the reader was closed
     *
     * @return The given dataset
     */
    Dataset getDataset(String datasetName) {
        if (isClosed)
            throw new IllegalStateException("Reader was closed, no access to datasets!");
        return datasetMap.get(datasetName);
    }


}
