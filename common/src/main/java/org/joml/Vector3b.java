/*
 * The MIT License
 *
 * Copyright (c) 2015-2021 Richard Greenlees
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

import java.io.*;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Contains the definition of a Vector comprising 3 bytes and associated
 * transformations.
 *
 * @author Richard Greenlees
 * @author Kai Burjack
 * @author Hans Uhlig
 */
public class Vector3b implements Externalizable, Cloneable, Vector3bc {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The x component of the vector.
     */
    public byte x;
    /**
     * The y component of the vector.
     */
    public byte y;
    /**
     * The z component of the vector.
     */
    public byte z;

    /**
     * Create a new {@link Vector3b} of <code>(0, 0, 0)</code>.
     */
    public Vector3b() {
    }

    /**
     * Create a new {@link Vector3b} and initialize all three components with
     * the given value.
     *
     * @param d the value of all three components
     */
    public Vector3b(byte d) {
        this.x = d;
        this.y = d;
        this.z = d;
    }

    /**
     * Create a new {@link Vector3b} and initialize all three components with
     * the given value.
     *
     * @param d the value of all three components
     */
    public Vector3b(int d) {
        this.x = (byte) d;
        this.y = (byte) d;
        this.z = (byte) d;
    }

    /**
     * Create a new {@link Vector3b} with the given component values.
     *
     * @param x the value of x
     * @param y the value of y
     * @param z the value of z
     */
    public Vector3b(byte x, byte y, byte z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    /**
     * Create a new {@link Vector3b} with the given component values.
     *
     * @param x the value of x
     * @param y the value of y
     * @param z the value of z
     */
    public Vector3b(int x, int y, int z) {
        this.x = (byte) x;
        this.y = (byte) y;
        this.z = (byte) z;
    }

    /**
     * Create a new {@link Vector3b} with the same values as <code>v</code>.
     *
     * @param v the {@link Vector3bc} to copy the values from
     */
    public Vector3b(Vector3bc v) {
        this.x = v.x();
        this.y = v.y();
        this.z = v.z();
    }

    /**
     * Create a new {@link Vector3b} with the given component values and
     * round using the given {@link RoundingMode}.
     *
     * @param x    the value of x
     * @param y    the value of y
     * @param z    the value of z
     * @param mode the {@link RoundingMode} to use
     */
    public Vector3b(float x, float y, float z, int mode) {
        this.x = (byte) Math.roundUsing(x, mode);
        this.y = (byte) Math.roundUsing(y, mode);
        this.z = (byte) Math.roundUsing(z, mode);
    }

    /**
     * Create a new {@link Vector3b} with the given component values and
     * round using the given {@link RoundingMode}.
     *
     * @param x    the value of x
     * @param y    the value of y
     * @param z    the value of z
     * @param mode the {@link RoundingMode} to use
     */
    public Vector3b(double x, double y, double z, int mode) {
        this.x = (byte) Math.roundUsing(x, mode);
        this.y = (byte) Math.roundUsing(y, mode);
        this.z = (byte) Math.roundUsing(z, mode);
    }

    /**
     * Create a new {@link Vector3b} with the first two components from the
     * given <code>v</code> and the given <code>z</code> and round using the given {@link RoundingMode}.
     *
     * @param v    the {@link Vector2fc} to copy the values from
     * @param z    the z component
     * @param mode the {@link RoundingMode} to use
     */
    public Vector3b(Vector2fc v, float z, int mode) {
        this.x = (byte) Math.roundUsing(v.x(), mode);
        this.y = (byte) Math.roundUsing(v.y(), mode);
        this.z = (byte) Math.roundUsing(z, mode);
    }

    /**
     * Create a new {@link Vector3b} and initialize its components to the rounded value of
     * the given vector.
     *
     * @param v    the {@link Vector3fc} to round and copy the values from
     * @param mode the {@link RoundingMode} to use
     */
    public Vector3b(Vector3fc v, int mode) {
        this.x = (byte) Math.roundUsing(v.x(), mode);
        this.y = (byte) Math.roundUsing(v.y(), mode);
        this.z = (byte) Math.roundUsing(v.z(), mode);
    }

    /**
     * Create a new {@link Vector3b} with the first two components from the
     * given <code>v</code> and the given <code>z</code> and round using the given {@link RoundingMode}.
     *
     * @param v    the {@link Vector2dc} to copy the values from
     * @param z    the z component
     * @param mode the {@link RoundingMode} to use
     */
    public Vector3b(Vector2dc v, float z, int mode) {
        this.x = (byte) Math.roundUsing(v.x(), mode);
        this.y = (byte) Math.roundUsing(v.y(), mode);
        this.z = (byte) Math.roundUsing(z, mode);
    }

    /**
     * Create a new {@link Vector3b} and initialize its components to the rounded value of
     * the given vector.
     *
     * @param v    the {@link Vector3dc} to round and copy the values from
     * @param mode the {@link RoundingMode} to use
     */
    public Vector3b(Vector3dc v, int mode) {
        this.x = (byte) Math.roundUsing(v.x(), mode);
        this.y = (byte) Math.roundUsing(v.y(), mode);
        this.z = (byte) Math.roundUsing(v.z(), mode);
    }

    /**
     * Create a new {@link Vector3b} and initialize its three components from the first
     * three elements of the given array.
     *
     * @param xyz the array containing at least three elements
     */
    public Vector3b(byte[] xyz) {
        this.x = xyz[0];
        this.y = xyz[1];
        this.z = xyz[2];
    }

    /**
     * Create a new {@link Vector3b} and read this vector from the supplied
     * {@link ByteBuffer} at the current buffer
     * {@link ByteBuffer#position() position}.
     * <p>
     * This method will not increment the position of the given ByteBuffer.
     * <p>
     * In order to specify the offset into the ByteBuffer at which the vector is
     * read, use {@link #Vector3b(int, ByteBuffer)}, taking the absolute
     * position as parameter.
     *
     * @param buffer values will be read in <code>x, y, z</code> order
     * @see #Vector3b(int, ByteBuffer)
     */
    public Vector3b(ByteBuffer buffer) {
        int offset = buffer.position();
        this.x = buffer.get(offset);
        this.y = buffer.get(offset + 1);
        this.z = buffer.get(offset + 2);
    }

    /**
     * Create a new {@link Vector3b} and read this vector from the supplied
     * {@link ByteBuffer} starting at the specified absolute buffer
     * position/index.
     * <p>
     * This method will not increment the position of the given ByteBuffer.
     *
     * @param index  the absolute position into the ByteBuffer
     * @param buffer values will be read in <code>x, y, z</code> order
     */
    public Vector3b(int index, ByteBuffer buffer) {
        this.x = buffer.get(index);
        this.y = buffer.get(index + 1);
        this.z = buffer.get(index + 2);
    }

    public byte x() {
        return this.x;
    }

    public byte y() {
        return this.y;
    }

    public byte z() {
        return this.z;
    }

    /**
     * Set the x, y and z components to match the supplied vector.
     *
     * @param v contains the values of x, y and z to set
     * @return this
     */
    public Vector3b set(Vector3bc v) {
        x = v.x();
        y = v.y();
        z = v.z();
        return this;
    }

    /**
     * Set this {@link Vector3b} to the values of v using {@link RoundingMode#TRUNCATE} rounding.
     * <p>
     * Note that due to the given vector <code>v</code> storing the components
     * in double-precision, there is the possibility to lose precision.
     *
     * @param v the vector to copy from
     * @return this
     */
    public Vector3b set(Vector3dc v) {
        this.x = (byte) v.x();
        this.y = (byte) v.y();
        this.z = (byte) v.z();
        return this;
    }

    /**
     * Set this {@link Vector3b} to the values of v using the given {@link RoundingMode}.
     * <p>
     * Note that due to the given vector <code>v</code> storing the components
     * in double-precision, there is the possibility to lose precision.
     *
     * @param v    the vector to copy from
     * @param mode the {@link RoundingMode} to use
     * @return this
     */
    public Vector3b set(Vector3dc v, int mode) {
        this.x = (byte) Math.roundUsing(v.x(), mode);
        this.y = (byte) Math.roundUsing(v.y(), mode);
        this.z = (byte) Math.roundUsing(v.z(), mode);
        return this;
    }

    /**
     * Set this {@link Vector3b} to the values of v using the given {@link RoundingMode}.
     * <p>
     * Note that due to the given vector <code>v</code> storing the components
     * in double-precision, there is the possibility to lose precision.
     *
     * @param v    the vector to copy from
     * @param mode the {@link RoundingMode} to use
     * @return this
     */
    public Vector3b set(Vector3fc v, int mode) {
        this.x = (byte) Math.roundUsing(v.x(), mode);
        this.y = (byte) Math.roundUsing(v.y(), mode);
        this.z = (byte) Math.roundUsing(v.z(), mode);
        return this;
    }

    /**
     * Set the x, y, and z components to the supplied value.
     *
     * @param d the value of all three components
     * @return this
     */
    public Vector3b set(byte d) {
        this.x = d;
        this.y = d;
        this.z = d;
        return this;
    }

    /**
     * Set the x, y and z components to the supplied values.
     *
     * @param x the x component
     * @param y the y component
     * @param z the z component
     * @return this
     */
    public Vector3b set(byte x, byte y, byte z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    /**
     * Set the three components of this vector to the first three elements of the given array.
     *
     * @param xyz the array containing at least three elements
     * @return this
     */
    public Vector3b set(byte[] xyz) {
        this.x = xyz[0];
        this.y = xyz[1];
        this.z = xyz[2];
        return this;
    }

    /**
     * Read this vector from the supplied {@link ByteBuffer} at the current
     * buffer {@link ByteBuffer#position() position}.
     * <p>
     * This method will not increment the position of the given ByteBuffer.
     * <p>
     * In order to specify the offset into the ByteBuffer at which the vector is
     * read, use {@link #set(int, ByteBuffer)}, taking the absolute position as
     * parameter.
     *
     * @param buffer values will be read in <code>x, y, z</code> order
     * @return this
     * @see #set(int, ByteBuffer)
     */
    public Vector3b set(ByteBuffer buffer) {
        int offset = buffer.position();
        this.x = buffer.get(offset);
        this.y = buffer.get(offset + 1);
        this.z = buffer.get(offset + 2);
        return this;
    }

    /**
     * Read this vector from the supplied {@link ByteBuffer} starting at the
     * specified absolute buffer position/index.
     * <p>
     * This method will not increment the position of the given ByteBuffer.
     *
     * @param index  the absolute position into the ByteBuffer
     * @param buffer values will be read in <code>x, y, z</code> order
     * @return this
     */
    public Vector3b set(int index, ByteBuffer buffer) {
        this.x = buffer.get(index);
        this.y = buffer.get(index + 1);
        this.z = buffer.get(index + 2);
        return this;
    }

    public int get(int component) throws IllegalArgumentException {
        return switch (component) {
            case 0 -> x;
            case 1 -> y;
            case 2 -> z;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Set the value of the specified component of this vector.
     *
     * @param component the component whose value to set, within <code>[0..2]</code>
     * @param value     the value to set
     * @return this
     * @throws IllegalArgumentException if <code>component</code> is not within <code>[0..2]</code>
     */
    public Vector3b setComponent(int component, byte value) throws IllegalArgumentException {
        switch (component) {
            case 0 -> x = value;
            case 1 -> y = value;
            case 2 -> z = value;
            default -> throw new IllegalArgumentException();
        }
        return this;
    }

    public ByteBuffer get(ByteBuffer buffer) {
        int offset = buffer.position();
        buffer.put(offset, this.x);
        buffer.put(offset + 1, this.y);
        buffer.put(offset + 2, this.z);
        return buffer;
    }

    public ByteBuffer get(int index, ByteBuffer buffer) {
        buffer.put(index, this.x);
        buffer.put(index + 1, this.y);
        buffer.put(index + 2, this.z);
        return buffer;
    }

    /**
     * Subtract the supplied vector from this one and store the result in
     * <code>this</code>.
     *
     * @param v the vector to subtract
     * @return this
     */
    public Vector3b sub(Vector3bc v) {
        this.x = (byte) (this.x - v.x());
        this.y = (byte) (this.y - v.y());
        this.z = (byte) (this.z - v.z());
        return this;
    }

    public Vector3b sub(Vector3bc v, Vector3b dest) {
        dest.x = (byte) (x - v.x());
        dest.y = (byte) (y - v.y());
        dest.z = (byte) (z - v.z());
        return dest;
    }

    /**
     * Decrement the components of this vector by the given values.
     *
     * @param x the x component to subtract
     * @param y the y component to subtract
     * @param z the z component to subtract
     * @return this
     */
    public Vector3b sub(int x, int y, int z) {
        this.x = (byte) (this.x - x);
        this.y = (byte) (this.y - y);
        this.z = (byte) (this.z - z);
        return this;
    }

    public Vector3b sub(int x, int y, int z, Vector3b dest) {
        dest.x = (byte) (this.x - x);
        dest.y = (byte) (this.y - y);
        dest.z = (byte) (this.z - z);
        return dest;
    }

    /**
     * Add the supplied vector to this one.
     *
     * @param v the vector to add
     * @return this
     */
    public Vector3b add(Vector3bc v) {
        this.x = (byte) (this.x + v.x());
        this.y = (byte) (this.y + v.y());
        this.z = (byte) (this.z + v.z());
        return this;
    }

    public Vector3b add(Vector3bc v, Vector3b dest) {
        dest.x = (byte) (x + v.x());
        dest.y = (byte) (y + v.y());
        dest.z = (byte) (z + v.z());
        return dest;
    }

    /**
     * Increment the components of this vector by the given values.
     *
     * @param x the x component to add
     * @param y the y component to add
     * @param z the z component to add
     * @return this
     */
    public Vector3b add(int x, int y, int z) {
        this.x = (byte) (this.x + x);
        this.y = (byte) (this.y + y);
        this.z = (byte) (this.z + z);
        return this;
    }

    public Vector3b add(int x, int y, int z, Vector3b dest) {
        dest.x = (byte) (this.x + x);
        dest.y = (byte) (this.y + y);
        dest.z = (byte) (this.z + z);
        return dest;
    }

    /**
     * Multiply all components of this {@link Vector3b} by the given scalar
     * value.
     *
     * @param scalar the scalar to multiply this vector by
     * @return this
     */
    public Vector3b mul(int scalar) {
        this.x = (byte) (x * scalar);
        this.y = (byte) (y * scalar);
        this.z = (byte) (z * scalar);
        return this;
    }

    public Vector3b mul(int scalar, Vector3b dest) {
        dest.x = (byte) (x * scalar);
        dest.y = (byte) (y * scalar);
        dest.z = (byte) (z * scalar);
        return dest;
    }

    /**
     * Multiply all components of this {@link Vector3b} by the given vector.
     *
     * @param v the vector to multiply
     * @return this
     */
    public Vector3b mul(Vector3bc v) {
        this.x = (byte) (this.x * v.x());
        this.y = (byte) (this.y * v.y());
        this.z = (byte) (this.z * v.z());
        return this;
    }

    public Vector3b mul(Vector3bc v, Vector3b dest) {
        dest.x = (byte) (x * v.x());
        dest.y = (byte) (y * v.y());
        dest.z = (byte) (z * v.z());
        return dest;
    }

    /**
     * Multiply the components of this vector by the given values.
     *
     * @param x the x component to multiply
     * @param y the y component to multiply
     * @param z the z component to multiply
     * @return this
     */
    public Vector3b mul(int x, int y, int z) {
        this.x = (byte) (this.x * x);
        this.y = (byte) (this.y * y);
        this.z = (byte) (this.z * z);
        return this;
    }

    public Vector3b mul(int x, int y, int z, Vector3b dest) {
        dest.x = (byte) (this.x * x);
        dest.y = (byte) (this.y * y);
        dest.z = (byte) (this.z * z);
        return dest;
    }

    /**
     * Divide all components of this {@link Vector3b} by the given scalar value.
     *
     * @param scalar the scalar to divide by
     * @return this
     */
    public Vector3b div(float scalar) {
        float invscalar = 1.0f / scalar;
        this.x = (byte) (x * invscalar);
        this.y = (byte) (y * invscalar);
        this.z = (byte) (z * invscalar);
        return this;
    }

    public Vector3b div(float scalar, Vector3b dest) {
        float invscalar = 1.0f / scalar;
        dest.x = (byte) (x * invscalar);
        dest.y = (byte) (y * invscalar);
        dest.z = (byte) (z * invscalar);
        return dest;
    }

    /**
     * Divide all components of this {@link Vector3b} by the given scalar value.
     *
     * @param scalar the scalar to divide by
     * @return this
     */
    public Vector3b div(int scalar) {
        this.x = (byte) (x / scalar);
        this.y = (byte) (y / scalar);
        this.z = (byte) (z / scalar);
        return this;
    }

    public Vector3b div(int scalar, Vector3b dest) {
        dest.x = (byte) (x / scalar);
        dest.y = (byte) (y / scalar);
        dest.z = (byte) (z / scalar);
        return dest;
    }

    public long lengthSquared() {
        return x * x + y * y + z * z;
    }

    /**
     * Get the length squared of a 3-dimensional single-precision vector.
     *
     * @param x The vector's x component
     * @param y The vector's y component
     * @param z The vector's z component
     * @return the length squared of the given vector
     */
    public static long lengthSquared(int x, int y, int z) {
        return (long) x * x + (long) y * y + (long) z * z;
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * Get the length of a 3-dimensional single-precision vector.
     *
     * @param x The vector's x component
     * @param y The vector's y component
     * @param z The vector's z component
     * @return the length squared of the given vector
     */
    public static double length(int x, int y, int z) {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double distance(Vector3bc v) {
        int dx = this.x - v.x();
        int dy = this.y - v.y();
        int dz = this.z - v.z();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public double distance(int x, int y, int z) {
        int dx = this.x - x;
        int dy = this.y - y;
        int dz = this.z - z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public long gridDistance(Vector3bc v) {
        return Math.abs(v.x() - x()) + Math.abs(v.y() - y()) + Math.abs(v.z() - z());
    }

    public long gridDistance(int x, int y, int z) {
        return Math.abs(x - x()) + Math.abs(y - y()) + Math.abs(z - z());
    }

    public long distanceSquared(Vector3bc v) {
        int dx = this.x - v.x();
        int dy = this.y - v.y();
        int dz = this.z - v.z();
        return dx * dx + dy * dy + dz * dz;
    }

    public long distanceSquared(int x, int y, int z) {
        int dx = this.x - x;
        int dy = this.y - y;
        int dz = this.z - z;
        return (long) dx * dx + (long) dy * dy + (long) dz * dz;
    }

    /**
     * Return the distance between <code>(x1, y1, z1)</code> and <code>(x2, y2, z2)</code>.
     *
     * @param x1 the x component of the first vector
     * @param y1 the y component of the first vector
     * @param z1 the z component of the first vector
     * @param x2 the x component of the second vector
     * @param y2 the y component of the second vector
     * @param z2 the z component of the second vector
     * @return the euclidean distance
     */
    public static double distance(int x1, int y1, int z1, int x2, int y2, int z2) {
        return Math.sqrt(distanceSquared(x1, y1, z1, x2, y2, z2));
    }

    /**
     * Return the squared distance between <code>(x1, y1, z1)</code> and <code>(x2, y2, z2)</code>.
     *
     * @param x1 the x component of the first vector
     * @param y1 the y component of the first vector
     * @param z1 the z component of the first vector
     * @param x2 the x component of the second vector
     * @param y2 the y component of the second vector
     * @param z2 the z component of the second vector
     * @return the euclidean distance squared
     */
    public static long distanceSquared(int x1, int y1, int z1, int x2, int y2, int z2) {
        int dx = x1 - x2;
        int dy = y1 - y2;
        int dz = z1 - z2;
        return (long) dx * dx + (long) dy * dy + (long) dz * dz;
    }

    /**
     * Set all components to zero.
     *
     * @return this
     */
    public Vector3b zero() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        return this;
    }

    /**
     * Return a string representation of this vector.
     * <p>
     * This method creates a new {@link DecimalFormat} on every invocation with the format string "<code>0.000E0;-</code>".
     *
     * @return the string representation
     */
    public String toString() {
        return Runtime.formatNumbers(toString(Options.NUMBER_FORMAT));
    }

    /**
     * Return a string representation of this vector by formatting the vector components with the given {@link NumberFormat}.
     *
     * @param formatter the {@link NumberFormat} used to format the vector components with
     * @return the string representation
     */
    public String toString(NumberFormat formatter) {
        return "(" + formatter.format(x) + " " + formatter.format(y) + " " + formatter.format(z) + ")";
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.write(x);
        out.write(y);
        out.write(z);
    }

    public void readExternal(ObjectInput in) throws IOException {
        x = in.readByte();
        y = in.readByte();
        z = in.readByte();
    }

    /**
     * Negate this vector.
     *
     * @return this
     */
    public Vector3b negate() {
        this.x = (byte) -x;
        this.y = (byte) -y;
        this.z = (byte) -z;
        return this;
    }

    public Vector3b negate(Vector3b dest) {
        dest.x = (byte) -x;
        dest.y = (byte) -y;
        dest.z = (byte) -z;
        return dest;
    }

    /**
     * Set the components of this vector to be the component-wise minimum of this and the other vector.
     *
     * @param v the other vector
     * @return this
     */
    public Vector3b min(Vector3bc v) {
        this.x = x < v.x() ? x : v.x();
        this.y = y < v.y() ? y : v.y();
        this.z = z < v.z() ? z : v.z();
        return this;
    }

    public Vector3b min(Vector3bc v, Vector3b dest) {
        dest.x = x < v.x() ? x : v.x();
        dest.y = y < v.y() ? y : v.y();
        dest.z = z < v.z() ? z : v.z();
        return dest;
    }

    /**
     * Set the components of this vector to be the component-wise maximum of this and the other vector.
     *
     * @param v the other vector
     * @return this
     */
    public Vector3b max(Vector3bc v) {
        this.x = x > v.x() ? x : v.x();
        this.y = y > v.y() ? y : v.y();
        this.z = z > v.z() ? z : v.z();
        return this;
    }

    public Vector3b max(Vector3bc v, Vector3b dest) {
        dest.x = x > v.x() ? x : v.x();
        dest.y = y > v.y() ? y : v.y();
        dest.z = z > v.z() ? z : v.z();
        return dest;
    }

    public int maxComponent() {
        float absX = Math.abs(x);
        float absY = Math.abs(y);
        float absZ = Math.abs(z);
        if (absX >= absY && absX >= absZ) {
            return 0;
        } else if (absY >= absZ) {
            return 1;
        }
        return 2;
    }

    public int minComponent() {
        float absX = Math.abs(x);
        float absY = Math.abs(y);
        float absZ = Math.abs(z);
        if (absX < absY && absX < absZ) {
            return 0;
        } else if (absY < absZ) {
            return 1;
        }
        return 2;
    }

    /**
     * Set <code>this</code> vector's components to their respective absolute values.
     *
     * @return this
     */
    public Vector3b absolute() {
        this.x = (byte) Math.abs(this.x);
        this.y = (byte) Math.abs(this.y);
        this.z = (byte) Math.abs(this.z);
        return this;
    }

    public Vector3b absolute(Vector3b dest) {
        dest.x = (byte) Math.abs(this.x);
        dest.y = (byte) Math.abs(this.y);
        dest.z = (byte) Math.abs(this.z);
        return dest;
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + z;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Vector3b other = (Vector3b) obj;
        if (x != other.x) {
            return false;
        }
        if (y != other.y) {
            return false;
        }
        return z == other.z;
    }

    public boolean equals(int x, int y, int z) {
        if (this.x != x)
            return false;
        if (this.y != y)
            return false;
        return this.z == z;
    }

    @Override
    public Vector3i toInt() {
        return new Vector3i(this.x, this.y, this.z);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
