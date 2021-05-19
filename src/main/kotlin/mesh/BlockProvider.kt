package mesh

import readResourceAsJson
import texture.TextureAtlas
import world.Block

class BlockProvider(private val textureAtlas: TextureAtlas) {
    // Create block meshes from json
    // hand over to block container to be handled by world

    private val blocksJson = "/blocks.json".readResourceAsJson()
    val blocks: MutableMap<Int, Block> = mutableMapOf()

    enum class FaceDirection {
        Top,
        Bottom,
        Left,
        Right,
        Front,
        Back,
        All
    }

    data class BlockFaceMesh(
        val positions: FloatArray,
        val uvs: FloatArray,
        val indices: IntArray,
        val faceDirection: FaceDirection
    )

    private val sides = setOf(
        FaceDirection.Back,
        FaceDirection.Front,
        FaceDirection.Right,
        FaceDirection.Left,
    )

    private val faceOrder = arrayOf(
        FaceDirection.Top,
        FaceDirection.Bottom,
        FaceDirection.Left,
        FaceDirection.Right,
        FaceDirection.Front,
        FaceDirection.Back,
    )

    private val positions = floatArrayOf(
        // TOP FACE
        -0.5f, 0.5f, -0.5f, // left top back
        -0.5f, 0.5f, 0.5f, // left top front
        0.5f, 0.5f, 0.5f, // right top front
        0.5f, 0.5f, -0.5f, // right top back

        // BOTTOM FACE
        -0.5f, -0.5f, -0.5f, // left bottom back
        -0.5f, -0.5f, 0.5f, // left bottom front
        0.5f, -0.5f, 0.5f, // right bottom front
        0.5f, -0.5f, -0.5f, // right bottom back

        // LEFT FACE
        -0.5f, 0.5f, 0.5f, // left top front
        -0.5f, -0.5f, 0.5f, // left bottom front
        -0.5f, -0.5f, -0.5f, // left bottom back
        -0.5f, 0.5f, -0.5f, // left top back

        // RIGHT FACE
        0.5f, 0.5f, 0.5f, // right top front
        0.5f, -0.5f, 0.5f, // right bottom front
        0.5f, -0.5f, -0.5f, // right bottom back
        0.5f, 0.5f, -0.5f, // right top back

        // FRONT FACE
        -0.5f, 0.5f, 0.5f, // left top front
        -0.5f, -0.5f, 0.5f, // left bottom front
        0.5f, -0.5f, 0.5f, // right bottom front
        0.5f, 0.5f, 0.5f, // right top front

        // BACK FACE
        -0.5f, 0.5f, -0.5f, // left top back
        -0.5f, -0.5f, -0.5f, // left bottom back
        0.5f, -0.5f, -0.5f, // right bottom back
        0.5f, 0.5f, -0.5f, // right top back
    )

    init {

        blocksJson.keys.forEach { k ->
            val faces = mutableListOf<BlockFaceMesh>()
            val blockData = blocksJson[k] as Map<*, *>
            val textureData = blockData["texture"] as Map<String, List<Int>>

            for ((j, i) in faceOrder.withIndex()) {
                val pos = FloatArray(12) {
                    positions[j * 12 + it]
                }

                val textureCoords = when {
                    textureData.containsKey("all") ->
                        textureAtlas.getTexture(textureData["all"]!![0], textureData["all"]!![1])
                    textureData.containsKey("sides") && sides.contains(i) ->
                        textureAtlas.getTexture(textureData["sides"]!![0], textureData["sides"]!![1])
                    textureData.containsKey(i.name.toLowerCase()) ->
                        textureAtlas.getTexture(
                            textureData[i.name.toLowerCase()]!![0],
                            textureData[i.name.toLowerCase()]!![1]
                        )
                    else -> textureAtlas.empty()
                }

                val face = BlockFaceMesh(
                    pos,
                    textureCoords.getDefaultUVArray(),
                    intArrayOf(0, 1, 3, 3, 1, 2),
                    i
                )

                println(face)
                faces.add(face)
            }

            val block = Block(
                blockData["id"] as Int,
                blockData["name"] as String,
                faces,
                blockData["collidable"] as Boolean
            )

            blocks[block.id] = block
        }
    }

}