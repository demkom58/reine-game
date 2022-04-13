package com.crown.graphic.texture;

import com.crown.graphic.util.Destroyable;
import com.crown.resource.image.GenericImageData;

import static org.lwjgl.opengl.GL33.*;

public class Texture2D implements Destroyable {
    private final int handle;

    public Texture2D(int handle) {
        this.handle = handle;
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

    public static Texture2D from(GenericImageData texture) {
        if (texture == null) {
            throw new IllegalStateException("Failed to load texture!");
        }

        int handle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, handle);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, texture.format().glType, texture.width(), texture.height(), 0,
                GL_RGBA, GL_UNSIGNED_BYTE, texture.bytes());
        glGenerateMipmap(GL_TEXTURE_2D);

        return new Texture2D(handle);
    }
}
