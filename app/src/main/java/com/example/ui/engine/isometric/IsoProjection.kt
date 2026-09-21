package com.example.ui.engine.isometric

import androidx.compose.ui.geometry.Offset

/**
 * Mathematical isometric projection engine for Jetpack Compose Canvas.
 * Converts 3D world coordinates into 2D screen canvas coordinates using
 * an isometric dimetric matrix with configurable tile dimensions and vertical height scaling.
 */
class IsoProjection(
    val originX: Float,
    val originY: Float,
    val tileWidth: Float,
    val tileHeight: Float,
    val heightScale: Float = tileHeight * 1.0f
) {
    private val halfTileW = tileWidth * 0.5f
    private val halfTileH = tileHeight * 0.5f

    /**
     * Converts a 3D isometric point into a 2D screen coordinate.
     */
    fun toScreen(point: IsoPoint3D): Offset {
        val sx = originX + (point.x - point.y) * halfTileW
        val sy = originY + (point.x + point.y) * halfTileH - point.z * heightScale
        return Offset(sx, sy)
    }

    /**
     * Converts 3D coordinates (x, y, z) directly into a 2D screen coordinate.
     */
    fun toScreen(x: Float, y: Float, z: Float = 0f): Offset {
        val sx = originX + (x - y) * halfTileW
        val sy = originY + (x + y) * halfTileH - z * heightScale
        return Offset(sx, sy)
    }

    /**
     * Inverse projection: maps a 2D screen position to the ground plane (z = elevationZ).
     * Useful for hit-testing or placing objects dynamically.
     */
    fun toIsoGround(screenOffset: Offset, elevationZ: Float = 0f): Pair<Float, Float> {
        val adjustedY = screenOffset.y - originY + elevationZ * heightScale
        val adjustedX = screenOffset.x - originX

        // Solving the system:
        // adjustedX = (x - y) * halfTileW  ==>  (x - y) = adjustedX / halfTileW
        // adjustedY = (x + y) * halfTileH  ==>  (x + y) = adjustedY / halfTileH
        val u = adjustedX / halfTileW
        val v = adjustedY / halfTileH

        val x = (v + u) * 0.5f
        val y = (v - u) * 0.5f
        return Pair(x, y)
    }

    companion object {
        /**
         * Factory function to compute optimal projection parameters that center and scale
         * the house comfortably inside a given Canvas viewport size.
         */
        fun createCentered(
            canvasWidth: Float,
            canvasHeight: Float,
            worldGridUnits: Float = 7.5f,
            verticalOffsetRatio: Float = 0.55f
        ): IsoProjection {
            // Compute base tile dimensions based on viewport bounds
            val baseUnit = minOf(canvasWidth / (worldGridUnits * 2.1f), canvasHeight / (worldGridUnits * 1.8f))
            val tileW = baseUnit * 2.0f
            val tileH = baseUnit * 1.0f
            val heightScale = baseUnit * 1.25f

            val originX = canvasWidth * 0.5f
            val originY = canvasHeight * verticalOffsetRatio

            return IsoProjection(
                originX = originX,
                originY = originY,
                tileWidth = tileW,
                tileHeight = tileH,
                heightScale = heightScale
            )
        }
    }
}
