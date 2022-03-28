package com.crown.graphic.texture;

import com.crown.graphic.util.Destroyable;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Texture2D implements Destroyable {
    private final int handle;

    public Texture2D(String path) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer widthBuff = stack.mallocInt(1);
            IntBuffer heightBuff = stack.mallocInt(1);
            IntBuffer channelBuff = stack.mallocInt(1);
            ByteBuffer pixels = stbi_load(path, widthBuff, heightBuff, channelBuff, 0);
            if (pixels == null) {
                throw new IllegalStateException("Failed to load texture!");
            }

            handle = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, handle);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            final int ch = toChannelId(channelBuff.get());
            glTexImage2D(GL_TEXTURE_2D, 0, ch, widthBuff.get(), heightBuff.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
            glGenerateMipmap(GL_TEXTURE_2D);
            stbi_image_free(pixels);
        }
    }

    public Texture2D(String path, int sourceFormat, int shaderFormat, int type) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer widthBuff = stack.mallocInt(1);
            IntBuffer heightBuff = stack.mallocInt(1);
            IntBuffer channelBuff = stack.mallocInt(1);
            ByteBuffer pixels = stbi_load(path, widthBuff, heightBuff, channelBuff, 0);
            if (pixels == null) {
                throw new IllegalStateException("Failed to load texture!");
            }

            handle = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, handle);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            glTexImage2D(GL_TEXTURE_2D, 0, sourceFormat, widthBuff.get(), heightBuff.get(), 0, shaderFormat, type, pixels);
            glGenerateMipmap(GL_TEXTURE_2D);
            stbi_image_free(pixels);
        }
    }

    public int getHandle() {
        return handle;
    }

    @Override
    public void destroy() {
        glDeleteTextures(handle);
    }

    public void use(int index) {
        glActiveTexture(GL_TEXTURE0 + index);
        glBindTexture(GL_TEXTURE_2D, handle);
    }

    private static int toChannelId(int count) {
        return switch (count) {
            case 1 -> GL_RED;
            case 2 -> GL_RG;
            case 3 -> GL_RGB;
            case 4 -> GL_RGBA;
            default -> throw new IllegalStateException("Unexpected value: " + count);
        };
    }
}
