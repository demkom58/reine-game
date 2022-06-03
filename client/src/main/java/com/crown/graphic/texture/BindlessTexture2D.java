package com.crown.graphic.texture;

import com.crown.graphic.util.Destroyable;
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

public class BindlessTexture2D extends Texture2D {
    private final long handle;

    public BindlessTexture2D(int name, long handle) {
        super(name);
        this.handle = handle;
    }


    public long getHandle() {
        return handle;
    }

    @Override
    public void destroy() {
        NVBindlessTexture.glMakeImageHandleNonResidentNV(handle);
        super.destroy();
    }

    public static BindlessTexture2D from(GenericImageData texture) {
        if (texture == null) {
            throw new IllegalStateException("Failed to load texture!");
        }

        int name = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, name);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, texture.format().glType, texture.width(), texture.height(), 0,
                GL_RGBA, GL_UNSIGNED_BYTE, texture.bytes());
        glGenerateMipmap(GL_TEXTURE_2D);

        float[] aniso = {0.0f};
        glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, aniso);
        aniso[0] = Math.min(aniso[0], 4);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, aniso[0]);

        long handle = glGetTextureHandleNV(name);
        glMakeTextureHandleResidentNV(handle);

        return new BindlessTexture2D(name, handle);
    }
}
