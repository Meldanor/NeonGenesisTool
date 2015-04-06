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

import de.meldanor.neongenesis.physicalReduce.PhysicalReducerType;
import de.meldanor.neongenesis.physicalReduce.PhysicalReductionProcess;
import de.meldanor.neongenesis.statisticalReduce.StatisticalReducerFactory;
import de.meldanor.neongenesis.statisticalReduce.StatisticalReductionProcess;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Used to create an {@link StatisticalReductionProcess}.
 *
 * @see StatisticalReductionProcess
 */
public class ReductionProcessBuilder {

    private ReducerType strategy;
    private List<String> variableDatasetsNames;

    private File targetDirectory;

    private ReductionProcessBuilder() {
        this.strategy = StatisticalReducerFactory.StatisticalReducerType.MEDIAN;
    }

    public static ReductionProcessBuilder create() {
        return new ReductionProcessBuilder();
    }

    public ReductionProcessBuilder strategy(ReducerType strategy) {
        this.strategy = strategy;
        return this;
    }

    public ReductionProcessBuilder variableDatasetsNames(List<String> names) {
        this.variableDatasetsNames = names;
        return this;
    }

    public ReductionProcessBuilder reduceAllVariableDatasets() {
        return variableDatasetsNames(null);
    }

    public ReductionProcessBuilder outputDirectory(File targetDirectory) {
        this.targetDirectory = targetDirectory;
        return this;
    }

    public AbstractReductionProcess build() {
        File targetDirectory = this.targetDirectory;
        if (targetDirectory == null)
            targetDirectory = new File(".");
        else if (!targetDirectory.exists()) {
            //noinspection ResultOfMethodCallIgnored
            targetDirectory.mkdirs();
        }
        if (strategy instanceof StatisticalReducerFactory.StatisticalReducerType)
            return new StatisticalReductionProcess((StatisticalReducerFactory.StatisticalReducerType) strategy,
                    (variableDatasetsNames == null ? Collections.emptyList() : variableDatasetsNames),
                    targetDirectory
            );
        else if (strategy instanceof PhysicalReducerType) {


            if (strategy == PhysicalReducerType.PHYSICAL_MEAN)
                strategy = StatisticalReducerFactory.StatisticalReducerType.MEAN;
            else if (strategy == PhysicalReducerType.PHYSICAL_MEDIAN)
                strategy = StatisticalReducerFactory.StatisticalReducerType.MEDIAN;
            else
                throw new IllegalArgumentException("Unknown strategy " + strategy);

            return new PhysicalReductionProcess((StatisticalReducerFactory.StatisticalReducerType) strategy,
                    (variableDatasetsNames == null ? Collections.emptyList() : variableDatasetsNames),
                    targetDirectory
            );
        }
        else {
            throw new IllegalArgumentException("Unknown strategy " + strategy);
        }

    }


}
