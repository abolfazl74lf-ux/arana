package com.example.ui.engine.isometric

import androidx.compose.ui.graphics.Color
import com.example.domain.model.RoomType

/**
 * Builds the architectural isometric 3D scene representing the home layout
 * across its 5 primary functional zones:
 * - Yard & Outdoors (Ground level)
 * - Kitchen (Ground Floor, left)
 * - Living Room (Ground Floor, right)
 * - Kids' Room (Upper Floor, left)
 * - Bedroom (Upper Floor, right)
 * Plus foundation, architectural partitions, roof, chimney, and interior furnishings.
 */
object HomeIsometricScene {

    /**
     * Calculates the exact 3D center point for a given room, accounting for
     * upper floor exploded offsets or interactive elevation.
     */
    fun getRoomCenter(
        room: RoomType,
        upperLevelZOffset: Float = 0f,
        selectedRoom: RoomType? = null
    ): IsoPoint3D {
        val isSelected = selectedRoom == room
        val elevation = if (isSelected) 0.35f else 0f

        return when (room) {
            RoomType.YARD -> IsoPoint3D(3.5f, 6.8f, 0.05f + elevation)
            RoomType.KITCHEN -> IsoPoint3D(1.85f, 4.85f, 1.3f + elevation)
            RoomType.LIVING_ROOM -> IsoPoint3D(4.85f, 4.85f, 1.3f + elevation)
            RoomType.KIDS_ROOM -> IsoPoint3D(1.85f, 2.15f, 2.8f + upperLevelZOffset + elevation)
            RoomType.BEDROOM -> IsoPoint3D(4.85f, 2.15f, 2.8f + upperLevelZOffset + elevation)
        }
    }

