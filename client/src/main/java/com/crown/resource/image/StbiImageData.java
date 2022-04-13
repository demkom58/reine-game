package com.crown.resource.image;

import com.crown.util.PixelFormat;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;

public class StbiImageData extends GenericImageData {
    public StbiImageData(int width, int height, ByteBuffer bytes, PixelFormat format) {
        super(width, height, bytes, format);
    }

    public static StbiImageData load(File file) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            return load(file, stack.mallocInt(1), stack.mallocInt(1), stack.mallocInt(1));
        }
    }

    public static StbiImageData load(File file, IntBuffer widthBuff, IntBuffer heightBuff, IntBuffer channelBuff) {
        String absolutePath = file.getAbsolutePath();
        if (!file.exists()) {
            throw new IllegalStateException("Texture doesn't exists! Path: " + absolutePath);
        }

        ByteBuffer bytes = stbi_load(absolutePath, widthBuff, heightBuff, channelBuff, 4);
        if (bytes == null) {
            throw new IllegalStateException("Failed to load texture!");
        }

        int width = widthBuff.get();
        int height = heightBuff.get();
        return new StbiImageData(width, height, bytes, PixelFormat.RGBA);
    }

    @Override
    public void destroy() {
        stbi_image_free(bytes());
    }
}
