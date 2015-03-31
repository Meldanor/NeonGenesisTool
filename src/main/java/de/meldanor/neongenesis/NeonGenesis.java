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
import de.meldanor.neongenesis.downsample.ReductionProcess;
import de.meldanor.neongenesis.downsample.ReductionProcessBuilder;
import de.meldanor.neongenesis.reduce.ReducerFactory;

import java.io.File;

/**
 * The actually program
 */
public class NeonGenesis {

    public NeonGenesis(NeonGenesisOptions options, JCommander commander) {

        System.out.println("Hello World, this is NeonGenesis!");

        // Check if the input exists
        File inputDirectory = new File(options.inputDirectory);
        if (!inputDirectory.exists()) {
            System.err.println("The input directory '" + options.inputDirectory + "' does not exist!");
            return;
        }

        // Check if the input director has files
        File[] files = inputDirectory.listFiles(pathname -> !pathname.isDirectory());
        if (files == null || files.length == 0) {
            System.err.println("The input directory '" + inputDirectory + "' is empty!");
            return;
        }

        ReductionProcessBuilder builder = ReductionProcessBuilder.create();

        // Get the target directory
        File outputDirectory = new File(options.outputDirecoty);
        if (!outputDirectory.exists()) {
            //noinspection ResultOfMethodCallIgnored
            outputDirectory.mkdirs();
        }
        builder.outputDirectory(outputDirectory);
        System.out.println("The reduced files will be placed in '" + outputDirectory + "'");

        // Get the type
        // TODO: Use factory pattern instead switch
        // TODO: Implement physically based ones
        ReducerFactory.Reducer reducer;
        switch (options.reduceType.toLowerCase()) {
            case "median":
                reducer = ReducerFactory.Reducer.MEDIAN;
                break;
            case "mean":
                reducer = ReducerFactory.Reducer.MEAN;
                break;
            // Unknown type
            default:
                System.err.println("Unknown reducer type '" + options.reduceType + "'!");
                StringBuilder tmp = new StringBuilder();
                commander.usage("-rt", tmp);
                System.err.println(tmp.toString());
                return;
        }
        builder.strategy(reducer);
        System.out.println("The strategy for reduction is '" + reducer.name() + "'.");

        // Extract the datasets to reduce - if the option was not use, reduce all
        if (options.datasetsToReduce == null || options.datasetsToReduce.isEmpty()) {
            builder.reduceAllVariableDatasets();
            System.out.println("No datasets to reduced specified - reduce all!");
        } else {
            builder.variableDatasetsNames(options.datasetsToReduce);
            System.out.println("Reduce only the following datasets: " + String.join(",", options.datasetsToReduce));
        }

        boolean isVerbose = options.verbose;
        if (isVerbose)
            System.out.println("Verbose mode active. Display more information about the process");

        // Create the process and invoke it
        ReductionProcess reductionProcess = builder.build();

        System.out.println("Start reduction process of " + files.length + " files");

        for (int i = 0; i < files.length; i++) {
            File inputFile = files[i];

            try {
                System.out.println("Start reducing file(" + (i + 1) + "/" + files.length + ") '" + getFileInformation(isVerbose, inputFile) + "'!");
                reductionProcess.reduceFile(inputFile);
                System.out.println("Finished reducing file '" + inputFile + "'!");
            } catch (Exception e) {
                System.err.println("An error occurred while processing file '" + inputFile + "'!");
                e.printStackTrace();
            }
        }
        System.out.println("Finished reduction process!");

    }

    private String getFileInformation(boolean isVerbose, File file) throws Exception {
        if (!isVerbose)
            return file.toString();
        else {

            return file.toString() + "(" + file.length() / 1024 / 1024 + " MB)";
        }
    }
}
