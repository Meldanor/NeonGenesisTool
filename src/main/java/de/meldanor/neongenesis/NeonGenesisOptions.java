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

package de.meldanor.neongenesis;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import java.util.List;

/**
 * Class for command line bases options. Values are  parsed via JCommander from the command line.
 */
public class NeonGenesisOptions {

    @Parameter(names = {"-id", "--inputDirectory"},
            description = "The directory containing the files to reduce. Must exist",
            required = true)
    String inputDirectory;

    @Parameter(names = {"-od", "--outputDirectory"},
            description = "The directory to save the reduced files. Will created if it does not exist",
            required = true)
    String outputDirecoty;

    @Parameter(names = {"-rt", "--reduceType"},
            description = "The reduce algorithm.")
    String reduceType = "mean";


    @Parameter(names = {"-v", "--verbose"},
            description = "Show more information about the file to reduce.")
    boolean verbose = false;

    @Parameter(names = {"-ds", "--datasets"},
            description = "A list of datasets to reduce. If not used, all datasets are to be reduced",
            variableArity = true)
    List<String> datasetsToReduce;

    @SuppressWarnings("unused")
    @Parameter(names = {"-h", "--help"},
            description = "Display this help",
            help = true)
    private boolean help;

    public NeonGenesisOptions() {
    }

    // Just to generate the help text
    @Deprecated
    public static void main(String[] args) {
        new JCommander(new NeonGenesisOptions(), "-h").usage();
    }
}
