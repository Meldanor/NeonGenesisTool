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

package de.meldanor.neongenesis.reduce;

import de.meldanor.neongenesis.hdf5.Flash3Reader;
import javafx.geometry.Point3D;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class CellReducerTest {

    private static final String X_DIMENSION = "nxb";
    private static final String Y_DIMENSION = "nyb";
    private static final String Z_DIMENSION = "nzb";

    private static final String FILE = "D:/Studium/Bachelorarbeit/plotfiles/hvc_hdf5_plt_cnt_0000";
    private Flash3Reader source;

    private Point3D sourceDimension;

    @Rule
    public ExternalResource externalResource = new ExternalResource() {
        @Override
        protected void before() throws Throwable {
            source = new Flash3Reader(new File(FILE));

            int xDimension = source.getMetaData().getIntegerSclar(X_DIMENSION);
            int yDimension = source.getMetaData().getIntegerSclar(Y_DIMENSION);
            int zDimension = source.getMetaData().getIntegerSclar(Z_DIMENSION);
            sourceDimension = new Point3D(xDimension, yDimension, zDimension);
        }

        @Override
        protected void after() {
            try {
                source.close();
            } catch (IOException ignore) {
            }
        }
    };

    @Test
    public void testSingleFloatDataset() throws Exception {
        CellReducer reducer = new CellReducer(sourceDimension, ReducerFactory.Reducer.MEAN);
        List<float[]> dens = reducer.reduceFloatDataset(source, "dens");
        assertTrue(dens.size() > 1);
    }

    @Test
    public void testAllFloatDataset() throws Exception {
        CellReducer reducer = new CellReducer(sourceDimension, ReducerFactory.Reducer.MEAN);
        List<String> datasets = Arrays.asList("dens", "eint", "ener", "gpot", "metl", "ms_h", "ms_i", "pres", "temp", "velx", "vely", "velz");
        for (String dataset : datasets) {
            List<float[]> result = reducer.reduceFloatDataset(source, dataset);
            assertTrue(result.size() > 1);
        }
    }
}