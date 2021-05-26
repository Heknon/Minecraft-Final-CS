package render.mesh

import org.joml.Vector3f

interface WorldObject {
    val position: Vector3f
    val rotation: Vector3f
    val scale: Float
    var mesh: Mesh
}