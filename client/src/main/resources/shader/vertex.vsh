#version 330 core

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aFace;
layout (location = 2) in vec4 aTexCoords;
layout (location = 3) in vec4 aLight;

out vec3 Pos;
out vec3 Face;

out vec4 TexCoords;

void main()
{
    gl_Position = projection * view * model * vec4(aPos, 1.0);

    Pos = aPos;
    Face = aFace;

    TexCoords = aTexCoords;
}