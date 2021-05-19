package shader

import org.lwjgl.opengl.GL20.*

abstract class Shader(shaderCode: String, shaderType: Int, private val programId: Int) {
    val shaderId: Int = glCreateShader(shaderType)

    init {
        if (shaderId == 0) {
            throw RuntimeException("Error creating shader. Type: $shaderType, Code: $shaderCode")
        }

        glShaderSource(shaderId, shaderCode)
        glCompileShader(shaderId)

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw RuntimeException("Error compiling shader: ${glGetShaderInfoLog(shaderId, 1024)}")
        }

        glAttachShader(programId, shaderId)
    }

    fun detach() {
        glDetachShader(programId, shaderId)
    }
}