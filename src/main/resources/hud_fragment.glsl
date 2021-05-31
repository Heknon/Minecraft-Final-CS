#version 330

in vec2 exTexCoord;

uniform sampler2D crosshair_texture_sampler;
uniform mat4 projectionMatrix;

out vec4 fragColor;

void main() {
    vec4 textureColor = texture(crosshair_texture_sampler, exTexCoord);
    if (textureColor.a < 0.05)
        discard;
    fragColor = vec4(textureColor);
}
