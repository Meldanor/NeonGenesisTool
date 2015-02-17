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

package de.meldanor.neongenesis.hdf5;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An implementation of an oct tree. The tree structure represents the inheritance of blocks. A parent always have
 * 8 children, each placed in an even space in the parents block.
 */
public class BlockTree {

    private Node root;

    /**
     * Helper class
     */
    private class Node {

        private Block value;
        private Node parent;
        private Node[] children;

        public Node(Block value) {
            this.value = value;
        }
    }

    private Node[] nodes;

    /**
     * Creates the structure of the block tree using the relation array.
     *
     * @param relationArray The relation array. Every 9 elements are one block.
     *                      The first is the parents id(-1 if no parent), the next 8 are the children
     * @param size          The amount of blocks
     */
    BlockTree(int[] relationArray, int size) {
        this.nodes = new Node[size];
        for (int i = 0; i < size; i++) {
            this.nodes[i] = new Node(new Block(i + 1));
        }
        construct(relationArray);
    }

    /**
     * Get the block with the given block id
     *
     * @param blockId The block id. 1 based
     * @return The block at the given block id
     */
    public Block get(int blockId) {
        return getNode(blockId).value;
    }

    /**
     * Get all blocks ordered by their id as a list
     *
     * @return An ordered Array List
     */
    public List<Block> getAll() {
        return Arrays.stream(nodes).map(n -> n.value).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Return the parent block of the given block if there is one
     *
     * @param block The child
     * @return The parent block or, if not existing, <code>null</code>
     */
    public Block getParentOf(Block block) {
        return getParentOf(block.getId());
    }

    /**
     * Return the parent block of the given block if there is one
     *
     * @param blockId The block id, 1 based
     * @return The parent block or, if not existing, <code>null</code>
     * @see #getParentOf(Block)
     */
    public Block getParentOf(int blockId) {
        Node parent = getNode(blockId).parent;
        return parent != null ? parent.value : null;
    }

    /**
     * Return the children of the parents block if there are any.
     *
     * @param block The parent block
     * @return A list of the children or an empty list, if there are no children
     */
    public List<Block> getChildrenOf(Block block) {
        return getChildrenOf(block.getId());
    }

    /**
     * Return the children of the parents block if there are any.
     *
     * @param blockId The parents block id, 1 based
     * @return A list of the children or an empty list, if there are no children
     * @see #getChildrenOf(Block)
     */
    public List<Block> getChildrenOf(int blockId) {
        Node[] children = getNode(blockId).children;
        if (children == null)
            return Collections.emptyList();
        else
            return Arrays.stream(children).map(n -> n.value).collect(Collectors.toCollection(ArrayList::new));

    }

    private Node getNode(int blockId) {
        return nodes[blockId - 1];
    }

    private void construct(int[] relationArray) {
        for (int i = 0; i < relationArray.length; i += 9) {
            // Every 9 ids are one block
            // First id = id of the parent
            int nodeIndex = (i / 9);
            int parentId = relationArray[i];
            Node node = nodes[nodeIndex];
            if (parentId >= 0) {
                node.parent = getNode(parentId);
            }
            // Last 8 ids = ids of the children
            for (int j = 0; j < 8; ++j) {
                int childId = relationArray[i + 1 + j];
                // if there is no children, break the loop
                // there are always 8 or 0 children
                if (childId < 0)
                    break;
                if (node.children == null)
                    node.children = new Node[8];

                node.children[j] = getNode(childId);
            }
        }
        this.root = nodes[0];
    }

    /**
     * Traverse the tree in level order
     *
     * @return A list with blocks ordered by their refinement level
     */
    public List<Block> levelOrder() {
        List<Block> res = new ArrayList<>();

        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            res.add(node.value);

            if (node.children != null) {
                for (Node child : node.children) {
                    if (child != null)
                        queue.add(child);
                }
            }
        }

        return res;

    }
}
