package com.crown.resource.image;

import com.crown.util.PixelFormat;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.nio.ByteBuffer;
import java.util.Objects;

public class GenericImageData implements ImageData {
    private static final ColorSpace LINEAR_RGB = ColorSpace.getInstance(ColorSpace.CS_sRGB);
    private final int width;
    private final int height;
    private final ByteBuffer bytes;
    private final PixelFormat format;

    public GenericImageData(int width, int height, ByteBuffer bytes, PixelFormat format) {
        this.width = width;
        this.height = height;
        this.bytes = bytes;
        this.format = format;
    }

    @Override
    public int getRGBA(int x, int y) {
        return format.getRGBA((x + y * width) * format.channels, bytes);
    }

    @Override
    public void setRGBA(int x, int y, int rgba) {
        format.putRGBA((x + y * width) * format.channels, rgba, bytes);
    }

    @Override
    public BufferedImage toBufferedImage() {
        ColorModel cm = switch (format) {
            case RGBA -> new ComponentColorModel(
                    LINEAR_RGB, true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
            case RGB -> new ComponentColorModel(
                    LINEAR_RGB, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        };

        WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
        BufferedImage img = new BufferedImage(cm, raster, false, null);

        int[] rgba = new int[4];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int idx = (x + y * width) * format.channels;
                int color = format.getRGBA(idx, bytes);
                rgba[0] = (byte) (color & 0xFF);
                rgba[1] = (byte) ((color >> 8) & 0xFF);
                rgba[2] = (byte) ((color >> 16) & 0xFF);
                rgba[3] = (byte) ((color >> 24) & 0xFF);
                raster.setPixel(x, y, rgba);
            }
        }

        return img;
    }

    @Override
    public void destroy() {
        MemoryUtil.memFree(bytes);
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public ByteBuffer bytes() {
        return bytes;
    }

    @Override
    public PixelFormat format() {
        return format;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (GenericImageData) obj;
        return this.width == that.width &&
                this.height == that.height &&
                Objects.equals(this.bytes, that.bytes) &&
                Objects.equals(this.format, that.format);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, bytes, format);
    }

    @Override
    public String toString() {
        return "GenericImageData[" +
                "width=" + width + ", " +
                "height=" + height + ", " +
                "bytes=" + bytes + ", " +
                "format=" + format + ']';
    }

    public static GenericImageData wrap(int width, int height, ByteBuffer data, PixelFormat format) {
        ByteBuffer buffer = MemoryUtil.memAlloc(data.remaining())
                .put(data)
                .flip();

        return new GenericImageData(width, height, buffer, format);
    }

    public static GenericImageData alloc(int width, int height, PixelFormat format) {
        ByteBuffer buffer = MemoryUtil.memAlloc(width * height * format.channels);
        return new GenericImageData(width, height, buffer, format);
    }

}
