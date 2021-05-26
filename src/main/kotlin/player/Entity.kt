package player

import org.joml.Vector3f
import render.mesh.WorldObject
interface Entity : WorldObject {
    val velocity: Vector3f
}