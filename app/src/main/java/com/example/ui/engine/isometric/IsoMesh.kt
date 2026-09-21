package com.example.ui.engine.isometric

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * A renderable 2D polygon face in screen space resulting from projected 3D isometric geometry.
 * Holds depth for Painter's algorithm back-to-front sorting.
 */
data class IsoPolygonFace(
    val vertices: List<Offset>,
    val depth: Float,
    val fillColor: Color,
    val strokeColor: Color? = null,
    val strokeWidth: Float = 1.2f,
    val tag: Any? = null
) {
    /**
     * Efficient Jordan curve ray-casting algorithm to test if a screen touch point falls
     * within this isometric polygon face.
     */
    fun contains(point: Offset): Boolean {
        if (vertices.size < 3) return false
        var inside = false
        var j = vertices.size - 1
        for (i in vertices.indices) {
            val vi = vertices[i]
            val vj = vertices[j]
            if ((vi.y > point.y) != (vj.y > point.y) &&
                point.x < (vj.x - vi.x) * (point.y - vi.y) / (vj.y - vi.y) + vi.x
            ) {
                inside = !inside
            }
            j = i
        }
        return inside
    }

    /**
     * Draws the face onto the Compose Canvas DrawScope.
     */
    fun draw(drawScope: DrawScope) {
        if (vertices.size < 3) return
        val path = Path().apply {
            moveTo(vertices[0].x, vertices[0].y)
            for (k in 1 until vertices.size) {
                lineTo(vertices[k].x, vertices[k].y)
            }
            close()
        }

        drawScope.drawPath(path, color = fillColor, style = Fill)
        if (strokeColor != null && strokeWidth > 0f) {
            drawScope.drawPath(path, color = strokeColor, style = Stroke(width = strokeWidth))
        }
    }
}

/**
 * Base interface for renderable 3D isometric entities in the engine scene.
 */
interface IsoEntity {
    val tag: Any?
    fun generateFaces(projection: IsoProjection): List<IsoPolygonFace>
}

/**
 * 3D isometric cuboid / block primitive.
 * Used for rooms, partition walls, furniture, steps, and architectural slabs.
 */
data class IsoBox(
    val origin: IsoPoint3D,
    val size: IsoSize3D,
    val baseColor: Color,
    val topColorOverride: Color? = null,
    val strokeColor: Color? = null,
    val strokeWidth: Float = 1.2f,
    val isHighlighted: Boolean = false,
    override val tag: Any? = null
) : IsoEntity {

    override fun generateFaces(projection: IsoProjection): List<IsoPolygonFace> {
        val faces = ArrayList<IsoPolygonFace>(3)

        val x0 = origin.x
        val x1 = origin.x + size.width
        val y0 = origin.y
        val y1 = origin.y + size.depth
        val z0 = origin.z
        val z1 = origin.z + size.height

        // Calculate 8 isometric corners
        val p000 = projection.toScreen(x0, y0, z0)
        val p100 = projection.toScreen(x1, y0, z0)
        val p110 = projection.toScreen(x1, y1, z0)
        val p010 = projection.toScreen(x0, y1, z0)

        val p001 = projection.toScreen(x0, y0, z1)
        val p101 = projection.toScreen(x1, y0, z1)
        val p111 = projection.toScreen(x1, y1, z1)
        val p011 = projection.toScreen(x0, y1, z1)

        val depthBase = (x0 + x1) * 0.5f + (y0 + y1) * 0.5f + (z0 + z1) * 0.5f
        val stroke = if (isHighlighted) Color.White.copy(alpha = 0.85f) else strokeColor

        // 1. Top face (visible if z1 > 0)
        val topFill = topColorOverride ?: IsoColorUtils.shadeColor(
            baseColor,
            IsoColorUtils.FaceType.TOP,
            if (isHighlighted) 0.15f else 0f
        )
        faces.add(
            IsoPolygonFace(
                vertices = listOf(p001, p101, p111, p011),
                depth = depthBase + 0.3f,
                fillColor = topFill,
                strokeColor = stroke,
                strokeWidth = if (isHighlighted) 2.2f else strokeWidth,
                tag = tag
            )
        )

        // 2. Left face (front-left side facing positive X/Z axis)
        val leftFill = IsoColorUtils.shadeColor(
            baseColor,
            IsoColorUtils.FaceType.LEFT,
            if (isHighlighted) 0.10f else 0f
        )
        faces.add(
            IsoPolygonFace(
                vertices = listOf(p010, p110, p111, p011),
                depth = depthBase + 0.1f,
                fillColor = leftFill,
                strokeColor = stroke,
                strokeWidth = strokeWidth,
                tag = tag
            )
        )

        // 3. Right face (front-right side facing positive Y/Z axis)
        val rightFill = IsoColorUtils.shadeColor(
            baseColor,
            IsoColorUtils.FaceType.RIGHT,
            if (isHighlighted) 0.10f else 0f
        )
        faces.add(
            IsoPolygonFace(
                vertices = listOf(p100, p110, p111, p101),
                depth = depthBase + 0.2f,
                fillColor = rightFill,
                strokeColor = stroke,
                strokeWidth = strokeWidth,
                tag = tag
            )
        )

        return faces
    }
}

