package com.example

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.domain.model.RoomType
import com.example.ui.engine.isometric.IsoBox
import com.example.ui.engine.isometric.IsoColorUtils
import com.example.ui.engine.isometric.IsoPoint3D
import com.example.ui.engine.isometric.IsoPolygonFace
import com.example.ui.engine.isometric.IsoProjection
import com.example.ui.engine.isometric.IsoScene
import com.example.ui.engine.isometric.IsoSize3D
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class IsometricEngineTest {

    @Test
    fun testProjectionToScreen() {
        val projection = IsoProjection(
            originX = 200f,
            originY = 200f,
            tileWidth = 40f,
            tileHeight = 20f,
            heightScale = 25f
        )

        // Point at origin (0, 0, 0) should project exactly to (originX, originY)
        val p0 = projection.toScreen(0f, 0f, 0f)
        assertEquals(200f, p0.x, 0.001f)
        assertEquals(200f, p0.y, 0.001f)

        // Point elevated along Z axis should move upwards (negative screen Y)
        val pZ = projection.toScreen(0f, 0f, 2f)
        assertEquals(200f, pZ.x, 0.001f)
        assertEquals(200f - 2f * 25f, pZ.y, 0.001f)

        // Inverse ground plane projection round trip
        val (gx, gy) = projection.toIsoGround(Offset(200f, 200f), elevationZ = 0f)
        assertEquals(0f, gx, 0.001f)
        assertEquals(0f, gy, 0.001f)
    }

    @Test
    fun testPolygonContains() {
        // A diamond shape (typical isometric face)
        val diamondFace = IsoPolygonFace(
            vertices = listOf(
                Offset(100f, 50f),
                Offset(150f, 75f),
                Offset(100f, 100f),
                Offset(50f, 75f)
            ),
            depth = 10f,
            fillColor = Color.Blue
        )

        // Center should be inside
        assertTrue(diamondFace.contains(Offset(100f, 75f)))
        // Outside point should be false
        assertFalse(diamondFace.contains(Offset(10f, 10f)))
        assertFalse(diamondFace.contains(Offset(200f, 200f)))
    }

    @Test
    fun testIsoColorShading() {
        val base = Color(0xFF808080) // 50% gray
        val top = IsoColorUtils.shadeColor(base, IsoColorUtils.FaceType.TOP)
        val left = IsoColorUtils.shadeColor(base, IsoColorUtils.FaceType.LEFT)
        val right = IsoColorUtils.shadeColor(base, IsoColorUtils.FaceType.RIGHT)

        // Top face is brightest, followed by left fill light, then right shadow face
        assertTrue("Top face should be brighter than left face", top.red > left.red)
        assertTrue("Left face should be brighter than right face", left.red > right.red)
    }

    @Test
    fun testIsoSceneHitTest() {
        val projection = IsoProjection(
            originX = 200f,
            originY = 200f,
            tileWidth = 60f,
            tileHeight = 30f,
            heightScale = 30f
        )

        val scene = IsoScene()
        val box = IsoBox(
            origin = IsoPoint3D(1f, 1f, 0f),
            size = IsoSize3D(2f, 2f, 1f),
            baseColor = Color.Red,
            tag = RoomType.KITCHEN
        )
        scene.add(box)

        // Center of the top face in 3D: (2f, 2f, 1f)
        val topCenterScreen = projection.toScreen(2f, 2f, 1f)
        val hitTag = scene.hitTest(topCenterScreen, projection)

        assertNotNull("Should hit the box top face", hitTag)
        assertEquals(RoomType.KITCHEN, hitTag)
    }
}
