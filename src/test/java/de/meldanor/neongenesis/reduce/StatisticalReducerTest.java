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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StatisticalReducerTest {

    @Test
    public void testMedianReducer() {
        StatisticalReducer reducer = ReducerFactory.getInstance().getReducer(ReducerFactory.Reducer.MEDIAN);
        int[] intArray = {1, 2, 3, 4, 5, 6};
        assertEquals(4, reducer.reduce(intArray));

        float[] floatArray = {1.0F, 2.0F, 3.0f, 4.0F, 5.0F, 6.0F};
        assertEquals(3.5F, reducer.reduce(floatArray), 0.1F);
        
        intArray = new int[]{1};
        assertEquals(1, reducer.reduce(intArray));
    }

    @Test
    public void testMeanReducer() {
        StatisticalReducer reducer = ReducerFactory.getInstance().getReducer(ReducerFactory.Reducer.MEAN);
        int[] intArray = {100, 2, 3, 4, 5, 6};
        assertEquals(20, reducer.reduce(intArray));

        float[] floatArray = {100.0F, 2.0F, 3.0f, 4.0F, 5.0F, 6.0F};
        assertEquals(20.0F, reducer.reduce(floatArray), 0.1F);

        intArray = new int[]{1};
        assertEquals(1, reducer.reduce(intArray));
    }

}