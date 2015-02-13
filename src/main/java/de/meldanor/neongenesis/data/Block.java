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

/**
 *
 */
public class Block {

    private final int id;

    private byte bflags;
    private float blockSize;
    private byte whichChild;

    private Point3D coordinates;
    private Point3D boundingBox;
    private byte refineLevel;

    Block(int id) {
        this.id = id;
    }


    public int getId() {
        return id;
    }

    public byte getBflags() {
        return bflags;
    }

    public byte getWhichChild() {
        return whichChild;
    }

    public float getBlockSize() {
        return blockSize;
    }

    public Point3D getCoordinates() {
        return coordinates;
    }

    public Point3D getBoundingBox() {
        return boundingBox;
    }

    public byte getRefineLevel() {
        return refineLevel;
    }

    void setBflags(byte bflags) {
        this.bflags = bflags;
    }

    void setBlockSize(float blockSize) {
        this.blockSize = blockSize;
    }

    void setWhichChild(byte whichChild) {
        this.whichChild = whichChild;
    }

    void setCoordinates(Point3D coordinates) {
        this.coordinates = coordinates;
    }

    void setBoundingBox(Point3D boundingBox) {
        this.boundingBox = boundingBox;
    }

    void setRefineLevel(byte refineLevel) {
        this.refineLevel = refineLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Block block = (Block) o;

        return id == block.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Block{" +
                "id=" + id +
                ", bflags=" + bflags +
                ", blockSize=" + blockSize +
                ", whichChild=" + whichChild +
                ", coordinates=" + coordinates +
                ", boundingBox=" + boundingBox +
                ", refineLevel=" + refineLevel +
                '}';
    }
}
