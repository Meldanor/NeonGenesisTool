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

import javafx.geometry.Point3D;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BlockReader {

    private static final FileFormat FILE_FORMAT = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);

    private enum BlockTable {
        BLOCK_SIZE("block size"),
        BOUNDING_BOX("bounding box"),
        COORDINATES("coordinates"),
        REFINE_LEVEL("refine level"),
        B_FLAGS("bflags"),
        WHICH_CHILD("which child"),
        GID("gid");

        private final String name;

        BlockTable(String name) {
            this.name = name;
        }
    }

    public BlockReader() {

    }

    public BlockTree readBlocks(File file) throws Exception {

        FileFormat instance = FILE_FORMAT.createInstance(file.getAbsolutePath(), FileFormat.READ);
        instance.open();
        Group root = (Group) ((javax.swing.tree.DefaultMutableTreeNode) instance.getRootNode()).getUserObject();
        Map<String, Dataset> objectMap = getObjectMap(root);
        BlockTree tree = constructBlockTree(objectMap);

        fillAttributes(objectMap, tree);
        instance.close();
        return tree;
    }

    private BlockTree constructBlockTree(Map<String, Dataset> objectMap) throws Exception {
        Dataset dataset = objectMap.get(BlockTable.GID.name);
        dataset.init();

        // Skip the neighbor dimensions
        dataset.getStartDims()[1] = 6;
        // Select only the parent(1) and children dimensions(8) 
        dataset.getSelectedDims()[1] = 9;

        int[] data = (int[]) dataset.read();
        return new BlockTree(data, dataset.getHeight());
    }

    private Map<String, Dataset> getObjectMap(Group root) {
        return root.getMemberList().stream().filter(o -> o instanceof Dataset).map(o -> (Dataset) o)
                .collect(Collectors.toMap(HObject::getName, Function.identity()));
    }

    private void fillAttributes(Map<String, Dataset> datasetMap, BlockTree tree) throws Exception {
        fillBlockSize(datasetMap.get(BlockTable.BLOCK_SIZE.name), tree);
        fillBoundingBox(datasetMap.get(BlockTable.BOUNDING_BOX.name), tree);
        fillRefineLevel(datasetMap.get(BlockTable.REFINE_LEVEL.name), tree);
        fillCoordinates(datasetMap.get(BlockTable.COORDINATES.name), tree);
        fillBFlags(datasetMap.get(BlockTable.B_FLAGS.name), tree);
        fillWhichChild(datasetMap.get(BlockTable.WHICH_CHILD.name), tree);
        fillNeighbors(datasetMap.get(BlockTable.GID.name), tree);
    }

    private void fillBlockSize(Dataset set, BlockTree builderMap) throws Exception {
        set.init();
        // Restrict values to one dimension
        // The block size is equal in every dimension, so we need only a third of memory
        set.getSelectedDims()[1] = 1;
        float[] data = (float[]) set.read();
        for (int i = 0; i < data.length; ++i) {
            builderMap.get(i + 1).setBlockSize(data[i]);
        }
    }

    private void fillBoundingBox(Dataset set, BlockTree builderMap) throws Exception {
        set.init();
        set.getSelectedDims()[1] = 3;
        float[] data = (float[]) set.read();
        for (int i = 0, j = 0; i < data.length; i = i + 3, ++j) {
            builderMap.get(j + 1).setBoundingBox(new Point3D(data[i], data[i + 1], data[i + 2]));
        }
    }

    private void fillRefineLevel(Dataset set, BlockTree builderMap) throws Exception {
        set.init();
        int[] data = (int[]) set.read();
        for (int i = 0; i < data.length; ++i) {
            builderMap.get(i + 1).setRefineLevel((byte) data[i]);
        }
    }

    private void fillCoordinates(Dataset set, BlockTree builderMap) throws Exception {
        set.init();
        set.getSelectedDims()[1] = 3;
        float[] data = (float[]) set.read();
        for (int i = 0, j = 0; i < data.length; i = i + 3, ++j) {
            builderMap.get(j + 1).setCoordinates(new Point3D(data[i], data[i + 1], data[i + 2]));
        }
    }


    private void fillBFlags(Dataset set, BlockTree builderMap) throws Exception {
        set.init();
        int[] data = (int[]) set.read();
        for (int i = 0; i < data.length; ++i) {
            builderMap.get(i + 1).setBflags((byte) data[i]);
        }
    }

    private void fillWhichChild(Dataset set, BlockTree builderMap) throws Exception {
        set.init();
        int[] data = (int[]) set.read();
        for (int i = 0; i < data.length; ++i) {
            builderMap.get(i + 1).setWhichChild((byte) data[i]);
        }
    }

    private void fillNeighbors(Dataset set, BlockTree tree) throws Exception {
        set.init();
        // Select only the neighbors(6)
        set.getSelectedDims()[1] = 6;
        int[] data = (int[]) set.read();
        int height = set.getHeight();
        for (int i = 0; i < height; i++) {
            Block b = tree.get(i + 1);
            List<Block> neighbors = new ArrayList<>(6);
            for (int j = 0; j < 6; ++j) {
                int neighborId = data[(i * 6) + j];
                if (neighborId > 0)
                    neighbors.add(tree.get(neighborId));
                else
                    neighbors.add(null);
            }
            b.setNeighbors(neighbors);
        }
    }
}
