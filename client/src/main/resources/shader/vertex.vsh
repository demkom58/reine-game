uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aUV;
layout (location = 2) in uint aTextureID;

out flat uint TextureID;
out vec2 UV;

void main()
{
    gl_Position = projection * view * model * vec4(aPos, 1.0);
    TextureID = aTextureID;
    UV = aUV;
}