package shader

import org.lwjgl.opengl.GL20

class VertexShader(
    shaderCode: String,
    programId: Int
) : Shader(
    shaderCode,
    GL20.GL_VERTEX_SHADER,
    programId
) {

}