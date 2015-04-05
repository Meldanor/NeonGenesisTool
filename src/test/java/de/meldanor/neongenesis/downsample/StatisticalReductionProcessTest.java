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

package de.meldanor.neongenesis.downsample;

import de.meldanor.neongenesis.statisticalReduce.StatisticalReducerFactory;
import de.meldanor.neongenesis.statisticalReduce.StatisticalReductionProcess;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

public class StatisticalReductionProcessTest {

    static String FILE = "D:/Studium/Bachelorarbeit/plotfiles/hvc_hdf5_plt_cnt_0000";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    
    @Ignore(value = "This is a long running test for performance testing")
    @Test
    public void testReduceFile() throws Exception {
        ReductionProcessBuilder builder = ReductionProcessBuilder.create();
        StatisticalReductionProcess build = builder.reduceAllVariableDatasets()
                .strategy(StatisticalReducerFactory.StatisticalReducerType.MEAN)
                .outputDirectory(folder.newFolder())
                .build();

        File f = new File(FILE);
        File directory = f.getParentFile();
        if (directory == null)
            throw new NullPointerException();

        File[] plotFiles = directory.listFiles();
        if (plotFiles == null)
            throw new NullPointerException();

        for (File plotFile : plotFiles) {
            build.reduceFile(plotFile);
        }
    }
}