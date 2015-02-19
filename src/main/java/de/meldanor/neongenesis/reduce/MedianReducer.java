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

import java.util.Arrays;

/**
 * Reduce an array to its median. The median is defined as the middle of a sorted set. If the set has even members and
 * has no unique middle, the mean is calculate from the middle element and its predecessor.
 *
 * @apiNote The median is calculated by sorting the Array with {@link Arrays#sort}.
 * <p>
 * If the integer array is even, the middle will be rounded by {@link Math#round}.
 */
public class MedianReducer implements StatisticalReducer {

    MedianReducer() {
    }

    @Override
    public int reduce(int[] array) {
        Arrays.sort(array);
        int middle = array.length / 2;
        if (array.length % 2 == 1)
            return array[middle];
        else
            return (int) Math.round((array[middle] + array[middle - 1]) / 2.0);
    }

    @Override
    public float reduce(float[] array) {
        Arrays.sort(array);
        int middle = array.length / 2;
        if (array.length % 2 == 1)
            return array[middle];
        else
            return (array[middle] + array[middle - 1]) / 2.0F;
    }
}
