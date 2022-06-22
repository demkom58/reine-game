package com.reine.client;

import com.crown.graphic.gl.buffer.GlMutableBuffer;
import com.crown.graphic.gl.texture.GlBindlessTexture;
import com.crown.resource.image.ImageResource;
import com.crown.resource.image.StbiImageData;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.LongBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.lwjgl.opengl.ARBUniformBufferObject.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL33.*;

public class TextureManager {
    private final File texturesRoot = new File("assets/textures/");
    private final Set<ImageResource> textureResources = new HashSet<>();
    private final Map<String, GlBindlessTexture> name2Texture = new HashMap<>();
    private final Object2IntMap<String> name2Id = new Object2IntOpenHashMap<>();

    private GlMutableBuffer texIdUniBuf = null;

    public TextureManager() {
    }

    public void registerTexture(String name) {
        textureResources.add(new ImageResource(name));
    }

    public void rebuild() {
        if (texIdUniBuf != null) {
            texIdUniBuf.destroy();
            texIdUniBuf = null;
        }

        name2Texture.clear();
        name2Id.clear();

        for (ImageResource resource : textureResources) {
            final String name = resource.name();
            final File file = new File(texturesRoot, name);
            try (StbiImageData load = StbiImageData.load(file)) {
                name2Texture.put(name, GlBindlessTexture.from(load, 4));
            }
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            final int size = textureResources.size();
            final LongBuffer handles = stack.callocLong(size);

            int i = 0;
            for (var entry : name2Texture.entrySet()) {
                handles.put(entry.getValue().getBindlessHandle());
                name2Id.put(entry.getKey(), i);
                i++;
            }
            handles.flip();

            texIdUniBuf = new GlMutableBuffer(GL_STATIC_DRAW);
            texIdUniBuf.bind(GL_UNIFORM_BUFFER);
            texIdUniBuf.allocate(GL_UNIFORM_BUFFER, (long) handles.remaining() * Long.BYTES);
            texIdUniBuf.bindRange(GL_UNIFORM_BUFFER, 3, 0, (long) handles.remaining() * Long.BYTES);
            texIdUniBuf.upload(GL_UNIFORM_BUFFER, 0, handles);
            texIdUniBuf.unbind(GL_UNIFORM_BUFFER);
        }
    }

    public GlBindlessTexture getByName(String name) {
        return name2Texture.get(name);
    }

    public int getId(String name) {
        return name2Id.getInt(name);
    }

    public int texturesCount() {
        return name2Texture.size();
    }
}