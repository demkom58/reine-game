package com.crown.graphic.texture;

import com.crown.resource.image.AtlasDimension;
import com.crown.resource.image.AtlasImage;
import com.crown.resource.image.GenericImageData;
import com.crown.util.PixelFormat;
import org.joml.Vector2d;
import org.joml.Vector2f;

import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class TextureAtlas2D extends Texture2D {
    private final int width;
    private final int height;
    private final Map<String, AtlasDimension> positions;

    public TextureAtlas2D(int handle, int width, int height, Map<String, AtlasDimension> positions) {
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
        AtlasDimension pos = positions.get(id);
        return new Vector2f(
                (pos.x() + 1 + x) / (float) width,
                (pos.y() + 1 + y) / (float) height
        );
    }

    public static TextureAtlas2D from(AtlasImage atlas) {
        GenericImageData texture = atlas.getData();

        if (texture == null) {
            throw new IllegalStateException("Failed to load texture!");
        }

        int width = texture.width();
        int height = texture.height();

        int handle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, handle);

        final PixelFormat format = texture.format();
        System.out.println("Atlas format is " + format.name());
        if (format.channels < 4) {
            glPixelStorei(GL_UNPACK_ALIGNMENT, 4 - format.channels);
        }
        glTexImage2D(GL_TEXTURE_2D, 0, format.glType, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, texture.bytes());

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glGenerateMipmap(GL_TEXTURE_2D);
        return new TextureAtlas2D(handle, width, height, atlas.getPositions());
    }
}
