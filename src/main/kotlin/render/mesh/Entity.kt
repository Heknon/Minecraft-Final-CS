package render.mesh

import org.joml.Vector3f

class Entity(
    override val position: Vector3f = Vector3f(0f, 0f, 0f),
    override val rotation: Vector3f = Vector3f(0f, 0f, 0f),
    override val scale: Float = 1f,
    override var mesh: Mesh
) : WorldObject