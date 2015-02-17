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

import javafx.geometry.Point3D;

import java.util.List;

/**
 * Builder pattern to mutate a single block
 *
 * @see de.meldanor.neongenesis.hdf5.Block
 */
public class BlockChanger {

    private int id;

    private byte bflags;
    private float blockSize;
    private byte whichChild;
    private byte nodeType;

    private Point3D coordinates;
    private Point3D boundingBox;
    private byte refineLevel;

    private List<Block> neighbors;

    /**
     * Initially create the changer with a Block. The block it self will no be changed.
     *
     * @param original The original data block
     */
    public BlockChanger(Block original) {
        this.reset(original);
    }

    /**
     * Reset the changer by using another block as an original
     *
     * @param original The new block to use the data from
     */
    public void reset(Block original) {
        this.id = original.getId();
        this.bflags = original.getBflags();
        this.blockSize = original.getBlockSize();
        this.whichChild = original.getWhichChild();
        this.nodeType = original.getNodeType();
        this.coordinates = original.getCoordinates();
        this.boundingBox = original.getBoundingBox();
        this.refineLevel = original.getRefineLevel();
        this.neighbors = original.getNeighbors();
    }

    /**
     * Build the changed block.
     *
     * @return A new instance with the new values
     */
    public Block change() {

        Block b = new Block(id);
        b.setBflags(bflags);
        b.setBlockSize(blockSize);
        b.setWhichChild(whichChild);
        b.setNodeType(nodeType);
        b.setCoordinates(coordinates);
        b.setBoundingBox(boundingBox);
        b.setRefineLevel(refineLevel);
        b.setNeighbors(neighbors);

        return b;
    }

    /**
     * @param id See {@link Block#getId()}
     * @return This instance
     */
    public BlockChanger setId(int id) {
        this.id = id;
        return this;
    }

    /**
     * @param bflags See {@link Block#getBflags()}
     * @return This instance
     */
    public BlockChanger setBflags(byte bflags) {
        this.bflags = bflags;
        return this;
    }

    /**
     * @param blockSize See {@link Block#getBlockSize()}
     * @return This instance
     */
    public BlockChanger setBlockSize(float blockSize) {
        this.blockSize = blockSize;
        return this;
    }

    /**
     * @param whichChild See {@link Block#getWhichChild()}
     * @return This instance
     */
    public BlockChanger setWhichChild(byte whichChild) {
        this.whichChild = whichChild;
        return this;
    }

    /**
     * @param nodeType See {@link Block#getNodeType()}
     * @return This instance
     */
    public BlockChanger setNodeType(byte nodeType) {
        this.nodeType = nodeType;
        return this;
    }

    /**
     * @param coordinates See {@link Block#getCoordinates()}
     * @return This instance
     */
    public BlockChanger setCoordinates(Point3D coordinates) {
        this.coordinates = coordinates;
        return this;
    }

    /**
     * @param boundingBox See {@link Block#getBoundingBox()}
     * @return This instance
     */
    public BlockChanger setBoundingBox(Point3D boundingBox) {
        this.boundingBox = boundingBox;
        return this;
    }

    /**
     * @param refineLevel See {@link Block#getRefineLevel()}
     * @return This instance
     */
    public BlockChanger setRefineLevel(byte refineLevel) {
        this.refineLevel = refineLevel;
        return this;
    }

    /**
     * @param neighbors See {@link Block#getNeighbors()}
     * @return This instance
     */
    public BlockChanger setNeighbors(List<Block> neighbors) {
        this.neighbors = neighbors;
        return this;
    }
}
