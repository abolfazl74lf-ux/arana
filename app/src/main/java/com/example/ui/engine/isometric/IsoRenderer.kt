package com.example.ui.engine.isometric

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Scene container and renderer for isometric 3D spaces in Jetpack Compose Canvas.
 */
class IsoScene {
    private val entities = mutableListOf<IsoEntity>()

    fun clear() {
        entities.clear()
    }

    fun add(entity: IsoEntity) {
        entities.add(entity)
    }

    fun addAll(vararg entityList: IsoEntity) {
        entities.addAll(entityList)
    }

    fun addAll(items: Collection<IsoEntity>) {
        entities.addAll(items)
    }

    /**
     * Renders the entire isometric scene to the provided Compose Canvas DrawScope.
     * Implements Painter's algorithm: generates projected screen polygons, sorts them
     * back-to-front by depth, and renders each face sequentially.
     * Returns the list of sorted faces for hit-testing and coordinate anchoring.
     */
    fun render(
        drawScope: DrawScope,
        projection: IsoProjection
    ): List<IsoPolygonFace> {
        // 1. Generate all faces from entities
        val allFaces = ArrayList<IsoPolygonFace>(entities.size * 3)
        for (entity in entities) {
            allFaces.addAll(entity.generateFaces(projection))
        }

        // 2. Sort faces using Painter's algorithm (lower depth drawn first, higher depth on top)
        allFaces.sortBy { it.depth }

        // 3. Draw sorted faces
        for (face in allFaces) {
            face.draw(drawScope)
        }

        return allFaces
    }

    /**
     * Hit tests a 2D screen tap offset against the scene's entities.
     * Iterates from topmost face (highest depth) down to lowest depth.
     * Returns the tag (e.g. RoomType) of the first entity hit, or null if none was clicked.
     */
    fun hitTest(
        tapOffset: Offset,
        projection: IsoProjection
    ): Any? {
        val allFaces = ArrayList<IsoPolygonFace>(entities.size * 3)
        for (entity in entities) {
            allFaces.addAll(entity.generateFaces(projection))
        }

        // Search front-to-back (highest depth first)
        allFaces.sortByDescending { it.depth }

        for (face in allFaces) {
            if (face.tag != null && face.contains(tapOffset)) {
                return face.tag
            }
        }
        return null
    }
}
