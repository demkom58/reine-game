package com.crown.resource.image;

import com.crown.graphic.util.Destroyable;
import com.crown.util.PixelFormat;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public interface ImageData extends Destroyable {
    int getRGBA(int x, int y);

    void setRGBA(int x, int y, int rgba);

    BufferedImage toBufferedImage();

    int width();

    int height();

    ByteBuffer bytes();

    PixelFormat format();
}
