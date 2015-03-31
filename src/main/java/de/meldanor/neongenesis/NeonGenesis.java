/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Kilian G�rtner
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

        Core.logger.info("Hello World, this is NeonGenesis!");

        // Check if the input exists
        File inputDirectory = new File(options.inputDirectory);
        if (!inputDirectory.exists()) {
            Core.logger.error("The input directory '" + options.inputDirectory + "' does not exist!");
            return;
        }

        // Check if the input director has files
        File[] files = inputDirectory.listFiles(pathname -> !pathname.isDirectory());
        if (files == null || files.length == 0) {
            Core.logger.error("The input directory '" + inputDirectory + "' is empty!");
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
        Core.logger.info("The reduced files will be placed in '" + outputDirectory + "'");

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
                Core.logger.error("Unknown reducer type '" + options.reduceType + "'!");
                StringBuilder tmp = new StringBuilder();
                commander.usage("-rt", tmp);
                Core.logger.info(tmp.toString());
                return;
        }
        builder.strategy(reducer);
        Core.logger.info("The strategy for reduction is '" + reducer.name() + "'.");

        // Extract the datasets to reduce - if the option was not use, reduce all
        if (options.datasetsToReduce == null || options.datasetsToReduce.isEmpty()) {
            builder.reduceAllVariableDatasets();
            Core.logger.info("No datasets to reduced specified - reduce all!");
        } else {
            builder.variableDatasetsNames(options.datasetsToReduce);
            Core.logger.info("Reduce only the following datasets: " + String.join(",", options.datasetsToReduce));
        }

        boolean isVerbose = options.verbose;
        if (isVerbose)
            Core.logger.info("Verbose mode active. Display more information about the process");

        // Create the process and invoke it
        ReductionProcess reductionProcess = builder.build();

        Core.logger.info("Start reduction process of " + files.length + " files");

        for (int i = 0; i < files.length; i++) {
            File inputFile = files[i];

            try {
                Core.logger.info("Start reducing file(" + (i + 1) + "/" + files.length + ") '" + getFileInformation(isVerbose, inputFile) + "'!");
                reductionProcess.reduceFile(inputFile);
                Core.logger.info("Finished reducing file '" + inputFile + "'!");
            } catch (Exception e) {
                Core.logger.error("An error occurred while processing file '" + inputFile + "'!");
                e.printStackTrace();
            }
        }
        Core.logger.info("Finished reduction process!");

    }

    private String getFileInformation(boolean isVerbose, File file) throws Exception {
        if (!isVerbose)
            return file.toString();
        else {
            return file.toString() + "(" + file.length() / 1024 / 1024 + " MB)";
        }
    }
}
