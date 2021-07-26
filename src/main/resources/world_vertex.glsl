#version 330

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoord;
layout (location=2) in vec4 color;

uniform mat4 projectionMatrix;
uniform mat4 modelView;

out vec2 exTexCoord;
out vec4 exColor;

void main() {
    exTexCoord = texCoord;

    gl_Position = projectionMatrix * modelView * vec4(position, 1.0);
}
