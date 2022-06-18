package com.crown.graphic.gl.texture;

import com.crown.resource.image.ImageDimension;
import com.crown.resource.image.AtlasImage;
import com.crown.resource.image.GenericImageData;
import com.crown.util.PixelFormat;
import org.joml.Vector2f;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class GlTexture2dAtlas extends GlTexture {
    private final int width;
    private final int height;
    private final Map<String, ImageDimension> positions;

    public GlTexture2dAtlas(int handle, int width, int height, Map<String, ImageDimension> positions) {
        super(GL_TEXTURE_2D, handle);
        this.width = width;
        this.height = height;
        this.positions = positions;
    }

    public float u(String id, int x) {
        return (positions.get(id).x() + x) / (float) width;
    }

    public float v(String id, int v) {
        return (positions.get(id).y() + v) / (float) height;
    }

    public Vector2f uv(String id, int x, int y) {
        ImageDimension pos = positions.get(id);
        return new Vector2f(
                (pos.x() + 1 + x) / (float) width,
                (pos.y() + 1 + y) / (float) height
        );
    }

    public static GlTexture2dAtlas from(AtlasImage atlasImage, int anisoLevel) {
        return from(Collections.singletonList(atlasImage), anisoLevel, GL_NEAREST_MIPMAP_LINEAR, GL_NEAREST);
    }

    public static GlTexture2dAtlas from(List<AtlasImage> atlasMipMaps, int anisoLevel, int minFilter, int magFilter) {
        AtlasImage sourceAtlas = atlasMipMaps.get(0);
        GenericImageData texture = sourceAtlas.getData();

        if (texture == null) {
            throw new IllegalStateException("Failed to load texture!");
        }

        int width = texture.width();
        int height = texture.height();

        int handle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, handle);

        float[] aniso = {0.0f};
        glGetFloatv(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, aniso);
        aniso[0] = Math.min(aniso[0], anisoLevel);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, aniso[0]);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, minFilter);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, magFilter);

        if (atlasMipMaps.size() == 1) {
            glGenerateMipmap(GL_TEXTURE_2D);
        } else {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, atlasMipMaps.size() - 1);
        }

        for (int i = 0; i < atlasMipMaps.size(); i++) {
            final GenericImageData data = atlasMipMaps.get(i).getData();
            final PixelFormat format = data.format();
            if (format.channels < 4) {
                glPixelStorei(GL_UNPACK_ALIGNMENT, 4 - format.channels);
            }

            glTexImage2D(GL_TEXTURE_2D, i, format.glType,
                    data.width(), data.height(), 0, GL_RGBA, GL_UNSIGNED_BYTE, data.bytes());
        }

        return new GlTexture2dAtlas(handle, width, height, sourceAtlas.getPositions());
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