/**
 * 2D isometric floor slab on a given horizontal plane (e.g. lawn, patio, carpet, room floor).
 */
data class IsoFloor(
    val origin: IsoPoint3D,
    val width: Float,
    val depth: Float,
    val color: Color,
    val strokeColor: Color? = null,
    val strokeWidth: Float = 1.0f,
    override val tag: Any? = null
) : IsoEntity {
    override fun generateFaces(projection: IsoProjection): List<IsoPolygonFace> {
        val p0 = projection.toScreen(origin.x, origin.y, origin.z)
        val p1 = projection.toScreen(origin.x + width, origin.y, origin.z)
        val p2 = projection.toScreen(origin.x + width, origin.y + depth, origin.z)
        val p3 = projection.toScreen(origin.x, origin.y + depth, origin.z)

        val depth = origin.x + width * 0.5f + origin.y + depth * 0.5f + origin.z
        return listOf(
            IsoPolygonFace(
                vertices = listOf(p0, p1, p2, p3),
                depth = depth,
                fillColor = color,
                strokeColor = strokeColor,
                strokeWidth = strokeWidth,
                tag = tag
            )
        )
    }
}

/**
 * 3D isometric gable roof primitive with two sloped pitch faces and two gable end triangles.
 */
data class IsoGableRoof(
    val origin: IsoPoint3D,
    val width: Float,
    val depth: Float,
    val apexHeight: Float,
    val roofColor: Color,
    val gableColor: Color,
    val strokeColor: Color? = null,
    override val tag: Any? = null
) : IsoEntity {
    override fun generateFaces(projection: IsoProjection): List<IsoPolygonFace> {
        val faces = ArrayList<IsoPolygonFace>(3)
        val x0 = origin.x
        val x1 = origin.x + width
        val y0 = origin.y
        val y1 = origin.y + depth
        val zBase = origin.z
        val zApex = origin.z + apexHeight

        val midY = (y0 + y1) * 0.5f

        // Corners at base
        val b00 = projection.toScreen(x0, y0, zBase)
        val b10 = projection.toScreen(x1, y0, zBase)
        val b11 = projection.toScreen(x1, y1, zBase)
        val b01 = projection.toScreen(x0, y1, zBase)

        // Apex points (ridge of the roof running along X axis)
        val a0 = projection.toScreen(x0, midY, zApex)
        val a1 = projection.toScreen(x1, midY, zApex)

        val avgDepth = (x0 + x1) * 0.5f + midY + zApex

        // 1. South sloped pitch face (front-facing)
        faces.add(
            IsoPolygonFace(
                vertices = listOf(b01, b11, a1, a0),
                depth = avgDepth + 0.3f,
                fillColor = IsoColorUtils.shadeColor(roofColor, IsoColorUtils.FaceType.SLOPE_LEFT),
                strokeColor = strokeColor,
                strokeWidth = 1.5f,
                tag = tag
            )
        )

        // 2. North sloped pitch face
        faces.add(
            IsoPolygonFace(
                vertices = listOf(b00, b10, a1, a0),
                depth = avgDepth - 0.2f,
                fillColor = IsoColorUtils.shadeColor(roofColor, IsoColorUtils.FaceType.SLOPE_RIGHT),
                strokeColor = strokeColor,
                strokeWidth = 1.5f,
                tag = tag
            )
        )

        // 3. Gable triangle on front-right side (x1)
        faces.add(
            IsoPolygonFace(
                vertices = listOf(b10, b11, a1),
                depth = avgDepth + 0.2f,
                fillColor = IsoColorUtils.shadeColor(gableColor, IsoColorUtils.FaceType.RIGHT),
                strokeColor = strokeColor,
                strokeWidth = 1.5f,
                tag = tag
            )
        )

        return faces
    }
}
