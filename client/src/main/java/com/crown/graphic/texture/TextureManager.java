package com.crown.graphic.texture;

import com.crown.resource.image.*;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.io.File;
import java.util.*;

import static org.lwjgl.opengl.ARBUniformBufferObject.GL_UNIFORM_BUFFER;
import static org.lwjgl.opengl.GL33.*;

public class TextureManager {
    private final File texturesRoot = new File("assets/textures/");
    private final Set<ImageResource> textureResources = new HashSet<>();
    private final Map<String, BindlessTexture2D> name2Texture = new HashMap<>();
    private final Object2IntMap<String> name2Id = new Object2IntOpenHashMap<>();

    private int uniBufName = 0;

    public TextureManager() {
    }

    public void registerTexture(String name) {
        textureResources.add(new ImageResource(name));
    }

    public void rebuild() {
        for (ImageResource resource : textureResources) {
            final String name = resource.name();
            final File file = new File(texturesRoot, name);
            try (StbiImageData load = StbiImageData.load(file)) {
                BindlessTexture2D tex = BindlessTexture2D.from(load);
                name2Texture.put(name, tex);
            }
        }

        Collection<BindlessTexture2D> values = name2Texture.values();
        long[] handles = new long[values.size()];

        int[] i = {0};
        name2Texture.forEach((k, v) -> {
            int curr = i[0];

            handles[curr] = v.getHandle();
            name2Id.put(k, curr);

            i[0]++;
        });

        uniBufName = glGenBuffers();
        glBindBuffer(GL_UNIFORM_BUFFER, uniBufName);
        glBufferData(GL_UNIFORM_BUFFER, (long) handles.length * Long.SIZE, GL_STATIC_DRAW);
        glBindBufferRange(GL_UNIFORM_BUFFER, 0, uniBufName, 0, (long) handles.length * Long.SIZE);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, handles);
    }

    public BindlessTexture2D getByName(String name) {
        return name2Texture.get(name);
    }

    public int getId(String name) {
        return name2Id.getInt(name);
    }

    public int texturesCount() {
        return name2Texture.size();
    }

    public int getUniformBufferName() {
        return uniBufName;
    }
}