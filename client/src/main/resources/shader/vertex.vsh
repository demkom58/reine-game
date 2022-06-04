uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

layout (location = 0) in vec3 aPos;
layout (location = 1) in uvec4 aFace;

out vec3 Pos;
out flat uvec4 Face;

void main()
{
    gl_Position = projection * view * model * vec4(aPos, 1.0);

    Pos = aPos;
    Face = aFace;
}