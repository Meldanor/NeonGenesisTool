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

package de.meldanor.neongenesis.other;

import de.meldanor.neongenesis.hdf5.Block;
import de.meldanor.neongenesis.hdf5.BlockTree;
import de.meldanor.neongenesis.hdf5.Flash3Reader;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class BlockTest {

    static String FILE = "D:/Studium/Bachelorarbeit/plotfiles/hvc_hdf5_plt_cnt_0000";

    @Test
    public void testReadingBlocks() throws Exception {
        Flash3Reader reader = new Flash3Reader(new File(FILE));
        BlockTree blockTree = reader.getMetaData().getBlockTree();

        List<Block> blocks = blockTree.getAll();
        // Group blocks based on the refine level (height of node in tree) without knowing the tree structure
        Map<Byte, List<Block>> levelOrderMap = blocks.stream().collect(Collectors.groupingBy(Block::getRefineLevel));

        // Create level order from the tree structure
        Map<Byte, List<Block>> levelOrder = blockTree.levelOrder().stream().collect(Collectors.groupingBy(Block::getRefineLevel));

        assertEquals("Not equal tree heights", levelOrderMap.keySet(), levelOrder.keySet());

        for (Byte key : levelOrderMap.keySet()) {
            assertEquals("Not equal tree levels at level '" + key + " '!", levelOrder.get(key), levelOrderMap.get(key));
        }
        reader.close();
    }

    @Test
    public void testToJson() throws Exception {

        Flash3Reader reader = new Flash3Reader(new File(FILE));
        BlockTree blockTree = reader.getMetaData().getBlockTree();

        String treeAsJson = BlockTreeJsonExporter.toJson(blockTree);
        assertFalse(treeAsJson.isEmpty());
        reader.close();
    }

    @Test
    public void readValuesTest() throws Exception {
        Flash3Reader reader = new Flash3Reader(new File(FILE));
        BlockTree blockTree = reader.getMetaData().getBlockTree();

        float[] floats = reader.readFloatValues("dens");
        assertTrue(floats.length > 1);
        Block block = blockTree.get(2);
        floats = reader.readFloatValues("velx", block);
        assertTrue(floats.length > 1);
        reader.close();
    }

    @Test(expected = IllegalStateException.class)
    public void readFromClosedReader() throws Exception {
        Flash3Reader reader = new Flash3Reader(new File(FILE));

        reader.close();

        reader.getMetaData().getVariableMap();
    }


    // Disable ignore to generate the tree and display it via 'src/test/resources/de/meldanor/neongenesis/other/tree.html'
    @Ignore
    @Test
    public void toJson() throws Exception {

        Flash3Reader reader = new Flash3Reader(new File(FILE));
        BlockTree blockTree = reader.getMetaData().getBlockTree();
        PrintWriter writer = new PrintWriter(Paths.get("src", "test", "resources", "de", "meldanor", "neongenesis", "other", "tree.json").toFile());
        writer.print(BlockTreeJsonExporter.toJson(blockTree));
        writer.flush();
        writer.close();
        reader.close();
    }

}