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

import java.util.Collections;
import java.util.List;

/**
 *
 */
public class Block {

    private final int id;

    private byte bflags;
    private float blockSize;
    private byte whichChild;
    private byte nodeType;

    private Point3D coordinates;
    private Point3D boundingBox;
    private byte refineLevel;

    private List<Block> neighbors;

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

    public byte getNodeType() {
        return nodeType;
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

    void setNodeType(byte nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * The first neighbor is at lower x coordinate, the
     * second at higher x, the third at lower y, fourth at higher y, fifth
     * at lower z and the sixth at higher z.
     *
     * @return List of Neighbors. If there is no neighbor at a certain position, the member will be <code>null</code>
     */
    public List<Block> getNeighbors() {
        return neighbors;
    }

    public Block getNeighbor(BlockFace face) {
        return neighbors.get(face.neighborListIndex);
    }

    void setNeighbors(List<Block> neighbors) {
        this.neighbors = Collections.unmodifiableList(neighbors);
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

    public enum BlockFace {
        LEFT(-1, 0, 0, 0),
        RIGHT(1, 0, 0, 1),
        BOTTOM(0, -1, 0, 2),
        TOP(0, 1, 0, 3),
        BACK(0, 0, -1, 4),
        FRONT(0, 0, 1, 5);

        private final int xDirection;
        private final int yDirection;
        private final int zDirection;
        private final int neighborListIndex;

        BlockFace(int xDirection, int yDirection, int zDirection, int neighborListIndex) {
            this.xDirection = xDirection;
            this.yDirection = yDirection;
            this.zDirection = zDirection;
            this.neighborListIndex = neighborListIndex;
        }

        public int getXDirection() {
            return xDirection;
        }

        public int getYDirection() {
            return yDirection;
        }

        public int getZDirection() {
            return zDirection;
        }

        public Point3D getDirectionVector() {
            return new Point3D(xDirection, yDirection, zDirection);
        }

        public int getNeighborListIndex() {
            return neighborListIndex;
        }
    }
}
