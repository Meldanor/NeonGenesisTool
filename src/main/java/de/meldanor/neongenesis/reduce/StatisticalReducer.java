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

/**
 * Reduce a set of values to a single value based on statistical algorithm.
 *
 * @apiNote The algorithm implementation should be immutable and thread-safe.
 * <p>
 * It is not necessary to have a public constructor, default visibility is enough,
 * because they are created by {@link ReducerFactory}.
 * @see ReducerFactory
 */
public interface StatisticalReducer {

    /**
     * Reduce an array of integer to one integer. 
     *
     * @param array The values
     * @return The reduced value
     * @implNote The reduced value, if calculated with real numbers, MUST be rounded and not only casted.
     */
    public int reduce(int[] array);

    /**
     * Reduce an array of floats to one float.
     *
     * @param array The values
     * @return The reduced value
     */
    public float reduce(float[] array);
}
