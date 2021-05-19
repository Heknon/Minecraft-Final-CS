#version 330

in vec2 exTexCoord;

uniform mat4 projectionMatrix;

uniform sampler2D texture_sampler;

out vec4 fragColor;

void main() {
    vec4 textureColor = texture(texture_sampler, exTexCoord);
    if(textureColor.a < 0.05)
        discard;
    fragColor = texture(texture_sampler, exTexCoord);
}
