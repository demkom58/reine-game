package com.crown.resource.image;

import com.crown.util.PixelFormat;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.IntBuffer;

public record ImageInfo(File file, int width, int height, PixelFormat format) {
    public static ImageInfo read(File file) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            return read(file, stack.mallocInt(1), stack.mallocInt(1), stack.mallocInt(1));
        }
    }

    public static ImageInfo read(File file, IntBuffer width, IntBuffer height, IntBuffer channels) {
        if (!STBImage.stbi_info(file.getAbsolutePath(), width, height, channels)) {
            throw new IllegalArgumentException("Failed to read image '" + file.getAbsolutePath() + "'!");
        }

        return new ImageInfo(file, width.get(), height.get(), switch (channels.get()) {
            case 4 -> PixelFormat.RGBA;
            case 3 -> PixelFormat.RGB;
            default -> throw new IllegalArgumentException("Unsupported format of image '" + file.getAbsolutePath() + "'!");
        });
    }
}
