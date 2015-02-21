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

import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.Datatype;
import ncsa.hdf.object.h5.H5ScalarDS;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class Hdf5WriterTest {

    public static final String DATASET_2D = "2DArray 20x10";
    public static final String DATASET_3D = "3DArray 20x10x5";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testWriteIntDataset() throws Exception {
        File tmp = temporaryFolder.newFile();
        Hdf5Writer writer = new Hdf5Writer(tmp);
        int[] array2D = new int[20 * 10];
        // 2D Array
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                array2D[i * 10 + j] = 1000 + i * 100 + j;
            }
        }
        writer.writeIntDataset(DATASET_2D, array2D, 20L, 10L);

        int[] array3D = new int[20 * 10 * 5];
        // 3D Array
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                for (int k = 0; k < 5; k++) {
                    array3D[i * 10 * 5 + j * 5] = 1000 + i * 100 + j * 50 + k;
                }

            }
        }
        writer.writeIntDataset(DATASET_3D, array3D, 20L, 10L, 5L);
        writer.close();

        Hdf5Reader reader = new Hdf5Reader(tmp);
        int[] readValues;
        readValues = reader.readIntValues(DATASET_2D);
        assertArrayEquals(array2D, readValues);

        readValues = reader.readIntValues(DATASET_3D);
        assertArrayEquals(array3D, readValues);
        reader.close();
    }

    @Test
    public void testWriteFloatDataset() throws Exception {
        File tmp = temporaryFolder.newFile();
        Hdf5Writer writer = new Hdf5Writer(tmp);
        float[] array2D = new float[20 * 10];
        // 2D Array
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                array2D[i * 10 + j] = 1000.0F + i * 100.0F + j;
            }
        }
        writer.writeFloatDataset(DATASET_2D, array2D, 20L, 10L);

        float[] array3D = new float[20 * 10 * 5];
        // 3D Array
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                for (int k = 0; k < 5; k++) {
                    array3D[i * 10 * 5 + j * 5] = 1000.0F + i * 100.0F + j * 50.0F + k;
                }

            }
        }
        writer.writeFloatDataset(DATASET_3D, array3D, 20L, 10L, 5L);
        writer.close();

        Hdf5Reader reader = new Hdf5Reader(tmp);
        float[] readValues;
        readValues = reader.readFloatValues(DATASET_2D);
        assertArrayEquals(array2D, readValues, 0.0F);

        readValues = reader.readFloatValues(DATASET_3D);
        assertArrayEquals(array3D, readValues, 0.0F);
        reader.close();
    }

    @Test
    public void writeAttributeDataset() throws Exception {
        File tmp = temporaryFolder.newFile();
        Hdf5Writer writer = new Hdf5Writer(tmp);

        int[] intArray2D = new int[20 * 10];
        // 2D Array
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                intArray2D[i * 10 + j] = 1000 + i * 100 + j;
            }
        }

        long[] dimension = {1L};

        int maxInt = IntStream.of(intArray2D).max().getAsInt();
        int minInt = IntStream.of(intArray2D).min().getAsInt();

        List<Attribute> attributes = new ArrayList<>(2);
        attributes.add(new Attribute("maximum", Flash3DataTypes.INT.getDatatype(), dimension));
        attributes.get(0).setValue(new int[]{maxInt});

        attributes.add(new Attribute("minimum", Flash3DataTypes.INT.getDatatype(), dimension));
        attributes.get(1).setValue(new int[]{minInt});

        writer.writeIntDataset(DATASET_2D + "int", intArray2D, attributes, 20, 10);

        float[] floatArray2D = new float[20 * 10];
        // 2D Array
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                floatArray2D[i * 10 + j] = 1000.0F + i * 100.0F + j;
            }
        }

        float maxFloat = (float) IntStream.range(0, floatArray2D.length).mapToDouble(i -> floatArray2D[i]).max().getAsDouble();
        float minFloat = (float) IntStream.range(0, floatArray2D.length).mapToDouble(i -> floatArray2D[i]).min().getAsDouble();

        attributes = new ArrayList<>(2);
        attributes.add(new Attribute("maximum", Flash3DataTypes.FLOAT.getDatatype(), dimension));
        attributes.get(0).setValue(new float[]{maxFloat});

        attributes.add(new Attribute("minimum", Flash3DataTypes.FLOAT.getDatatype(), dimension));
        attributes.get(1).setValue(new float[]{minFloat});

        writer.writeFloatDataset(DATASET_2D + "float", floatArray2D, attributes, 20, 10);
        writer.close();

        Hdf5Reader reade = new Hdf5Reader(tmp);
        H5ScalarDS dataset = (H5ScalarDS) reade.getMetaData().getDataset(DATASET_2D + "int");
        dataset.init();

        for (Attribute attribute : dataset.getMetadata()) {
            if ("maximum".equals(attribute.getName())) {
                assertEquals(maxInt, ((int[]) attribute.getValue())[0]);
            } else if ("minimum".equals(attribute.getName())) {
                assertEquals(minInt, ((int[]) attribute.getValue())[0]);
            } else {
                fail("Unknown attribute " + attribute);
            }
        }

        reade.close();
    }

    @Test
    public void writeCompoundDataset() throws Exception {

        File tmp = temporaryFolder.newFile();
        Hdf5Writer writer = new Hdf5Writer(tmp);

        Map<String, Integer> values = new LinkedHashMap<>();
        IntStream.range(0, 10).forEach(i -> values.put("Number -> " + String.valueOf(i), (i * i)));

        Vector<Object> vector = new Vector<>();
        vector.add(values.keySet().toArray(new String[values.keySet().size()]));

        vector.add(values.values().toArray(new Integer[values.values().size()]));

        Map<String, Datatype> datatypes = new HashMap<>();
        datatypes.put("name", Flash3DataTypes.STRING.getDatatype());
        datatypes.put("value", Flash3DataTypes.STRING.getDatatype());

        int[] membersizes = {1, 1};
        writer.writeCompound("integer scalars", vector, datatypes, membersizes, values.size(), 1);

        writer.close();
    }
}