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

import java.util.Collections;
import java.util.List;

/**
 * A cube in the adaptive mesh. Contains the necessary information to localize the block. To get the variable values
 * of a cube, use one of the read functions of {@link de.meldanor.neongenesis.hdf5.Flash3Reader} and the block.
 *
 * @implNote This class is nearly immutable. To mutate the block, use {@link de.meldanor.neongenesis.hdf5.BlockChanger}
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

    /**
     * Constructs an empty block with a fixed id
     *
     * @param id The unique id. 1 based
     */
    Block(int id) {
        this.id = id;
    }

    /**
     * @return The unique id of the block.
     * <p>
     * 1 based.
     */
    public int getId() {
        return id;
    }

    /**
     * @return Some FLASH3 specific flags.
     */
    public byte getBflags() {
        return bflags;
    }

    /**
     * @return Every child has an index, at which the child itself is contained in its parent array. 1 based.
     * <p>
     * -1 indicates, this is a root.
     */
    public byte getWhichChild() {
        return whichChild;
    }

    /**
     * @return The size of the cube in cm
     */
    public float getBlockSize() {
        return blockSize;
    }

    /**
     * @return The 3D coordinates in cm
     */
    public Point3D getCoordinates() {
        return coordinates;
    }

    /**
     * @return The bounding box of the cube in cm
     */
    public Point3D getBoundingBox() {
        return boundingBox;
    }

    /**
     * @return The refinement level of the cube. It is also the level of the node in the tree. 1 based
     */
    public byte getRefineLevel() {
        return refineLevel;
    }

    /**
     * @return 1, if the block is a leaf, 2 if its a parent, 3 otherwise(like the root)
     */
    public byte getNodeType() {
        return nodeType;
    }

    /**
     * Set the Bflags
     *
     * @param bflags See {@link #getBflags()}
     */
    void setBflags(byte bflags) {
        this.bflags = bflags;
    }

    /**
     * Set the BlockSize
     *
     * @param blockSize See {@link #getBlockSize()}
     */
    void setBlockSize(float blockSize) {
        this.blockSize = blockSize;
    }

    /**
     * Set the WhichChild
     *
     * @param whichChild See {@link #getWhichChild()}
     */
    void setWhichChild(byte whichChild) {
        this.whichChild = whichChild;
    }

    /**
     * Set the Coordinates
     *
     * @param coordinates See {@link #getCoordinates()}
     */
    void setCoordinates(Point3D coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Set the bounding box
     *
     * @param boundingBox See {@link #getBoundingBox()}
     */
    void setBoundingBox(Point3D boundingBox) {
        this.boundingBox = boundingBox;
    }

    /**
     * Set the refine level
     *
     * @param refineLevel See {@link #getRefineLevel()}
     */
    void setRefineLevel(byte refineLevel) {
        this.refineLevel = refineLevel;
    }


    /**
     * Set the refine level
     *
     * @param nodeType See {@link #getNodeType()}
     */
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

    /**
     * Return the neighbor of this block.
     *
     * @param face The direction to indicate the neighbor.
     * @return A neighbor if there is one, otherwise <code>null</code>
     */
    public Block getNeighbor(BlockFace face) {
        return neighbors.get(face.neighborListIndex);
    }

    /**
     * Set the neighbors
     *
     * @param neighbors See {@link #getNeighbors()}
     */
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

    /**
     * Indicates the orientation to get the neighbor of a block. See {@link #getNeighbor(de.meldanor.neongenesis.hdf5.Block.BlockFace)}
     */
    public enum BlockFace {
        /**
         * Negative x-coordinate
         */
        LEFT(-1, 0, 0, 0),
        /**
         * Positive x-coordinate
         */
        RIGHT(1, 0, 0, 1),
        /**
         * Negative y-coordinate
         */
        BOTTOM(0, -1, 0, 2),
        /**
         * Positive y-coordinate
         */
        TOP(0, 1, 0, 3),
        /**
         * Negative z-coordinate
         */
        BACK(0, 0, -1, 4),
        /**
         * Positive z-coordinate
         */
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
