/*
 * The MIT License
 *
 * Copyright (c) 2016-2021 JOML
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
package org.joml;

import java.nio.ByteBuffer;

/**
 * Interface to a read-only view of a 3-dimensional vector of bytes.
 *
 * @author Kai Burjack
 */
public interface Vector3bc {

    /**
     * @return the value of the x component
     */
    byte x();

    /**
     * @return the value of the y component
     */
    byte y();

    /**
     * @return the value of the z component
     */
    byte z();

    /**
     * Store this vector into the supplied {@link ByteBuffer} at the current
     * buffer {@link ByteBuffer#position() position}.
     * <p>
     * This method will not increment the position of the given ByteBuffer.
     * <p>
     * In order to specify the offset into the ByteBuffer at which the vector is
     * stored, use {@link #get(int, ByteBuffer)}, taking the absolute position
     * as parameter.
     *
     * @see #get(int, ByteBuffer)
     *
     * @param buffer
     *          will receive the values of this vector in <code>x, y, z</code> order
     * @return the passed in buffer
     */
    ByteBuffer get(ByteBuffer buffer);

    /**
     * Store this vector into the supplied {@link ByteBuffer} starting at the
     * specified absolute buffer position/index.
     * <p>
     * This method will not increment the position of the given ByteBuffer.
     *
     * @param index
     *          the absolute position into the ByteBuffer
     * @param buffer
     *          will receive the values of this vector in <code>x, y, z</code> order
     * @return the passed in buffer
     */
    ByteBuffer get(int index, ByteBuffer buffer);

    /**
     * Subtract the supplied vector from this one and store the result in
     * <code>dest</code>.
     *
     * @param v
     *          the vector to subtract
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector3b sub(Vector3bc v, Vector3b dest);

    /**
     * Decrement the components of this vector by the given values and store the
     * result in <code>dest</code>.
     *
     * @param x
     *          the x component to subtract
     * @param y
     *          the y component to subtract
     * @param z
     *          the z component to subtract
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector3b sub(int x, int y, int z, Vector3b dest);

    /**
     * Add the supplied vector to this one and store the result in
     * <code>dest</code>.
     *
     * @param v
     *          the vector to add
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector3b add(Vector3bc v, Vector3b dest);

    /**
     * Increment the components of this vector by the given values and store the
     * result in <code>dest</code>.
     *
     * @param x
     *          the x component to add
     * @param y
     *          the y component to add
     * @param z
     *          the z component to add
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector3b add(int x, int y, int z, Vector3b dest);

    /**
     * Multiply the components of this vector by the given scalar and store the result in <code>dest</code>.
     *
     * @param scalar
     *        the value to multiply this vector's components by
     * @param dest
     *        will hold the result
     * @return dest
     */
    Vector3b mul(int scalar, Vector3b dest);

    /**
     * Multiply the supplied vector by this one and store the result in
     * <code>dest</code>.
     *
     * @param v
     *          the vector to multiply
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector3b mul(Vector3bc v, Vector3b dest);

    /**
     * Multiply the components of this vector by the given values and store the
     * result in <code>dest</code>.
     *
     * @param x
     *          the x component to multiply
     * @param y
     *          the y component to multiply
     * @param z
     *          the z component to multiply
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector3b mul(int x, int y, int z, Vector3b dest);

    /**
     * Divide all components of this {@link Vector3b} by the given scalar value
     * and store the result in <code>dest</code>.
     *
     * @param scalar
     *          the scalar to divide by
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector3b div(float scalar, Vector3b dest);

    /**
     * Divide all components of this {@link Vector3b} by the given scalar value
     * and store the result in <code>dest</code>.
     *
     * @param scalar
     *          the scalar to divide by
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector3b div(int scalar, Vector3b dest);

    /**
     * Return the length squared of this vector.
     *
     * @return the length squared
     */
    long lengthSquared();

    /**
     * Return the length of this vector.
     *
     * @return the length
     */
    double length();

    /**
     * Return the distance between this Vector and <code>v</code>.
     *
     * @param v
     *          the other vector
     * @return the distance
     */
    double distance(Vector3bc v);

    /**
     * Return the distance between <code>this</code> vector and <code>(x, y, z)</code>.
     *
     * @param x
     *          the x component of the other vector
     * @param y
     *          the y component of the other vector
     * @param z
     *          the z component of the other vector
     * @return the euclidean distance
     */
    double distance(int x, int y, int z);


    /**
     * Return the grid distance in between (aka 1-Norm, Minkowski or Manhattan distance)
     * <code>(x, y)</code>.
     *
     * @param v
     *          the other vector
     * @return the grid distance
     */
    long gridDistance(Vector3bc v);

    /**
     * Return the grid distance in between (aka 1-Norm, Minkowski or Manhattan distance)
     * <code>(x, y)</code>.
     *
     * @param x
     *          the x component of the other vector
     * @param y
     *          the y component of the other vector
     * @param z
     *          the y component of the other vector
     * @return the grid distance
     */
    long gridDistance(int x, int y, int z);

    /**
     * Return the square of the distance between this vector and <code>v</code>.
     *
     * @param v
     *          the other vector
     * @return the squared of the distance
     */
    long distanceSquared(Vector3bc v);

    /**
     * Return the square of the distance between <code>this</code> vector and <code>(x, y, z)</code>.
     *
     * @param x
     *          the x component of the other vector
     * @param y
     *          the y component of the other vector
     * @param z
     *          the z component of the other vector
     * @return the square of the distance
     */
    long distanceSquared(int x, int y, int z);

    /**
     * Negate this vector and store the result in <code>dest</code>.
     *
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector3b negate(Vector3b dest);

    /**
     * Set the components of <code>dest</code> to be the component-wise minimum of this and the other vector.
     *
     * @param v
     *          the other vector
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector3b min(Vector3bc v, Vector3b dest);

    /**
     * Set the components of <code>dest</code> to be the component-wise maximum of this and the other vector.
     *
     * @param v
     *          the other vector
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector3b max(Vector3bc v, Vector3b dest);

    /**
     * Get the value of the specified component of this vector.
     *
     * @param component
     *          the component, within <code>[0..2]</code>
     * @return the value
     * @throws IllegalArgumentException if <code>component</code> is not within <code>[0..2]</code>
     */
    int get(int component) throws IllegalArgumentException;

    /**
     * Determine the component with the biggest absolute value.
     *
     * @return the component index, within <code>[0..2]</code>
     */
    int maxComponent();

    /**
     * Determine the component with the smallest (towards zero) absolute value.
     *
     * @return the component index, within <code>[0..2]</code>
     */
    int minComponent();

    /**
     * Compute the absolute of each of this vector's components
     * and store the result into <code>dest</code>.
     *
     * @param dest
     *          will hold the result
     * @return dest
     */
    Vector3b absolute(Vector3b dest);

    /**
     * Compare the vector components of <code>this</code> vector with the given <code>(x, y, z)</code>
     * and return whether all of them are equal.
     *
     * @param x
     *          the x component to compare to
     * @param y
     *          the y component to compare to
     * @param z
     *          the z component to compare to
     * @return <code>true</code> if all the vector components are equal
     */
    boolean equals(int x, int y, int z);

    Vector3i toInt();
}
