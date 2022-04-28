package com.crown.graphic.texture;

import com.crown.resource.image.ImageDimension;
import com.crown.resource.image.AtlasImage;
import com.crown.resource.image.GenericImageData;
import com.crown.util.PixelFormat;
import org.joml.Vector2f;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class TextureAtlas2D extends Texture2D {
    private final int width;
    private final int height;
    private final Map<String, ImageDimension> positions;

    public TextureAtlas2D(int handle, int width, int height, Map<String, ImageDimension> positions) {
        super(handle);
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

    public static TextureAtlas2D from(AtlasImage atlasImage) {
        return from(Collections.singletonList(atlasImage));
    }

    public static TextureAtlas2D from(List<AtlasImage> atlasMipMaps) {
        AtlasImage sourceAtlas = atlasMipMaps.get(0);
        GenericImageData texture = sourceAtlas.getData();

        if (texture == null) {
            throw new IllegalStateException("Failed to load texture!");
        }

        int width = texture.width();
        int height = texture.height();

        int handle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, handle);

        for (int i = 0; i < atlasMipMaps.size(); i++) {
            final GenericImageData data = atlasMipMaps.get(i).getData();
            final PixelFormat format = data.format();
            if (format.channels < 4) {
                glPixelStorei(GL_UNPACK_ALIGNMENT, 4 - format.channels);
            }

            glTexImage2D(GL_TEXTURE_2D, i, format.glType,
                    data.width(), data.height(), 0, GL_RGBA, GL_UNSIGNED_BYTE, data.bytes());
        }

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        if (atlasMipMaps.size() == 1) {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glGenerateMipmap(GL_TEXTURE_2D);
        } else {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, atlasMipMaps.size() - 1);
        }

        return new TextureAtlas2D(handle, width, height, sourceAtlas.getPositions());
    }

    @Override
    public int getHandle() {
        return super.getHandle();
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
