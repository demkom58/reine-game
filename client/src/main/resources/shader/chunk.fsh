#extension GL_ARB_gpu_shader_int64      : require
#extension GL_ARB_bindless_texture      : require

layout(binding = 3) uniform Textures {
    uint64_t tex_handles[TEXTURES_COUNT];
};

in vec3 Pos;
in flat ivec3 Face;
in flat int Texture;

out vec4 FragColor;

layout(location = 2) uniform float uAlphaThreshold;

void main() {
    float u = dot(Face.yxz, Pos.xzx);
    float v = dot(Face.yxz, Pos.zyy);
    vec2 tileUV = fract(abs(vec2(u, v)));

    sampler2D sampler = sampler2D(tex_handles[Texture]);
    vec4 texel = texture(sampler, tileUV);

    if (texel.a < uAlphaThreshold) {
        discard;
    }

    FragColor = texel;
}