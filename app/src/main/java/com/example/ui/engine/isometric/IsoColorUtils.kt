package com.example.ui.engine.isometric

import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.math.min

/**
 * Lighting and shading utility for the isometric rendering engine.
 * Simulates an architectural isometric directional light source (sunlight coming from top-left).
 */
object IsoColorUtils {

    /**
     * Shading factors for faces:
     * - Top: Direct light (slightly boosted)
     * - Left: Side fill light
     * - Right: Shadow side
     */
    const val TOP_LIGHT_FACTOR = 1.05f
    const val LEFT_LIGHT_FACTOR = 0.88f
    const val RIGHT_LIGHT_FACTOR = 0.72f
    const val BOTTOM_LIGHT_FACTOR = 0.60f

    enum class FaceType {
        TOP,
        LEFT,
        RIGHT,
        BOTTOM,
        SLOPE_LEFT,
        SLOPE_RIGHT
    }

    /**
     * Calculates the shaded color based on the face orientation and optional ambient intensity.
     */
    fun shadeColor(baseColor: Color, faceType: FaceType, ambientBoost: Float = 0f): Color {
        val factor = when (faceType) {
            FaceType.TOP -> min(1.0f, TOP_LIGHT_FACTOR + ambientBoost)
            FaceType.LEFT -> min(1.0f, LEFT_LIGHT_FACTOR + ambientBoost)
            FaceType.RIGHT -> min(1.0f, RIGHT_LIGHT_FACTOR + ambientBoost)
            FaceType.BOTTOM -> min(1.0f, BOTTOM_LIGHT_FACTOR + ambientBoost)
            FaceType.SLOPE_LEFT -> min(1.0f, 0.95f + ambientBoost)
            FaceType.SLOPE_RIGHT -> min(1.0f, 0.80f + ambientBoost)
        }

        return Color(
            red = min(1f, max(0f, baseColor.red * factor)),
            green = min(1f, max(0f, baseColor.green * factor)),
            blue = min(1f, max(0f, baseColor.blue * factor)),
            alpha = baseColor.alpha
        )
    }

    /**
     * Generates a subtle outline/stroke color matching the base color for clean architectural lines.
     */
    fun strokeColor(baseColor: Color, alpha: Float = 0.35f): Color {
        return Color(
            red = baseColor.red * 0.55f,
            green = baseColor.green * 0.55f,
            blue = baseColor.blue * 0.55f,
            alpha = alpha
        )
    }
}
