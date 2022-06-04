#extension GL_ARB_gpu_shader_int64      : require
#extension GL_ARB_bindless_texture      : require

layout(binding = 0) uniform Textures {
    uint64_t tex_handles[TEXTURES_COUNT];
};

in vec3 Pos;
in flat uvec4 Face;

out vec4 FragColor;

void main() {
    float u = dot(Face.yxz, Pos.xzx);
    float v = dot(Face.yxz, Pos.zyy);
    vec2 tileUV = fract(vec2(u, v));

    sampler2D sampler = sampler2D(tex_handles[Face.w]);

    FragColor = texture(sampler, tileUV);
}