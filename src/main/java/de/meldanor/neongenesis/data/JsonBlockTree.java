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

package de.meldanor.neongenesis.data;

import jodd.json.JsonWriter;

import java.util.List;

/**
 *
 */
public class JsonBlockTree {

    public static String toJson(BlockTree tree) {

        StringBuilder builder = new StringBuilder();
        JsonWriter writer = new JsonWriter(builder);
        toJson(writer, tree, tree.get(1));

        return builder.toString();
    }

    private static void toJson(JsonWriter writer, BlockTree tree, Block node) {
        writer.writeOpenObject();
        {
            writer.writeName("name");
            writer.writeString(String.valueOf(node.getId()));
            Block parent = tree.getParentOf(node);
            if (parent != null) {
                writer.writeComma();
                writer.writeName("parent");
                writer.writeString(String.valueOf(parent.getId()));
            }
            List<Block> children = tree.getChildrenOf(node);
            if (!children.isEmpty()) {
                writer.writeComma();
                writer.writeName("children");
                writer.writeOpenArray();
                for (int i = 0; i < children.size(); i++) {
                    toJson(writer, tree, children.get(i));
                    if (i < children.size() - 1)
                        writer.writeComma();
                }
                writer.writeCloseArray();
            }
        }
        writer.writeCloseObject();
    }
}
