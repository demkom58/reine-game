package com.crown.util;

import com.crown.resource.image.ImageData;
import com.crown.resource.image.ImageDimension;
import com.crown.resource.image.ImageInfo;

import java.util.Collection;
import java.util.Comparator;

public final class ImageUtil {
    public static void insert(ImageData texture, ImageDimension dim, ImageData location) {
        final int width = texture.width();
        final int height = texture.height();

        final int tX = dim.x();
        final int tY = dim.y();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgba = texture.getRGBA(x, y);
                location.setRGBA(tX + x, tY + y, rgba);
            }
        }
    }

    public static void copy(ImageData from, ImageData to) {
        final int width = from.width();
        final int height = from.height();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                to.setRGBA(x, y, from.getRGBA(x, y));
            }
        }
    }

    public static PixelFormat fitFormat(Collection<ImageInfo> textures) {
        return textures.stream()
                .max(Comparator.comparingInt(a -> a.format().channels))
                .map(ImageInfo::format)
                .orElseThrow();
    }


}
