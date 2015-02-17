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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BlockReader implements Closeable {

    private static final FileFormat FILE_FORMAT = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
    private Map<String, Dataset> datasetMap;
    private final FileFormat instance;


    private enum BlockTable {
        BLOCK_SIZE("block size"),
        BOUNDING_BOX("bounding box"),
        COORDINATES("coordinates"),
        REFINE_LEVEL("refine level"),
        B_FLAGS("bflags"),
        WHICH_CHILD("which child"),
        NODE_TYPE("node type"),
        GID("gid");

        private final String name;

        BlockTable(String name) {
            this.name = name;
        }
    }

    public BlockReader(File file) throws Exception {
        instance = FILE_FORMAT.createInstance(file.getAbsolutePath(), FileFormat.READ);
        instance.open();
        Group root = (Group) ((javax.swing.tree.DefaultMutableTreeNode) instance.getRootNode()).getUserObject();
        datasetMap = getObjectMap(root);
    }

    @Override
    public void close() throws IOException {
        try {
            instance.close();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public BlockTree readBlocks() throws Exception {
        BlockTree tree = constructBlockTree(datasetMap);
        fillAttributes(tree);
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

    private void fillAttributes(BlockTree tree) throws Exception {
        fillBlockSize(tree);
        fillBoundingBox(tree);
        fillRefineLevel(tree);
        fillCoordinates(tree);
        fillBFlags(tree);
        fillWhichChild(tree);
        fillNodeType(tree);
        fillNeighbors(tree);
    }

    private void fillBlockSize(BlockTree blockTree) throws Exception {
        Dataset set = datasetMap.get(BlockTable.BLOCK_SIZE.name);
        set.init();
        // Restrict values to one dimension
        // The block size is equal in every dimension, so we need only a third of memory
        set.getSelectedDims()[1] = 1;
        float[] data = (float[]) set.read();
        for (int i = 0; i < data.length; ++i) {
            blockTree.get(i + 1).setBlockSize(data[i]);
        }
    }

    private void fillBoundingBox(BlockTree blockTree) throws Exception {
        Dataset set = datasetMap.get(BlockTable.BOUNDING_BOX.name);
        set.init();
        set.getSelectedDims()[1] = 3;
        float[] data = (float[]) set.read();
        for (int i = 0, j = 0; i < data.length; i = i + 3, ++j) {
            blockTree.get(j + 1).setBoundingBox(new Point3D(data[i], data[i + 1], data[i + 2]));
        }
    }

    private void fillRefineLevel(BlockTree blockTree) throws Exception {
        Dataset set = datasetMap.get(BlockTable.REFINE_LEVEL.name);
        set.init();
        int[] data = (int[]) set.read();
        for (int i = 0; i < data.length; ++i) {
            blockTree.get(i + 1).setRefineLevel((byte) data[i]);
        }
    }

    private void fillCoordinates(BlockTree blockTree) throws Exception {
        Dataset set = datasetMap.get(BlockTable.COORDINATES.name);
        set.init();
        set.getSelectedDims()[1] = 3;
        float[] data = (float[]) set.read();
        for (int i = 0, j = 0; i < data.length; i = i + 3, ++j) {
            blockTree.get(j + 1).setCoordinates(new Point3D(data[i], data[i + 1], data[i + 2]));
        }
    }

    private void fillNodeType(BlockTree blockTree) throws Exception {
        Dataset set = datasetMap.get(BlockTable.NODE_TYPE.name);
        set.init();
        int[] data = (int[]) set.read();
        for (int i = 0; i < data.length; ++i) {
            blockTree.get(i + 1).setNodeType((byte) data[i]);
        }
    }

    private void fillBFlags(BlockTree blockTree) throws Exception {
        Dataset set = datasetMap.get(BlockTable.B_FLAGS.name);
        set.init();
        int[] data = (int[]) set.read();
        for (int i = 0; i < data.length; ++i) {
            blockTree.get(i + 1).setBflags((byte) data[i]);
        }
    }

    private void fillWhichChild(BlockTree blockTree) throws Exception {
        Dataset set = datasetMap.get(BlockTable.WHICH_CHILD.name);
        set.init();
        int[] data = (int[]) set.read();
        for (int i = 0; i < data.length; ++i) {
            blockTree.get(i + 1).setWhichChild((byte) data[i]);
        }
    }

    private void fillNeighbors(BlockTree blockTree) throws Exception {
        Dataset set = datasetMap.get(BlockTable.GID.name);
        set.init();
        // Select only the neighbors(6)
        set.getSelectedDims()[1] = 6;
        int[] data = (int[]) set.read();
        int height = set.getHeight();
        for (int i = 0; i < height; i++) {
            Block b = blockTree.get(i + 1);
            List<Block> neighbors = new ArrayList<>(6);
            for (int j = 0; j < 6; ++j) {
                int neighborId = data[(i * 6) + j];
                if (neighborId > 0)
                    neighbors.add(blockTree.get(neighborId));
                else
                    neighbors.add(null);
            }
            b.setNeighbors(neighbors);
        }
    }

    public float[] readFloatValues(String dataSetName) throws Exception {
        Dataset dataset = datasetMap.get(dataSetName);
        if (dataset == null)
            throw new NoSuchElementException("No dataset named '" + dataSetName + "' found!");

        dataset.init();
        selectAll(dataset);
        return (float[]) dataset.read();
    }

    public float[] readFloatValues(String dataSetName, Block block) throws Exception {
        Dataset dataset = datasetMap.get(dataSetName);
        if (dataset == null)
            throw new NoSuchElementException("No dataset named '" + dataSetName + "' found!");

        dataset.init();
        selectAll(dataset);

        dataset.getSelectedDims()[0] = 1;
        dataset.getStartDims()[0] = block.getId() - 1;

        return (float[]) dataset.read();
    }

    public int[] readIntValues(String dataSetName) throws Exception {
        Dataset dataset = datasetMap.get(dataSetName);
        if (dataset == null)
            throw new NoSuchElementException("No dataset named '" + dataSetName + "' found!");

        dataset.init();
        selectAll(dataset);
        return (int[]) dataset.read();
    }

    public int[] readIntValues(String dataSetName, Block block) throws Exception {
        Dataset dataset = datasetMap.get(dataSetName);
        if (dataset == null)
            throw new NoSuchElementException("No dataset named '" + dataSetName + "' found!");

        dataset.init();
        selectAll(dataset);

        dataset.getSelectedDims()[0] = 1;
        dataset.getStartDims()[0] = block.getId() - 1;

        return (int[]) dataset.read();
    }

    private void selectAll(Dataset dataset) {
        long[] selectedDims = dataset.getSelectedDims();
        long[] dims = dataset.getDims();
        System.arraycopy(dims, 0, selectedDims, 0, selectedDims.length);
    }


}
