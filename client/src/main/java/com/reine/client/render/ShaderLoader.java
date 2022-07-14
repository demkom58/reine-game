package com.reine.client.render;

import com.crown.graphic.gl.GraphicsLibrary;
import com.crown.graphic.gl.shader.GlShader;
import com.crown.graphic.gl.shader.GlShaderProgram;
import com.crown.graphic.gl.shader.ShaderType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ShaderLoader {
    private final String glslVersion;

    public ShaderLoader() {
        this(GraphicsLibrary.OGL_VERSION.glslVersion());
    }

    public ShaderLoader(int glslVersion) {
        this.glslVersion = "#version " + glslVersion + " core";
    }

    public GlShaderProgram loadResource(String vertex, String fragment, Collection<String> defines) {
        List<String> shaderDefines = new ArrayList<>(defines.size() + 1);
        shaderDefines.add(glslVersion);
        for (String define : defines) {
            shaderDefines.add("#define " + define);
        }

        try (GlShader v = new GlShader(ShaderType.VERTEX, getClass().getResource(vertex), shaderDefines);
             GlShader f = new GlShader(ShaderType.FRAGMENT, getClass().getResource(fragment), shaderDefines)) {
            return new GlShaderProgram(v, f);
        }
    }

    public GlShaderProgram loadResource(String vertex, String fragment) {
        List<String> shaderDefines = List.of(glslVersion);
        try (GlShader v = new GlShader(ShaderType.VERTEX, getClass().getResource(vertex), shaderDefines);
             GlShader f = new GlShader(ShaderType.FRAGMENT, getClass().getResource(fragment), shaderDefines)) {
            return new GlShaderProgram(v, f);
        }
    }
}
