package com.crown.resource.image;

import com.crown.util.PixelFormat;

import java.awt.*;
import java.awt.image.*;
import java.nio.ByteBuffer;

import static com.crown.resource.image.GenericImageData.LINEAR_RGB;

public class FramedImageData implements ImageData {
    private final ImageData target;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public FramedImageData(ImageData target, ImageDimension dim) {
        this.target = target;
        this.x = dim.x();
        this.y = dim.y();
        this.width = dim.width();
        this.height = dim.height();
    }

    public FramedImageData(ImageData target, int x, int y, int width, int height) {
        this.target = target;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void destroy() {

    }

    @Override
    public int getRGBA(int x, int y) {
        if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
            throw new IndexOutOfBoundsException();
        }

        return target.getRGBA(this.x + x, this.y + y);
    }

    @Override
    public void setRGBA(int x, int y, int rgba) {
        if (x < 0 || x >= this.width || y < 0 || y >= this.height) {
            throw new IndexOutOfBoundsException();
        }

        target.setRGBA(this.x + x, this.y + y, rgba);
    }

    @Override
    public BufferedImage toBufferedImage() {
        final PixelFormat format = target.format();
        final ByteBuffer bytes = target.bytes();
        final ColorModel cm = switch (format) {
            case RGBA -> new ComponentColorModel(
                    LINEAR_RGB, true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
            case RGB -> new ComponentColorModel(
                    LINEAR_RGB, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        };

        WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
        BufferedImage img = new BufferedImage(cm, raster, false, null);

        int[] rgba = new int[4];
        for (int x = this.x; x < width; x++) {
            for (int y = this.y; y < height; y++) {
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
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public ByteBuffer bytes() {
        throw new UnsupportedOperationException("Not possible direct access to bytes of framed image");
    }

    @Override
    public PixelFormat format() {
        return target.format();
    }
}
