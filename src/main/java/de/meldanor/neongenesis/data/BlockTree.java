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

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
public class BlockTree {

    private Node root;

    private class Node {

        private Block value;
        private Node parent;
        private Node[] children;

        public Node(Block value) {
            this.value = value;
        }
    }

    private Node[] nodes;

    BlockTree(int[] relationArray, int size) {
        this.nodes = new Node[size];
        for (int i = 0; i < size; i++) {
            this.nodes[i] = new Node(new Block(i + 1));
        }
        construct(relationArray);
    }

    public Block get(int blockId) {
        return getNode(blockId).value;
    }

    public List<Block> getAll() {
        return Arrays.stream(nodes).map(n -> n.value).collect(Collectors.toCollection(ArrayList::new));
    }

    public Block getParentOf(Block block) {
        return getParentOf(block.getId());
    }

    public Block getParentOf(int blockId) {
        Node parent = getNode(blockId).parent;
        return parent != null ? parent.value : null;
    }

    public List<Block> getChildrenOf(Block block) {
        return getChildrenOf(block.getId());
    }

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
            int nodeIndex = (i / 9);
            int parentId = relationArray[i];
            Node node = nodes[nodeIndex];
            if (parentId >= 0) {
                node.parent = getNode(parentId);
            }
            for (int j = 0; j < 8; ++j) {
                int childId = relationArray[i + 1 + j];
                if (childId < 0)
                    break;
                if (node.children == null)
                    node.children = new Node[8];

                node.children[j] = getNode(childId);
            }
        }
        this.root = nodes[0];
    }

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