    /**
     * Populates an [IsoScene] with all architectural blocks, rooms, furniture,
     * roof structures, and garden elements.
     */
    fun buildScene(
        selectedRoom: RoomType?,
        upperLevelZOffset: Float = 0f,
        isRoofVisible: Boolean = true,
        activeRoomLifts: Map<RoomType, Float> = emptyMap()
    ): IsoScene {
        val scene = IsoScene()

        // -------------------------------------------------------------
        // 1. YARD & OUTDOOR SURROUNDINGS (z = 0 to 0.15)
        // -------------------------------------------------------------
        val yardSelected = selectedRoom == RoomType.YARD
        val yardLift = activeRoomLifts[RoomType.YARD] ?: (if (yardSelected) 0.25f else 0f)

        // Wide grass perimeter
        scene.add(
            IsoFloor(
                origin = IsoPoint3D(-0.5f, -0.5f, yardLift),
                width = 8.0f,
                depth = 8.0f,
                color = if (yardSelected) Color(0xFFA5D6A7) else Color(0xFFC8E6C9),
                strokeColor = Color(0xFF81C784),
                strokeWidth = if (yardSelected) 2.2f else 1.2f,
                tag = RoomType.YARD
            )
        )

        // Patio stone deck bordering the entrance
        scene.add(
            IsoFloor(
                origin = IsoPoint3D(0.2f, 6.2f, 0.04f + yardLift),
                width = 6.6f,
                depth = 1.4f,
                color = Color(0xFFD7CCC8),
                strokeColor = Color(0xFFBCAAA4),
                tag = RoomType.YARD
            )
        )

        // Stepping stone walkway in the yard
        for (i in 0..3) {
            scene.add(
                IsoFloor(
                    origin = IsoPoint3D(3.2f + i * 0.7f, 6.4f, 0.06f + yardLift),
                    width = 0.45f,
                    depth = 0.55f,
                    color = Color(0xFFECEFF1),
                    strokeColor = Color(0xFFCFD8DC),
                    tag = RoomType.YARD
                )
            )
        }

        // Flowerbed / garden soil strip
        scene.add(
            IsoBox(
                origin = IsoPoint3D(0.3f, 0.3f, yardLift),
                size = IsoSize3D(6.4f, 0.5f, 0.15f),
                baseColor = Color(0xFF8D6E63),
                topColorOverride = Color(0xFF6D4C41),
                tag = RoomType.YARD
            )
        )

        // Garden shrub / plant bushes in flowerbed
        for (i in 0..4) {
            scene.add(
                IsoBox(
                    origin = IsoPoint3D(0.6f + i * 1.3f, 0.35f, 0.15f + yardLift),
                    size = IsoSize3D(0.5f, 0.4f, 0.35f),
                    baseColor = Color(0xFF4CAF50),
                    topColorOverride = Color(0xFF66BB6A),
                    tag = RoomType.YARD
                )
            )
        }

        // -------------------------------------------------------------
        // 2. GROUND FLOOR FOUNDATION SLAB (z = 0.15)
        // -------------------------------------------------------------
        scene.add(
            IsoBox(
                origin = IsoPoint3D(0.4f, 0.6f, 0.05f),
                size = IsoSize3D(6.2f, 5.8f, 0.15f),
                baseColor = Color(0xFFCFD8DC),
                topColorOverride = Color(0xFFECEFF1)
            )
        )

        // -------------------------------------------------------------
        // 3. GROUND FLOOR: KITCHEN (RoomType.KITCHEN)
        // Bounds: x in 0.5..3.3, y in 3.5..6.3, z in 0.2..1.5
        // -------------------------------------------------------------
        val kitchenSelected = selectedRoom == RoomType.KITCHEN
        val kitchenLift = activeRoomLifts[RoomType.KITCHEN] ?: (if (kitchenSelected) 0.35f else 0f)
        val kZ = 0.2f + kitchenLift

        // Kitchen Room Block
        scene.add(
            IsoBox(
                origin = IsoPoint3D(0.5f, 3.5f, kZ),
                size = IsoSize3D(2.8f, 2.8f, 1.25f),
                baseColor = if (kitchenSelected) Color(0xFFFFB74D) else Color(0xFFFFE0B2),
                topColorOverride = if (kitchenSelected) Color(0xFFFF9800) else Color(0xFFFFF3E0),
                strokeColor = if (kitchenSelected) Color(0xFFF57C00) else Color(0xFFFFCC80),
                isHighlighted = kitchenSelected,
                tag = RoomType.KITCHEN
            )
        )

        // Kitchen L-shaped countertop & sink detail
        scene.add(
            IsoBox(
                origin = IsoPoint3D(0.65f, 3.65f, kZ + 1.25f),
                size = IsoSize3D(1.4f, 0.5f, 0.12f),
                baseColor = Color(0xFFB0BEC5),
                topColorOverride = Color(0xFFECEFF1),
                tag = RoomType.KITCHEN
            )
        )
        // Refrigerator tower
        scene.add(
            IsoBox(
                origin = IsoPoint3D(0.65f, 5.5f, kZ + 1.25f),
                size = IsoSize3D(0.55f, 0.55f, 0.35f),
                baseColor = Color(0xFF78909C),
                topColorOverride = Color(0xFF90A4AE),
                tag = RoomType.KITCHEN
            )
        )

        // -------------------------------------------------------------
        // 4. GROUND FLOOR: LIVING ROOM (RoomType.LIVING_ROOM)
        // Bounds: x in 3.5..6.3, y in 3.5..6.3, z in 0.2..1.5
        // -------------------------------------------------------------
        val livingSelected = selectedRoom == RoomType.LIVING_ROOM
        val livingLift = activeRoomLifts[RoomType.LIVING_ROOM] ?: (if (livingSelected) 0.35f else 0f)
        val lrZ = 0.2f + livingLift

        // Living Room Block
        scene.add(
            IsoBox(
                origin = IsoPoint3D(3.5f, 3.5f, lrZ),
                size = IsoSize3D(2.8f, 2.8f, 1.25f),
                baseColor = if (livingSelected) Color(0xFF81C784) else Color(0xFFC8E6C9),
                topColorOverride = if (livingSelected) Color(0xFF66BB6A) else Color(0xFFE8F5E9),
                strokeColor = if (livingSelected) Color(0xFF388E3C) else Color(0xFFA5D6A7),
                isHighlighted = livingSelected,
                tag = RoomType.LIVING_ROOM
            )
        )

        // Living Room Cozy Sofa
        scene.add(
            IsoBox(
                origin = IsoPoint3D(4.2f, 4.4f, lrZ + 1.25f),
                size = IsoSize3D(1.3f, 0.6f, 0.18f),
                baseColor = Color(0xFF5C6BC0),
                topColorOverride = Color(0xFF7986CB),
                tag = RoomType.LIVING_ROOM
            )
        )
        // Coffee table
        scene.add(
            IsoBox(
                origin = IsoPoint3D(4.5f, 5.2f, lrZ + 1.25f),
                size = IsoSize3D(0.7f, 0.5f, 0.10f),
                baseColor = Color(0xFF8D6E63),
                topColorOverride = Color(0xFFA1887F),
                tag = RoomType.LIVING_ROOM
            )
        )
        // Potted houseplant (Living room botanical accent)
        scene.add(
            IsoBox(
                origin = IsoPoint3D(5.6f, 3.7f, lrZ + 1.25f),
                size = IsoSize3D(0.35f, 0.35f, 0.32f),
                baseColor = Color(0xFF2E7D32),
                topColorOverride = Color(0xFF43A047),
                tag = RoomType.LIVING_ROOM
            )
        )

        // -------------------------------------------------------------
        // 5. INTERIOR SLAB / FLOOR SEPARATOR (Upper level base)
        // -------------------------------------------------------------
        val upperBaseZ = 1.55f + upperLevelZOffset
        scene.add(
            IsoBox(
                origin = IsoPoint3D(0.45f, 0.65f, upperBaseZ),
                size = IsoSize3D(6.1f, 5.7f, 0.12f),
                baseColor = Color(0xFFB0BEC5),
                topColorOverride = Color(0xFFECEFF1)
            )
        )

        // -------------------------------------------------------------
        // 6. UPPER FLOOR: KIDS' ROOM (RoomType.KIDS_ROOM)
        // Bounds: x in 0.5..3.3, y in 0.7..3.4, z in 1.7..2.9
        // -------------------------------------------------------------
        val kidsSelected = selectedRoom == RoomType.KIDS_ROOM
        val kidsLift = activeRoomLifts[RoomType.KIDS_ROOM] ?: (if (kidsSelected) 0.35f else 0f)
        val kdzZ = upperBaseZ + 0.12f + kidsLift

        // Kids Room Block
        scene.add(
            IsoBox(
                origin = IsoPoint3D(0.5f, 0.7f, kdzZ),
                size = IsoSize3D(2.8f, 2.7f, 1.2f),
                baseColor = if (kidsSelected) Color(0xFFFFAB91) else Color(0xFFFFCCBC),
                topColorOverride = if (kidsSelected) Color(0xFFFF8A65) else Color(0xFFFBE9E7),
                strokeColor = if (kidsSelected) Color(0xFFD84315) else Color(0xFFFFAB91),
                isHighlighted = kidsSelected,
                tag = RoomType.KIDS_ROOM
            )
        )

        // Kids Bed & Toys block
        scene.add(
            IsoBox(
                origin = IsoPoint3D(0.8f, 1.0f, kdzZ + 1.2f),
                size = IsoSize3D(0.8f, 1.3f, 0.18f),
                baseColor = Color(0xFF4FC3F7),
                topColorOverride = Color(0xFF81D4FA),
                tag = RoomType.KIDS_ROOM
            )
        )
        // Toy box
        scene.add(
            IsoBox(
                origin = IsoPoint3D(2.0f, 1.1f, kdzZ + 1.2f),
                size = IsoSize3D(0.6f, 0.5f, 0.15f),
                baseColor = Color(0xFFFFD54F),
                topColorOverride = Color(0xFFFFE082),
                tag = RoomType.KIDS_ROOM
            )
        )

        // -------------------------------------------------------------
        // 7. UPPER FLOOR: BEDROOM (RoomType.BEDROOM)
        // Bounds: x in 3.5..6.3, y in 0.7..3.4, z in 1.7..2.9
        // -------------------------------------------------------------
        val bedSelected = selectedRoom == RoomType.BEDROOM
        val bedLift = activeRoomLifts[RoomType.BEDROOM] ?: (if (bedSelected) 0.35f else 0f)
        val bzZ = upperBaseZ + 0.12f + bedLift

        // Master Bedroom Block
        scene.add(
            IsoBox(
                origin = IsoPoint3D(3.5f, 0.7f, bzZ),
                size = IsoSize3D(2.8f, 2.7f, 1.2f),
                baseColor = if (bedSelected) Color(0xFFB39DDB) else Color(0xFFD1C4E9),
                topColorOverride = if (bedSelected) Color(0xFF9575CD) else Color(0xFFEDE7F6),
                strokeColor = if (bedSelected) Color(0xFF512DA8) else Color(0xFFB39DDB),
                isHighlighted = bedSelected,
                tag = RoomType.BEDROOM
            )
        )

        // Master Bed (Double Bed + Pillows)
        scene.add(
            IsoBox(
                origin = IsoPoint3D(4.2f, 1.0f, bzZ + 1.2f),
                size = IsoSize3D(1.3f, 1.4f, 0.18f),
                baseColor = Color(0xFF90A4AE),
                topColorOverride = Color(0xFFCFD8DC),
                tag = RoomType.BEDROOM
            )
        )
        // Wardrobe closet
        scene.add(
            IsoBox(
                origin = IsoPoint3D(5.5f, 1.8f, bzZ + 1.2f),
                size = IsoSize3D(0.5f, 1.1f, 0.45f),
                baseColor = Color(0xFF8D6E63),
                topColorOverride = Color(0xFFA1887F),
                tag = RoomType.BEDROOM
            )
        )

        // -------------------------------------------------------------
        // 8. ROOF STRUCTURE & CHIMNEY (z = upperBaseZ + 1.4 to 2.8)
        // -------------------------------------------------------------
        if (isRoofVisible) {
            val roofBaseZ = upperBaseZ + 1.45f

            // Sloped Gable Roof atop the entire house
            scene.add(
                IsoGableRoof(
                    origin = IsoPoint3D(0.3f, 0.5f, roofBaseZ),
                    width = 6.4f,
                    depth = 6.0f,
                    apexHeight = 1.6f,
                    roofColor = Color(0xFFD32F2F),
                    gableColor = Color(0xFFFFCDD2),
                    strokeColor = Color(0xFFB71C1C)
                )
            )

            // Brick Chimney
            scene.add(
                IsoBox(
                    origin = IsoPoint3D(4.6f, 2.0f, roofBaseZ + 0.6f),
                    size = IsoSize3D(0.6f, 0.6f, 1.4f),
                    baseColor = Color(0xFF795548),
                    topColorOverride = Color(0xFF8D6E63),
                    strokeColor = Color(0xFF4E342E)
                )
            )
        }

        return scene
    }
}
