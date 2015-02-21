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

import ncsa.hdf.object.Datatype;
import ncsa.hdf.object.h5.H5Datatype;

/**
 * Constants of used {@link ncsa.hdf.object.Datatype} in the project corresponding the java types.
 */
public enum Flash3DataTypes {

    /**
     * Describes an integer
     */
    INT(new H5Datatype(Datatype.CLASS_INTEGER, Integer.BYTES, Datatype.NATIVE, Datatype.NATIVE)),
    /**
     * Describes a float
     */
    FLOAT(new H5Datatype(Datatype.CLASS_FLOAT, Float.BYTES, Datatype.NATIVE, Datatype.NATIVE)),
    /**
     * Describes a string of undefined length
     */
    STRING(new H5Datatype(Datatype.CLASS_STRING, -1, -1, -1));

    private final Datatype datatype;

    Flash3DataTypes(Datatype datatype) {
        this.datatype = datatype;
    }

    public Datatype getDatatype() {
        return datatype;
    }
}
