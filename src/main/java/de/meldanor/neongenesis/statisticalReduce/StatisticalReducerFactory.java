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

import de.meldanor.neongenesis.downsample.ReducerType;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A factory for get {@link de.meldanor.neongenesis.statisticalReduce.StatisticalReducer}.
 *
 * @see StatisticalReducerType
 * @see de.meldanor.neongenesis.statisticalReduce.StatisticalReducer
 */
public class StatisticalReducerFactory {

    private Map<StatisticalReducerType, de.meldanor.neongenesis.statisticalReduce.StatisticalReducer> reducerMap;

    private static StatisticalReducerFactory ourInstance = new StatisticalReducerFactory();

    public static StatisticalReducerFactory getInstance() {
        return ourInstance;
    }

    private StatisticalReducerFactory() {
        this.reducerMap = new EnumMap<>(StatisticalReducerType.class);
        for (StatisticalReducerType reducer : StatisticalReducerType.values()) {
            reducerMap.put(reducer, reducer.reducerSupplier.get());
        }
    }

    /**
     * The types of reducing.
     *
     * @see de.meldanor.neongenesis.statisticalReduce.StatisticalReducer
     */
    public enum StatisticalReducerType implements ReducerType {
        /**
         * Using the mean for reducing a set.
         *
         * @see MeanReducer
         */
        MEAN(MeanReducer::new),
        /**
         * Using the median for reducing a set.
         *
         * @see MedianReducer
         */
        MEDIAN(MedianReducer::new);

        private final Supplier<de.meldanor.neongenesis.statisticalReduce.StatisticalReducer> reducerSupplier;

        StatisticalReducerType(Supplier<de.meldanor.neongenesis.statisticalReduce.StatisticalReducer> reducerSupplier) {

            this.reducerSupplier = reducerSupplier;
        }
    }

    /**
     * Get the reducer for the type. Every reducer is created once and shared, because they are immutable and contains
     * only of the functions.
     *
     * @param type The type of the reducing
     * @return The statistical reducer assigned to the type
     */
    public de.meldanor.neongenesis.statisticalReduce.StatisticalReducer getReducer(StatisticalReducerType type) {
        return reducerMap.get(type);
    }
}
