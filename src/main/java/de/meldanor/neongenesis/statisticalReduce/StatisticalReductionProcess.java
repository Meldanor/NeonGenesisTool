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

package de.meldanor.neongenesis.statisticalReduce;

import de.meldanor.neongenesis.downsample.AbstractReductionProcess;
import de.meldanor.neongenesis.hdf5.Flash3Reader;
import de.meldanor.neongenesis.hdf5.Hdf5Writer;
import de.meldanor.neongenesis.statisticalReduce.StatisticalDatasetReducer;
import de.meldanor.neongenesis.statisticalReduce.StatisticalReducerFactory;
import javafx.geometry.Point3D;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.Datatype;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An object to reduce a certain dataset for lower latency.
 * <p>
 * The class is thread-safe!
 *
 * @see de.meldanor.neongenesis.downsample.ReductionProcessBuilder
 */
public class StatisticalReductionProcess extends AbstractReductionProcess {

    StatisticalReductionProcess(StatisticalReducerFactory.StatisticalReducerType strategy, List<String> variableDatasetsNames, File targetDirectory) {
        super(variableDatasetsNames, targetDirectory, strategy);
    }

    @Override
    protected void reduceDatasets(StatisticalDatasetReducer reducer, Flash3Reader source, Hdf5Writer destination) throws Exception {
        Map<String, Dataset> variableMap = source.getMetaData().getVariableMap();

        Point3D dimension = getDimension(source.getMetaData());
        int xDim = (int) (dimension.getX() / 2);
        int yDim = (int) (dimension.getY() / 2);
        int zDim = (int) (dimension.getZ() / 2);

        Buffer buffer = new Buffer(source.getMetaData(), xDim, yDim, zDim);

        List<String> datasetsToReduce = this.variableDatasetsNames;
        if (datasetsToReduce.isEmpty())
            datasetsToReduce = new ArrayList<>(variableMap.keySet());

        for (String dataset : datasetsToReduce) {
            reduceDataset(dataset, reducer, source, destination, buffer);
        }

    }

    private void reduceDataset(String datasetName, StatisticalDatasetReducer reducer, Flash3Reader source, Hdf5Writer destination, Buffer buffer) throws Exception {
        Dataset dataset = source.getMetaData().getDataset(datasetName);
        dataset.init();
        switch (dataset.getDatatype().getDatatypeClass()) {
            case Datatype.CLASS_FLOAT:
                List<float[]> floats = reducer.reduceFloatDataset(source, datasetName);
                writeReducedFloatDataset(floats, datasetName, source, destination, buffer);
                break;
            case Datatype.CLASS_INTEGER:
                List<int[]> ints = reducer.reduceIntDataset(source, datasetName);
                writeReducedIntDataset(ints, datasetName, source, destination, buffer);
                break;
            default:
                throw new IllegalArgumentException("Unsupported datatype found while reducing! Datatype: " + dataset.getDatatype());
        }
    }

}
