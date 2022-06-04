#extension GL_NV_bindless_texture   : require
#extension GL_NV_gpu_shader5        : require

layout(binding = 0) uniform Textures {
    uint64_t tex_handles[TEXTURES_COUNT];
};

in flat uint TextureID;
in vec2 UV;

out vec4 FragColor;

void main() {
    sampler2D sampler = sampler2D(tex_handles[TextureID]);
    FragColor = texture(sampler, fract(UV));
}