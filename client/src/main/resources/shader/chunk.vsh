layout(binding = 0) uniform Models {
    mat4 models[BATCH_CHUNK_COUNT];
};

layout(location = 0) uniform mat4 view;
layout(location = 1) uniform mat4 projection;

layout(location = 0) in ivec3 aPos;
layout(location = 1) in ivec3 aFace;
layout(location = 2) in int aTexture;

out vec3 Pos;
out flat ivec3 Face;
out flat int Texture;

void main()
{
    gl_Position = projection * view * models[gl_DrawID] * vec4(aPos, 1.0);

    Pos = aPos;
    Face = aFace;
    Texture = aTexture;
}