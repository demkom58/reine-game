package com.crown.graphic.gl.texture;

import com.crown.resource.image.GenericImageData;
import org.lwjgl.opengl.NVBindlessTexture;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.NVBindlessTexture.glGetTextureHandleNV;
import static org.lwjgl.opengl.NVBindlessTexture.glMakeTextureHandleResidentNV;

public class GlBindlessTexture extends GlTexture {
    private final long bindlessHandle;

    public GlBindlessTexture(int type, int handle, long bindlessHandle) {
        super(type, handle);
        this.bindlessHandle = bindlessHandle;
    }

    public long getBindlessHandle() {
        return bindlessHandle;
    }

    @Override
    public void destroy() {
        NVBindlessTexture.glMakeImageHandleNonResidentNV(bindlessHandle);
        super.destroy();
    }

    public static GlBindlessTexture from(GenericImageData texture, int anisoLevel) {
        return from(texture, anisoLevel, GL_NEAREST_MIPMAP_LINEAR, GL_NEAREST);
    }

    public static GlBindlessTexture from(GenericImageData texture, int anisoLevel, int minFilter, int magFilter) {
        if (texture == null) {
            throw new IllegalArgumentException("Failed to load null texture!");
        }

        int handle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, handle);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);

        glTexImage2D(GL_TEXTURE_2D, 0, texture.format().glType, texture.width(), texture.height(), 0,
                GL_RGBA, GL_UNSIGNED_BYTE, texture.bytes());

        if (minFilter != GL_NEAREST && minFilter != GL_LINEAR) {
            glGenerateMipmap(GL_TEXTURE_2D);
        }

        float[] aniso = {0.0f};
        glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, aniso);
        aniso[0] = Math.min(aniso[0], anisoLevel);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, aniso[0]);

        long bindlessHandle = glGetTextureHandleNV(handle);
        glMakeTextureHandleResidentNV(bindlessHandle);

        return new GlBindlessTexture(GL_TEXTURE_2D, handle, bindlessHandle);
    }
}
