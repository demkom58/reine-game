uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aTexture;

out flat uint TextureID;
out vec2 UV;

void main()
{
    gl_Position = projection * view * model * vec4(aPos, 1.0);
    TextureID = uint(aTexture.x);
    UV = aTexture.yz;
}