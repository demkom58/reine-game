package com.crown.graphic.texture;

import com.crown.graphic.util.Destroyable;
import com.crown.resource.image.GenericImageData;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.GL33.*;

public class Texture2D implements Destroyable {
    private final int name;

    public Texture2D(int name) {
        this.name = name;
    }

    public int getName() {
        return name;
    }

    @Override
    public void destroy() {
        glDeleteTextures(name);
    }

    public void use(int index) {
        glActiveTexture(GL_TEXTURE0 + index);
        glBindTexture(GL_TEXTURE_2D, name);
    }

    public static Texture2D from(GenericImageData texture, int anisoLevel) {
        return from(texture, anisoLevel, GL_NEAREST_MIPMAP_LINEAR, GL_NEAREST);
    }

    public static Texture2D from(GenericImageData texture, int anisoLevel, int minFilter, int magFilter) {
        if (texture == null) {
            throw new IllegalStateException("Failed to load texture!");
        }

        int handle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, handle);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);

        float[] aniso = {0.0f};
        glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, aniso);
        aniso[0] = Math.min(aniso[0], anisoLevel);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, aniso[0]);

        glTexImage2D(GL_TEXTURE_2D, 0, texture.format().glType, texture.width(), texture.height(), 0,
                GL_RGBA, GL_UNSIGNED_BYTE, texture.bytes());

        if (minFilter != GL_NEAREST && minFilter != GL_LINEAR) {
            glGenerateMipmap(GL_TEXTURE_2D);
        }

        return new Texture2D(handle);
    }
}
