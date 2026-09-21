package com.example.ui.engine.isometric

import androidx.compose.ui.geometry.Offset

/**
 * Represents a point in 3D isometric space.
 * X: Extends to the right-down in isometric projection (along axis 1)
 * Y: Extends to the left-down in isometric projection (along axis 2)
 * Z: Extends vertically upwards (elevation / height)
 */
data class IsoPoint3D(
    val x: Float,
    val y: Float,
    val z: Float = 0f
) {
    operator fun plus(other: IsoPoint3D): IsoPoint3D =
        IsoPoint3D(x + other.x, y + other.y, z + other.z)

    operator fun times(scale: Float): IsoPoint3D =
        IsoPoint3D(x * scale, y * scale, z * scale)
}

/**
 * Holds dimensions for a 3D isometric volume (bounding box / room block).
 */
data class IsoSize3D(
    val width: Float,   // Size along X axis
    val depth: Float,   // Size along Y axis
    val height: Float   // Size along Z axis
)
