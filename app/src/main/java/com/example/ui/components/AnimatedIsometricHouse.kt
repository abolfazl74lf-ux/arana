package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Roofing
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.domain.model.RoomType
import com.example.ui.home.HomeOverviewStats

/**
 * Premium Architectural 3D Isometric Smart House.
 *
 * Designed with two distinct, meticulously crafted states:
 * 1. Collapsed Mode (حالت یکپارچه):
 *    - A single, solid, beautiful 3D architectural villa.
 *    - No gaps, no floating disconnected cubicles, no cluttering labels.
 *    - Concrete foundation & grass lawn, stone path leading to the wooden entrance door,
 *      ground floor with large panoramic windows, upper floor with bedroom/kids windows,
 *      and an authentic terracotta hip roof with an isometric 3D chimney.
 *
 * 2. Exploded View (حالت تفکیک طبقات):
 *    - Triggered via the "تفکیک طبقات" button or tapping the house.
 *    - Smooth spring physics separate the house into 5 floating architectural modules:
 *      * سقف شیروانی و دودکش (Roof lifts upward)
 *      * اتاق خواب و کودک (Upper floor separates diagonally up)
 *      * پذیرایی و آشپزخانه (Ground floor separates diagonally down)
 *      * حیاط و فونداسیون (Yard & lawn drop downward)
 *    - Dashed architectural projection guide lines.
 *    - ONLY in exploded mode: Room labels gracefully fade in beside each piece with live alerts,
 *      chore counters, and direct tap-to-open navigation.
 */
@Composable
fun AnimatedIsometricHouse(
    stats: HomeOverviewStats,
    selectedRoom: RoomType?,
    onRoomClick: (RoomType) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExploded by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current
    val isDark = isSystemInDarkTheme()

    // Spring physics spec for snappy, physical separation
    val springSpec = spring<Dp>(dampingRatio = 0.74f, stiffness = 300f)

    // Offsets for Exploded View
    val roofOffsetY by animateDpAsState(targetValue = if (isExploded) (-95).dp else 0.dp, animationSpec = springSpec, label = "roofY")
    val bedOffsetX by animateDpAsState(targetValue = if (isExploded) (-80).dp else 0.dp, animationSpec = springSpec, label = "bedX")
    val bedOffsetY by animateDpAsState(targetValue = if (isExploded) (-35).dp else 0.dp, animationSpec = springSpec, label = "bedY")
    val kidsOffsetX by animateDpAsState(targetValue = if (isExploded) 80.dp else 0.dp, animationSpec = springSpec, label = "kidsX")
    val kidsOffsetY by animateDpAsState(targetValue = if (isExploded) (-35).dp else 0.dp, animationSpec = springSpec, label = "kidsY")
    val livingOffsetX by animateDpAsState(targetValue = if (isExploded) (-85).dp else 0.dp, animationSpec = springSpec, label = "livingX")
    val livingOffsetY by animateDpAsState(targetValue = if (isExploded) 45.dp else 0.dp, animationSpec = springSpec, label = "livingY")
    val kitchenOffsetX by animateDpAsState(targetValue = if (isExploded) 85.dp else 0.dp, animationSpec = springSpec, label = "kitchenX")
    val kitchenOffsetY by animateDpAsState(targetValue = if (isExploded) 45.dp else 0.dp, animationSpec = springSpec, label = "kitchenY")
    val yardOffsetY by animateDpAsState(targetValue = if (isExploded) 105.dp else 0.dp, animationSpec = springSpec, label = "yardY")

    // Alert beacon pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "housePulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    val explosionProgress by animateFloatAsState(
        targetValue = if (isExploded) 1f else 0f,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "explosionProgress"
    )

    // Theme-aware container colors
    val cardBackground = if (isDark) Color(0xFF1E2430) else Color(0xFFFFFDF9)
    val stageGradient = if (isDark) {
        listOf(Color(0xFF1E293B), Color(0xFF131B2B), Color(0xFF0B101D))
    } else {
        listOf(Color(0xFFFFFFFF), Color(0xFFF9F7F2), Color(0xFFEDE7DA))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .testTag("animated_isometric_house_card"),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Control Header: Title, Status Badge & Toggle Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "مدل تعاملی خانه",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isExploded) Color(0xFF4338CA).copy(alpha = 0.15f) else MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = if (isExploded) "نمای تفکیک‌شده" else "حالت یکپارچه",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                                color = if (isExploded) Color(0xFF6366F1) else MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = if (isExploded) "قطعات تفکیک شده • برای جزئیات روی هر بخش کلیک کنید" else "برای مشاهده ساختار داخلی و طبقات، دکمه تفکیک را بزنید",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Interactive Toggle Button: «تفکیک طبقات / تجمیع خانه»
                FilterChip(
                    selected = isExploded,
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        isExploded = !isExploded
                    },
                    label = {
                        Text(
                            text = if (isExploded) "تجمیع خانه" else "تفکیک طبقات",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isExploded) Icons.Default.CloseFullscreen else Icons.Default.Layers,
                            contentDescription = null,
                            modifier = Modifier.size(17.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF4338CA),
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Color.White,
                        containerColor = if (isDark) Color(0xFF283246) else Color(0xFFEEF2FF),
                        labelColor = if (isDark) Color(0xFFA5B4FC) else Color(0xFF4338CA),
                        iconColor = if (isDark) Color(0xFFA5B4FC) else Color(0xFF4338CA)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (isExploded) Color(0xFF3730A3) else Color(0xFF818CF8).copy(alpha = 0.4f)),
                    modifier = Modifier.testTag("btn_toggle_exploded_view")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main 3D Isometric Stage Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(390.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.radialGradient(stageGradient))
                    .clickable {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        isExploded = !isExploded
                    },
                contentAlignment = Alignment.Center
            ) {
                // =========================================================
                // 1. COLLAPSED VIEW: A SINGLE SOLID UNIFIED 3D VILLA
                // =========================================================
                if (!isExploded) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        DrawAssembledHouse(
                            isDark = isDark,
                            selectedRoom = selectedRoom,
                            stats = stats,
                            pulseAlpha = pulseAlpha
                        )

                        // Subtle bottom pill inviting interaction
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = (if (isDark) Color(0xFF0F172A) else Color.White).copy(alpha = 0.85f),
                            border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TouchApp,
                                    contentDescription = null,
                                    tint = if (isDark) Color(0xFFA5B4FC) else Color(0xFF4338CA),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "برای تفکیک طبقات کلیک کنید",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF475569)
                                )
                            }
                        }
                    }
                }

                // =========================================================
                // 2. EXPLODED VIEW: SEPARATED ARCHITECTURAL FLOORS & LABELS
                // =========================================================
                if (isExploded) {
                    // Dashed architectural guide lines connecting separated floors
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 7f), 0f)
                        val strokeColor = (if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)).copy(alpha = 0.5f * explosionProgress)

                        // To Roof (up)
                        drawLine(
                            color = strokeColor,
                            start = Offset(centerX, centerY - 20.dp.toPx()),
                            end = Offset(centerX, centerY - 95.dp.toPx()),
                            strokeWidth = 1.8.dp.toPx(),
                            pathEffect = pathEffect
                        )
                        // To Bedroom (up-left)
                        drawLine(
                            color = strokeColor,
                            start = Offset(centerX, centerY - 15.dp.toPx()),
                            end = Offset(centerX - 80.dp.toPx(), centerY - 35.dp.toPx()),
                            strokeWidth = 1.8.dp.toPx(),
                            pathEffect = pathEffect
                        )
                        // To Kids Room (up-right)
                        drawLine(
                            color = strokeColor,
                            start = Offset(centerX, centerY - 15.dp.toPx()),
                            end = Offset(centerX + 80.dp.toPx(), centerY - 35.dp.toPx()),
                            strokeWidth = 1.8.dp.toPx(),
                            pathEffect = pathEffect
                        )
                        // To Living Room (down-left)
                        drawLine(
                            color = strokeColor,
                            start = Offset(centerX, centerY + 15.dp.toPx()),
                            end = Offset(centerX - 85.dp.toPx(), centerY + 45.dp.toPx()),
                            strokeWidth = 1.8.dp.toPx(),
                            pathEffect = pathEffect
                        )
                        // To Kitchen (down-right)
                        drawLine(
                            color = strokeColor,
                            start = Offset(centerX, centerY + 15.dp.toPx()),
                            end = Offset(centerX + 85.dp.toPx(), centerY + 45.dp.toPx()),
                            strokeWidth = 1.8.dp.toPx(),
                            pathEffect = pathEffect
                        )
                        // To Yard (down)
                        drawLine(
                            color = strokeColor,
                            start = Offset(centerX, centerY + 25.dp.toPx()),
                            end = Offset(centerX, centerY + 105.dp.toPx()),
                            strokeWidth = 1.8.dp.toPx(),
                            pathEffect = pathEffect
                        )
                    }

                    // LAYER 1: YARD & FOUNDATION (Bottom)
                    Box(
                        modifier = Modifier
                            .offset(y = yardOffsetY)
                            .zIndex(1f)
                            .testTag("isometric_layer_yard"),
                        contentAlignment = Alignment.Center
                    ) {
                        DrawYard(
                            isSelected = selectedRoom == RoomType.YARD,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onRoomClick(RoomType.YARD)
                            }
                        )

                        ExplodedRoomLabel(
                            title = "حیاط و فونداسیون",
                            subtitle = if (stats.yardMaintenancePendingCount > 0) "${stats.yardMaintenancePendingCount} مورد رسیدگی" else "فضای سبز مرتب",
                            color = Color(0xFF16A34A),
                            icon = Icons.Default.Grass,
                            isSelected = selectedRoom == RoomType.YARD,
                            badgeOffsetX = 0.dp,
                            badgeOffsetY = 44.dp,
                            onClick = { onRoomClick(RoomType.YARD) }
                        )
                    }

                    // LAYER 2A: GROUND FLOOR - LIVING ROOM (Down-Left)
                    Box(
                        modifier = Modifier
                            .offset(x = livingOffsetX, y = livingOffsetY)
                            .zIndex(3f)
                            .testTag("isometric_layer_living_room"),
                        contentAlignment = Alignment.Center
                    ) {
                        DrawLivingRoom(
                            isSelected = selectedRoom == RoomType.LIVING_ROOM,
                            hasAlert = stats.plantsNeedingWaterCount > 0,
                            pulseAlpha = pulseAlpha,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onRoomClick(RoomType.LIVING_ROOM)
                            }
                        )

                        ExplodedRoomLabel(
                            title = "پذیرایی و نشیمن",
                            subtitle = if (stats.plantsNeedingWaterCount > 0) "💧 ${stats.plantsNeedingWaterCount} گیاه تشنه" else "گل‌ها شاداب",
                            color = Color(0xFF0D9488),
                            icon = Icons.Default.Weekend,
                            isAlert = stats.plantsNeedingWaterCount > 0,
                            isSelected = selectedRoom == RoomType.LIVING_ROOM,
                            badgeOffsetX = (-42).dp,
                            badgeOffsetY = 24.dp,
                            onClick = { onRoomClick(RoomType.LIVING_ROOM) }
                        )
                    }

                    // LAYER 2B: GROUND FLOOR - KITCHEN (Down-Right)
                    Box(
                        modifier = Modifier
                            .offset(x = kitchenOffsetX, y = kitchenOffsetY)
                            .zIndex(4f)
                            .testTag("isometric_layer_kitchen"),
                        contentAlignment = Alignment.Center
                    ) {
                        DrawKitchen(
                            isSelected = selectedRoom == RoomType.KITCHEN,
                            hasAlert = stats.kitchenExpiringCount > 0,
                            pulseAlpha = pulseAlpha,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onRoomClick(RoomType.KITCHEN)
                            }
                        )

                        ExplodedRoomLabel(
                            title = "آشپزخانه و ورودی",
                            subtitle = if (stats.kitchenExpiringCount > 0) "⚠️ ${stats.kitchenExpiringCount} انقضا نزدیک" else "موجودی منظم",
                            color = Color(0xFFE11D48),
                            icon = Icons.Default.Kitchen,
                            isAlert = stats.kitchenExpiringCount > 0,
                            isSelected = selectedRoom == RoomType.KITCHEN,
                            badgeOffsetX = 42.dp,
                            badgeOffsetY = 24.dp,
                            onClick = { onRoomClick(RoomType.KITCHEN) }
                        )
                    }

                    // LAYER 3A: UPPER FLOOR - BEDROOM (Up-Left)
                    Box(
                        modifier = Modifier
                            .offset(x = bedOffsetX, y = bedOffsetY)
                            .zIndex(5f)
                            .testTag("isometric_layer_bedroom"),
                        contentAlignment = Alignment.Center
                    ) {
                        DrawBedroom(
                            isSelected = selectedRoom == RoomType.BEDROOM,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onRoomClick(RoomType.BEDROOM)
                            }
                        )

                        ExplodedRoomLabel(
                            title = "اتاق خواب اصلی",
                            subtitle = if (stats.bedroomPendingRoutinesCount > 0) "${stats.bedroomPendingRoutinesCount} روتین منتظر" else "آرام و مرتب",
                            color = Color(0xFF4F46E5),
                            icon = Icons.Default.Bed,
                            isSelected = selectedRoom == RoomType.BEDROOM,
                            badgeOffsetX = (-42).dp,
                            badgeOffsetY = (-26).dp,
                            onClick = { onRoomClick(RoomType.BEDROOM) }
                        )
                    }

                    // LAYER 3B: UPPER FLOOR - KIDS ROOM (Up-Right)
                    Box(
                        modifier = Modifier
                            .offset(x = kidsOffsetX, y = kidsOffsetY)
                            .zIndex(6f)
                            .testTag("isometric_layer_kids_room"),
                        contentAlignment = Alignment.Center
                    ) {
                        DrawKidsRoom(
                            isSelected = selectedRoom == RoomType.KIDS_ROOM,
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onRoomClick(RoomType.KIDS_ROOM)
                            }
                        )

                        ExplodedRoomLabel(
                            title = "اتاق کودک و کار",
                            subtitle = if (stats.kidsPendingChores > 0) "${stats.kidsPendingChores} کار باقیمانده" else "امتیاز کامل",
                            color = Color(0xFFD97706),
                            icon = Icons.Default.ChildCare,
                            isSelected = selectedRoom == RoomType.KIDS_ROOM,
                            badgeOffsetX = 42.dp,
                            badgeOffsetY = (-26).dp,
                            onClick = { onRoomClick(RoomType.KIDS_ROOM) }
                        )
                    }

                    // LAYER 4: ROOF & CHIMNEY (Topmost)
                    Box(
                        modifier = Modifier
                            .offset(y = roofOffsetY)
                            .zIndex(10f)
                            .testTag("isometric_layer_roof"),
                        contentAlignment = Alignment.Center
                    ) {
                        DrawRoof(
                            onClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                isExploded = false
                            }
                        )

                        // Roof Badge in exploded mode
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFC2410C),
                            shadowElevation = 4.dp,
                            modifier = Modifier.offset(y = (-36).dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Roofing,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "سقف شیروانی و دودکش",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// 1. ASSEMBLED HOUSE COMPOSABLE (حالت یکپارچه: خانه سه‌بعدی کامل و پیوسته)
// =========================================================================
@Composable
private fun DrawAssembledHouse(
    isDark: Boolean,
    selectedRoom: RoomType?,
    stats: HomeOverviewStats,
    pulseAlpha: Float
) {
    Canvas(
        modifier = Modifier
            .size(width = 240.dp, height = 230.dp)
            .testTag("canvas_assembled_house")
    ) {
        val cx = size.width / 2f
        val cyYard = size.height / 2f + 40.dp.toPx()

        // -------------------------------------------------------------
        // 1. YARD & FOUNDATION (قاعده حیاط چمن و فونداسیون)
        // -------------------------------------------------------------
        val yardW = 90.dp.toPx()
        val yardH = 40.dp.toPx()
        val foundH = 12.dp.toPx()

        // Lawn Rhombus
        val lawn = Path().apply {
            moveTo(cx, cyYard - yardH)
            lineTo(cx + yardW, cyYard)
            lineTo(cx, cyYard + yardH)
            lineTo(cx - yardW, cyYard)
            close()
        }
        drawPath(
            path = lawn,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF86EFAC), Color(0xFF4ADE80), Color(0xFF16A34A)),
                start = Offset(cx, cyYard - yardH),
                end = Offset(cx, cyYard + yardH)
            )
        )
        drawPath(path = lawn, color = Color(0xFF15803D), style = Stroke(1.5.dp.toPx()))

        // 3D Foundation Concrete edges
        val foundLeft = Path().apply {
            moveTo(cx - yardW, cyYard)
            lineTo(cx, cyYard + yardH)
            lineTo(cx, cyYard + yardH + foundH)
            lineTo(cx - yardW, cyYard + foundH)
            close()
        }
        drawPath(path = foundLeft, color = Color(0xFF64748B))

        val foundRight = Path().apply {
            moveTo(cx, cyYard + yardH)
            lineTo(cx + yardW, cyYard)
            lineTo(cx + yardW, cyYard + foundH)
            lineTo(cx, cyYard + yardH + foundH)
            close()
        }
        drawPath(path = foundRight, color = Color(0xFF475569))

        // Stone Pathway leading to the front door
        val stonePathOffsets = listOf(
            Offset(cx + 6.dp.toPx(), cyYard + 25.dp.toPx()),
            Offset(cx + 16.dp.toPx(), cyYard + 18.dp.toPx()),
            Offset(cx + 26.dp.toPx(), cyYard + 11.dp.toPx()),
            Offset(cx + 36.dp.toPx(), cyYard + 4.dp.toPx())
        )
        stonePathOffsets.forEach { stone ->
            val sw = 7.dp.toPx()
            val sh = 3.5.dp.toPx()
            val stoneP = Path().apply {
                moveTo(stone.x, stone.y - sh)
                lineTo(stone.x + sw, stone.y)
                lineTo(stone.x, stone.y + sh)
                lineTo(stone.x - sw, stone.y)
                close()
            }
            drawPath(path = stoneP, color = Color(0xFFF1F5F9))
            drawPath(path = stoneP, color = Color(0xFF94A3B8), style = Stroke(1.dp.toPx()))
        }

        // Garden Shrub & Flowers on Left grass
        drawCircle(color = Color(0xFF15803D), radius = 6.dp.toPx(), center = Offset(cx - 52.dp.toPx(), cyYard - 4.dp.toPx()))
        drawCircle(color = Color(0xFFF43F5E), radius = 2.5.dp.toPx(), center = Offset(cx - 54.dp.toPx(), cyYard - 4.dp.toPx()))
        drawCircle(color = Color(0xFFFACC15), radius = 2.5.dp.toPx(), center = Offset(cx - 36.dp.toPx(), cyYard + 12.dp.toPx()))

        // -------------------------------------------------------------
        // 2. HOUSE WALLS (دیوارهای یکپارچه طبقه همکف و اول)
        // -------------------------------------------------------------
        val houseBaseY = cyYard - 8.dp.toPx()
        val hw = 52.dp.toPx()
        val hh = 26.dp.toPx()
        val hGround = 32.dp.toPx()
        val hUpper = 28.dp.toPx()
        val hTotal = hGround + hUpper

        // Left Exterior Wall (Illuminated Stucco)
        val leftWall = Path().apply {
            moveTo(cx - hw, houseBaseY - hh)
            lineTo(cx, houseBaseY)
            lineTo(cx, houseBaseY - hTotal)
            lineTo(cx - hw, houseBaseY - hh - hTotal)
            close()
        }
        drawPath(
            path = leftWall,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFFFFFF), Color(0xFFFDFBF7), Color(0xFFF5EFE6)),
                start = Offset(cx - hw, houseBaseY - hh - hTotal),
                end = Offset(cx, houseBaseY)
            )
        )
        drawPath(path = leftWall, color = Color(0xFFD4C7B5), style = Stroke(1.2.dp.toPx()))

        // Right Exterior Wall (Shaded Stucco)
        val rightWall = Path().apply {
            moveTo(cx, houseBaseY)
            lineTo(cx + hw, houseBaseY - hh)
            lineTo(cx + hw, houseBaseY - hh - hTotal)
            lineTo(cx, houseBaseY - hTotal)
            close()
        }
        drawPath(
            path = rightWall,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFEBE3D5), Color(0xFFDDD3C2)),
                start = Offset(cx, houseBaseY - hTotal),
                end = Offset(cx + hw, houseBaseY - hh)
            )
        )
        drawPath(path = rightWall, color = Color(0xFFBFAF9B), style = Stroke(1.2.dp.toPx()))

        // Horizontal Cornice Trim dividing Ground & Upper floor
        drawLine(
            color = Color(0xFFC7B8A3),
            start = Offset(cx - hw, houseBaseY - hh - hGround),
            end = Offset(cx, houseBaseY - hGround),
            strokeWidth = 1.5.dp.toPx()
        )
        drawLine(
            color = Color(0xFFB5A48E),
            start = Offset(cx, houseBaseY - hGround),
            end = Offset(cx + hw, houseBaseY - hh - hGround),
            strokeWidth = 1.5.dp.toPx()
        )

        // -------------------------------------------------------------
        // 3. ARCHITECTURAL OPENINGS (در ورودی و پنجره‌ها)
        // -------------------------------------------------------------
        // A. Wooden Entrance Door on Right Wall (در ورودی چوبی)
        drawIsometricWoodenDoor(
            wallBottomX = cx,
            wallRightX = cx + hw,
            wallBottomY = houseBaseY,
            wallH = hGround,
            offsetX = 10.dp.toPx(),
            doorWidth = 15.dp.toPx(),
            doorHeight = 22.dp.toPx()
        )

        // B. Kitchen Window next to the door
        drawIsometricWindowOnRightWall(
            wallBottomX = cx,
            wallRightX = cx + hw,
            wallBottomY = houseBaseY,
            wallH = hGround,
            offsetX = 30.dp.toPx(),
            offsetY = 6.dp.toPx(),
            winWidth = 13.dp.toPx(),
            winHeight = 13.dp.toPx()
        )

        // C. Panoramic Living Room Window on Left Wall (پنجره قدی)
        drawIsometricWindowOnLeftWall(
            wallLeftX = cx - hw,
            wallBottomX = cx,
            wallBottomY = houseBaseY,
            wallH = hGround,
            offsetX = 12.dp.toPx(),
            offsetY = 4.dp.toPx(),
            winWidth = 24.dp.toPx(),
            winHeight = 22.dp.toPx(),
            isFloorToCeiling = true
        )

        // D. Bedroom Window on Upper Left Wall (پنجره اتاق خواب)
        drawIsometricWindowOnLeftWall(
            wallLeftX = cx - hw,
            wallBottomX = cx,
            wallBottomY = houseBaseY - hGround,
            wallH = hUpper,
            offsetX = 14.dp.toPx(),
            offsetY = 6.dp.toPx(),
            winWidth = 16.dp.toPx(),
            winHeight = 14.dp.toPx()
        )

        // E. Kids Room Window on Upper Right Wall (پنجره اتاق کودک)
        drawIsometricWindowOnRightWall(
            wallBottomX = cx,
            wallRightX = cx + hw,
            wallBottomY = houseBaseY - hGround,
            wallH = hUpper,
            offsetX = 18.dp.toPx(),
            offsetY = 6.dp.toPx(),
            winWidth = 16.dp.toPx(),
            winHeight = 14.dp.toPx()
        )

        // -------------------------------------------------------------
        // 4. ROOF & CHIMNEY (سقف شیروانی سفالی و دودکش ۳بعدی)
        // -------------------------------------------------------------
        val roofBaseY = houseBaseY - hTotal
        val eavesOverhang = 8.dp.toPx()
        val rLeft = Offset(cx - hw - eavesOverhang, roofBaseY - hh + 2.dp.toPx())
        val rBottom = Offset(cx, roofBaseY + 5.dp.toPx())
        val rRight = Offset(cx + hw + eavesOverhang, roofBaseY - hh + 2.dp.toPx())
        val rBack = Offset(cx, roofBaseY - 2 * hh - 2.dp.toPx())

        val ridgeTop = Offset(cx - 10.dp.toPx(), roofBaseY - hh - 28.dp.toPx())
        val ridgeBottom = Offset(cx + 26.dp.toPx(), roofBaseY - hh - 10.dp.toPx())

        // Ambient Occlusion shadow under eaves onto upper wall
        val eavesShadow = Path().apply {
            moveTo(rLeft.x, rLeft.y)
            lineTo(rBottom.x, rBottom.y)
            lineTo(rBottom.x, rBottom.y + 4.dp.toPx())
            lineTo(rLeft.x, rLeft.y + 4.dp.toPx())
            close()
        }
        drawPath(path = eavesShadow, color = Color(0xFF7C2D12).copy(alpha = 0.8f))

        // Left Slope (Illuminated Terracotta)
        val leftSlope = Path().apply {
            moveTo(ridgeTop.x, ridgeTop.y)
            lineTo(ridgeBottom.x, ridgeBottom.y)
            lineTo(rBottom.x, rBottom.y)
            lineTo(rLeft.x, rLeft.y)
            close()
        }
        drawPath(
            path = leftSlope,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFB923C), Color(0xFFEA580C), Color(0xFFC2410C)),
                start = ridgeTop,
                end = rBottom
            )
        )
        drawPath(path = leftSlope, color = Color(0xFF9A3412), style = Stroke(1.5.dp.toPx()))

        // Decorative tile ridges
        for (i in 1..4) {
            val t = i / 5f
            val start = Offset(
                ridgeTop.x + (ridgeBottom.x - ridgeTop.x) * t,
                ridgeTop.y + (ridgeBottom.y - ridgeTop.y) * t
            )
            val end = Offset(
                rLeft.x + (rBottom.x - rLeft.x) * t,
                rLeft.y + (rBottom.y - rLeft.y) * t
            )
            drawLine(
                color = Color(0xFFFED7AA).copy(alpha = 0.5f),
                start = start,
                end = end,
                strokeWidth = 1.dp.toPx()
            )
        }

        // Right Slope (Shaded Terracotta)
        val rightSlope = Path().apply {
            moveTo(ridgeTop.x, ridgeTop.y)
            lineTo(rBack.x, rBack.y)
            lineTo(rRight.x, rRight.y)
            lineTo(ridgeBottom.x, ridgeBottom.y)
            close()
        }
        drawPath(
            path = rightSlope,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF9A3412), Color(0xFF7C2D12)),
                start = ridgeBottom,
                end = rRight
            )
        )
        drawPath(path = rightSlope, color = Color(0xFF5A1D0B), style = Stroke(1.5.dp.toPx()))

        // 3D Chimney on roof slope
        val chimX = cx - 22.dp.toPx()
        val chimY = roofBaseY - hh - 12.dp.toPx()
        val cw = 9.dp.toPx()
        val ch = 16.dp.toPx()
        val cd = 7.dp.toPx()

        val cLeft = Path().apply {
            moveTo(chimX - cw, chimY - 4.dp.toPx())
            lineTo(chimX, chimY)
            lineTo(chimX, chimY - ch)
            lineTo(chimX - cw, chimY - ch - 4.dp.toPx())
            close()
        }
        drawPath(path = cLeft, color = Color(0xFF64748B))
        drawPath(path = cLeft, color = Color(0xFF334155), style = Stroke(1.dp.toPx()))

        val cRight = Path().apply {
            moveTo(chimX, chimY)
            lineTo(chimX + cd, chimY - 4.dp.toPx())
            lineTo(chimX + cd, chimY - ch - 4.dp.toPx())
            lineTo(chimX, chimY - ch)
            close()
        }
        drawPath(path = cRight, color = Color(0xFF475569))
        drawPath(path = cRight, color = Color(0xFF1E293B), style = Stroke(1.dp.toPx()))

        val cTop = Path().apply {
            moveTo(chimX, chimY - ch)
            lineTo(chimX + cd, chimY - ch - 4.dp.toPx())
            lineTo(chimX + cd - cw, chimY - ch - 8.dp.toPx())
            lineTo(chimX - cw, chimY - ch - 4.dp.toPx())
            close()
        }
        drawPath(path = cTop, color = Color(0xFF334155))
        drawCircle(color = Color(0xFF0F172A), radius = 2.dp.toPx(), center = Offset(chimX, chimY - ch - 4.dp.toPx()))

        // Rising smoke puffs
        drawCircle(color = Color(0xFFE2E8F0).copy(alpha = 0.55f), radius = 3.dp.toPx(), center = Offset(chimX - 2.dp.toPx(), chimY - ch - 11.dp.toPx()))
        drawCircle(color = Color(0xFFE2E8F0).copy(alpha = 0.35f), radius = 4.2.dp.toPx(), center = Offset(chimX - 6.dp.toPx(), chimY - ch - 18.dp.toPx()))

        // Subtle alert rings if active in the house
        if (stats.plantsNeedingWaterCount > 0 || stats.kitchenExpiringCount > 0) {
            val alertPos = Offset(cx, houseBaseY - hGround / 2f)
            drawCircle(
                color = Color.Red.copy(alpha = pulseAlpha * 0.3f),
                radius = 18.dp.toPx(),
                center = alertPos
            )
        }
    }
}

// =========================================================================
// 2. SEPARATE DRAWING MODULES FOR EXPLODED VIEW
// =========================================================================

@Composable
fun DrawRoof(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Canvas(
        modifier = modifier
            .size(width = 160.dp, height = 100.dp)
            .clickable(onClick = onClick)
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f + 2.dp.toPx()

        val ridgeTop = Offset(cx - 10.dp.toPx(), cy - 28.dp.toPx())
        val ridgeBottom = Offset(cx + 26.dp.toPx(), cy - 10.dp.toPx())
        val eavesLeft = Offset(cx - 62.dp.toPx(), cy + 2.dp.toPx())
        val eavesBottom = Offset(cx - 24.dp.toPx(), cy + 24.dp.toPx())
        val eavesRight = Offset(cx + 52.dp.toPx(), cy + 12.dp.toPx())
        val eavesBack = Offset(cx + 16.dp.toPx(), cy - 24.dp.toPx())

        // Under-eaves shadow
        val underEaves = Path().apply {
            moveTo(eavesLeft.x, eavesLeft.y)
            lineTo(eavesBottom.x, eavesBottom.y)
            lineTo(eavesBottom.x, eavesBottom.y + 4.dp.toPx())
            lineTo(eavesLeft.x, eavesLeft.y + 4.dp.toPx())
            close()
        }
        drawPath(path = underEaves, color = Color(0xFF7C2D12))

        val underEavesR = Path().apply {
            moveTo(eavesBottom.x, eavesBottom.y)
            lineTo(eavesRight.x, eavesRight.y)
            lineTo(eavesRight.x, eavesRight.y + 4.dp.toPx())
            lineTo(eavesBottom.x, eavesBottom.y + 4.dp.toPx())
            close()
        }
        drawPath(path = underEavesR, color = Color(0xFF5A1D0B))

        // Left Slope
        val leftSlope = Path().apply {
            moveTo(ridgeTop.x, ridgeTop.y)
            lineTo(ridgeBottom.x, ridgeBottom.y)
            lineTo(eavesBottom.x, eavesBottom.y)
            lineTo(eavesLeft.x, eavesLeft.y)
            close()
        }
        drawPath(
            path = leftSlope,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFFFB923C), Color(0xFFEA580C), Color(0xFFC2410C)),
                start = ridgeTop,
                end = eavesBottom
            )
        )
        drawPath(path = leftSlope, color = Color(0xFF9A3412), style = Stroke(1.5.dp.toPx()))

        for (i in 1..4) {
            val t = i / 5f
            drawLine(
                color = Color(0xFFFED7AA).copy(alpha = 0.5f),
                start = Offset(ridgeTop.x + (ridgeBottom.x - ridgeTop.x) * t, ridgeTop.y + (ridgeBottom.y - ridgeTop.y) * t),
                end = Offset(eavesLeft.x + (eavesBottom.x - eavesLeft.x) * t, eavesLeft.y + (eavesBottom.y - eavesLeft.y) * t),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Right Slope
        val rightSlope = Path().apply {
            moveTo(ridgeTop.x, ridgeTop.y)
            lineTo(eavesBack.x, eavesBack.y)
            lineTo(eavesRight.x, eavesRight.y)
            lineTo(ridgeBottom.x, ridgeBottom.y)
            close()
        }
        drawPath(
            path = rightSlope,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF9A3412), Color(0xFF7C2D12)),
                start = ridgeBottom,
                end = eavesRight
            )
        )
        drawPath(path = rightSlope, color = Color(0xFF5A1D0B), style = Stroke(1.5.dp.toPx()))

        // Chimney
        val chimX = cx - 22.dp.toPx()
        val chimY = cy - 10.dp.toPx()
        val cw = 9.dp.toPx()
        val ch = 17.dp.toPx()
        val cd = 7.dp.toPx()

        val chimLeft = Path().apply {
            moveTo(chimX - cw, chimY - 4.dp.toPx())
            lineTo(chimX, chimY)
            lineTo(chimX, chimY - ch)
            lineTo(chimX - cw, chimY - ch - 4.dp.toPx())
            close()
        }
        drawPath(path = chimLeft, color = Color(0xFF64748B))

        val chimRight = Path().apply {
            moveTo(chimX, chimY)
            lineTo(chimX + cd, chimY - 4.dp.toPx())
            lineTo(chimX + cd, chimY - ch - 4.dp.toPx())
            lineTo(chimX, chimY - ch)
            close()
        }
        drawPath(path = chimRight, color = Color(0xFF475569))

        val chimTop = Path().apply {
            moveTo(chimX, chimY - ch)
            lineTo(chimX + cd, chimY - ch - 4.dp.toPx())
            lineTo(chimX + cd - cw, chimY - ch - 8.dp.toPx())
            lineTo(chimX - cw, chimY - ch - 4.dp.toPx())
            close()
        }
        drawPath(path = chimTop, color = Color(0xFF334155))
        drawCircle(color = Color(0xFF0F172A), radius = 2.dp.toPx(), center = Offset(chimX, chimY - ch - 4.dp.toPx()))

        // Smoke
        drawCircle(color = Color(0xFFE2E8F0).copy(alpha = 0.55f), radius = 3.dp.toPx(), center = Offset(chimX - 2.dp.toPx(), chimY - ch - 11.dp.toPx()))
        drawCircle(color = Color(0xFFE2E8F0).copy(alpha = 0.35f), radius = 4.dp.toPx(), center = Offset(chimX - 6.dp.toPx(), chimY - ch - 18.dp.toPx()))
    }
}

@Composable
fun DrawBedroom(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Canvas(
        modifier = modifier
            .size(width = 115.dp, height = 95.dp)
            .clickable(onClick = onClick)
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f + 4.dp.toPx()
        val w = 40.dp.toPx()
        val h = 21.dp.toPx()
        val wallH = 26.dp.toPx()

        drawIsometricBuildingBlock(
            cx = cx, cy = cy, w = w, h = h, wallH = wallH,
            floorColor = Color(0xFFEDE9FE),
            leftWallColor = Color(0xFFFBF9F5),
            rightWallColor = Color(0xFFE5DDD0),
            borderColor = if (isSelected) Color(0xFF4338CA) else Color(0xFFB5A998),
            isSelected = isSelected
        )

        drawIsometricWindowOnLeftWall(
            wallLeftX = cx - w, wallBottomX = cx, wallBottomY = cy + h, wallH = wallH,
            offsetX = 10.dp.toPx(), offsetY = 6.dp.toPx(), winWidth = 14.dp.toPx(), winHeight = 13.dp.toPx()
        )

        // Bed inside
        val bedX = cx - 8.dp.toPx()
        val bedY = cy - wallH + 5.dp.toPx()
        drawRoundRect(color = Color(0xFFC7D2FE), topLeft = Offset(bedX, bedY), size = Size(18.dp.toPx(), 11.dp.toPx()), cornerRadius = CornerRadius(2.5.dp.toPx(), 2.5.dp.toPx()))
        drawRoundRect(color = Color.White, topLeft = Offset(bedX + 2.dp.toPx(), bedY + 2.dp.toPx()), size = Size(5.dp.toPx(), 7.dp.toPx()), cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx()))
    }
}

@Composable
fun DrawKidsRoom(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Canvas(
        modifier = modifier
            .size(width = 115.dp, height = 95.dp)
            .clickable(onClick = onClick)
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f + 4.dp.toPx()
        val w = 40.dp.toPx()
        val h = 21.dp.toPx()
        val wallH = 26.dp.toPx()

        drawIsometricBuildingBlock(
            cx = cx, cy = cy, w = w, h = h, wallH = wallH,
            floorColor = Color(0xFFFEF3C7),
            leftWallColor = Color(0xFFF5EFE6),
            rightWallColor = Color(0xFFDCD2C4),
            borderColor = if (isSelected) Color(0xFFD97706) else Color(0xFFB5A998),
            isSelected = isSelected
        )

        drawIsometricWindowOnRightWall(
            wallBottomX = cx, wallRightX = cx + w, wallBottomY = cy + h, wallH = wallH,
            offsetX = 8.dp.toPx(), offsetY = 6.dp.toPx(), winWidth = 14.dp.toPx(), winHeight = 13.dp.toPx()
        )

        // Study desk inside
        val deskX = cx + 4.dp.toPx()
        val deskY = cy - wallH + 5.dp.toPx()
        drawRect(color = Color(0xFF92400E), topLeft = Offset(deskX, deskY), size = Size(15.dp.toPx(), 8.dp.toPx()))
        drawRect(color = Color(0xFF38BDF8), topLeft = Offset(deskX + 4.dp.toPx(), deskY + 1.5.dp.toPx()), size = Size(7.dp.toPx(), 4.5.dp.toPx()))
    }
}

@Composable
fun DrawLivingRoom(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    hasAlert: Boolean = false,
    pulseAlpha: Float = 1f,
    onClick: () -> Unit = {}
) {
    Canvas(
        modifier = modifier
            .size(width = 125.dp, height = 100.dp)
            .clickable(onClick = onClick)
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f + 4.dp.toPx()
        val w = 44.dp.toPx()
        val h = 23.dp.toPx()
        val wallH = 30.dp.toPx()

        drawIsometricBuildingBlock(
            cx = cx, cy = cy, w = w, h = h, wallH = wallH,
            floorColor = Color(0xFFCCFBF1),
            leftWallColor = Color(0xFFFBF9F5),
            rightWallColor = Color(0xFFE5DDD0),
            borderColor = if (isSelected) Color(0xFF0D9488) else Color(0xFFB5A998),
            isSelected = isSelected
        )

        // Large Panoramic Window
        drawIsometricWindowOnLeftWall(
            wallLeftX = cx - w, wallBottomX = cx, wallBottomY = cy + h, wallH = wallH,
            offsetX = 8.dp.toPx(), offsetY = 4.dp.toPx(), winWidth = 20.dp.toPx(), winHeight = 20.dp.toPx(),
            isFloorToCeiling = true
        )

        // Sofa
        val sofaX = cx - 14.dp.toPx()
        val sofaY = cy - wallH + 6.dp.toPx()
        drawRoundRect(color = Color(0xFF0F766E), topLeft = Offset(sofaX, sofaY), size = Size(20.dp.toPx(), 9.dp.toPx()), cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()))

        // Plant
        val plantX = cx + 8.dp.toPx()
        val plantY = cy - wallH + 8.dp.toPx()
        drawCircle(color = Color(0xFF15803D), radius = 4.dp.toPx(), center = Offset(plantX, plantY))

        if (hasAlert) {
            drawCircle(color = Color.Red.copy(alpha = pulseAlpha * 0.45f), radius = 12.dp.toPx(), center = Offset(plantX, plantY))
        }
    }
}

@Composable
fun DrawKitchen(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    hasAlert: Boolean = false,
    pulseAlpha: Float = 1f,
    onClick: () -> Unit = {}
) {
    Canvas(
        modifier = modifier
            .size(width = 125.dp, height = 100.dp)
            .clickable(onClick = onClick)
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f + 4.dp.toPx()
        val w = 44.dp.toPx()
        val h = 23.dp.toPx()
        val wallH = 30.dp.toPx()

        drawIsometricBuildingBlock(
            cx = cx, cy = cy, w = w, h = h, wallH = wallH,
            floorColor = Color(0xFFFFE4E6),
            leftWallColor = Color(0xFFF5EFE6),
            rightWallColor = Color(0xFFDCD2C4),
            borderColor = if (isSelected) Color(0xFFE11D48) else Color(0xFFB5A998),
            isSelected = isSelected
        )

        // Wooden Door
        drawIsometricWoodenDoor(
            wallBottomX = cx, wallRightX = cx + w, wallBottomY = cy + h, wallH = wallH,
            offsetX = 8.dp.toPx(), doorWidth = 14.dp.toPx(), doorHeight = 22.dp.toPx()
        )

        // Kitchen Window
        drawIsometricWindowOnRightWall(
            wallBottomX = cx, wallRightX = cx + w, wallBottomY = cy + h, wallH = wallH,
            offsetX = 26.dp.toPx(), offsetY = 5.dp.toPx(), winWidth = 12.dp.toPx(), winHeight = 12.dp.toPx()
        )

        // Fridge inside
        val fridgeX = cx + 8.dp.toPx()
        val fridgeY = cy - wallH - 3.dp.toPx()
        drawRect(color = Color(0xFFE2E8F0), topLeft = Offset(fridgeX, fridgeY), size = Size(10.dp.toPx(), 16.dp.toPx()))

        if (hasAlert) {
            drawCircle(color = Color.Red.copy(alpha = pulseAlpha * 0.45f), radius = 12.dp.toPx(), center = Offset(fridgeX + 5.dp.toPx(), fridgeY + 8.dp.toPx()))
        }
    }
}

@Composable
fun DrawYard(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Canvas(
        modifier = modifier
            .size(width = 185.dp, height = 95.dp)
            .clickable(onClick = onClick)
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val w = 76.dp.toPx()
        val h = 34.dp.toPx()
        val foundationH = 12.dp.toPx()

        // Lawn
        val lawn = Path().apply {
            moveTo(cx, cy - h)
            lineTo(cx + w, cy)
            lineTo(cx, cy + h)
            lineTo(cx - w, cy)
            close()
        }
        drawPath(
            path = lawn,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF86EFAC), Color(0xFF4ADE80), Color(0xFF22C55E)),
                start = Offset(cx, cy - h),
                end = Offset(cx, cy + h)
            )
        )
        drawPath(path = lawn, color = if (isSelected) Color(0xFF14532D) else Color(0xFF16A34A), style = Stroke(1.5.dp.toPx()))

        // Concrete edges
        val leftEdge = Path().apply {
            moveTo(cx - w, cy)
            lineTo(cx, cy + h)
            lineTo(cx, cy + h + foundationH)
            lineTo(cx - w, cy + foundationH)
            close()
        }
        drawPath(path = leftEdge, color = Color(0xFF64748B))

        val rightEdge = Path().apply {
            moveTo(cx, cy + h)
            lineTo(cx + w, cy)
            lineTo(cx + w, cy + foundationH)
            lineTo(cx, cy + h + foundationH)
            close()
        }
        drawPath(path = rightEdge, color = Color(0xFF475569))

        // Stone path
        val stoneOffsets = listOf(
            Offset(cx + 4.dp.toPx(), cy + 18.dp.toPx()),
            Offset(cx + 14.dp.toPx(), cy + 13.dp.toPx()),
            Offset(cx + 24.dp.toPx(), cy + 8.dp.toPx()),
            Offset(cx + 34.dp.toPx(), cy + 3.dp.toPx())
        )
        stoneOffsets.forEach { stone ->
            val sw = 7.dp.toPx()
            val sh = 3.5.dp.toPx()
            val stonePath = Path().apply {
                moveTo(stone.x, stone.y - sh)
                lineTo(stone.x + sw, stone.y)
                lineTo(stone.x, stone.y + sh)
                lineTo(stone.x - sw, stone.y)
                close()
            }
            drawPath(path = stonePath, color = Color(0xFFF1F5F9))
            drawPath(path = stonePath, color = Color(0xFF94A3B8), style = Stroke(1.dp.toPx()))
        }

        drawCircle(color = Color(0xFF15803D), radius = 5.dp.toPx(), center = Offset(cx - 42.dp.toPx(), cy - 4.dp.toPx()))
        drawCircle(color = Color(0xFFF43F5E), radius = 2.5.dp.toPx(), center = Offset(cx - 43.dp.toPx(), cy - 4.dp.toPx()))
    }
}

// =========================================================================
// HELPER DRAWING FUNCTIONS
// =========================================================================

private fun DrawScope.drawIsometricBuildingBlock(
    cx: Float,
    cy: Float,
    w: Float,
    h: Float,
    wallH: Float,
    floorColor: Color,
    leftWallColor: Color,
    rightWallColor: Color,
    borderColor: Color,
    isSelected: Boolean
) {
    val leftWall = Path().apply {
        moveTo(cx - w, cy)
        lineTo(cx, cy + h)
        lineTo(cx, cy + h - wallH)
        lineTo(cx - w, cy - wallH)
        close()
    }
    drawPath(path = leftWall, color = leftWallColor)
    drawPath(path = leftWall, color = borderColor, style = Stroke(1.2.dp.toPx()))

    val rightWall = Path().apply {
        moveTo(cx, cy + h)
        lineTo(cx + w, cy)
        lineTo(cx + w, cy - wallH)
        lineTo(cx, cy + h - wallH)
        close()
    }
    drawPath(path = rightWall, color = rightWallColor)
    drawPath(path = rightWall, color = borderColor, style = Stroke(1.2.dp.toPx()))

    val floor = Path().apply {
        moveTo(cx, cy - h - wallH)
        lineTo(cx + w, cy - wallH)
        lineTo(cx, cy + h - wallH)
        lineTo(cx - w, cy - wallH)
        close()
    }
    drawPath(path = floor, color = floorColor)
    drawPath(path = floor, color = borderColor, style = Stroke(if (isSelected) 2.dp.toPx() else 1.2.dp.toPx()))

    if (isSelected) {
        drawPath(path = floor, color = Color.White.copy(alpha = 0.35f))
    }
}

private fun DrawScope.drawIsometricWindowOnLeftWall(
    wallLeftX: Float,
    wallBottomX: Float,
    wallBottomY: Float,
    wallH: Float,
    offsetX: Float,
    offsetY: Float,
    winWidth: Float,
    winHeight: Float,
    isFloorToCeiling: Boolean = false
) {
    val slopeX = (wallBottomX - wallLeftX)
    val totalDist = wallBottomX - wallLeftX
    val tStart = offsetX / totalDist
    val tEnd = (offsetX + winWidth) / totalDist

    val startX = wallLeftX + slopeX * tStart
    val startY = (wallBottomY - (1f - tStart) * 20.dp.toPx()) - wallH + offsetY
    val endX = wallLeftX + slopeX * tEnd
    val endY = startY + (winWidth * 0.5f)

    val winPath = Path().apply {
        moveTo(startX, startY)
        lineTo(endX, endY)
        lineTo(endX, endY + winHeight)
        lineTo(startX, startY + winHeight)
        close()
    }
    drawPath(path = winPath, color = Color(0xFF334155))

    val inset = 1.2.dp.toPx()
    val glassPath = Path().apply {
        moveTo(startX + inset, startY + inset)
        lineTo(endX - inset, endY + inset)
        lineTo(endX - inset, endY + winHeight - inset)
        lineTo(startX + inset, startY + winHeight - inset)
        close()
    }
    drawPath(
        path = glassPath,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFE0F2FE), Color(0xFFBAE6FD), Color(0xFF7DD3FC)),
            start = Offset(startX, startY),
            end = Offset(endX, endY + winHeight)
        )
    )

    // Glare
    drawLine(
        color = Color.White.copy(alpha = 0.65f),
        start = Offset(startX + (endX - startX) * 0.3f, startY + winHeight * 0.2f),
        end = Offset(startX + (endX - startX) * 0.7f, startY + winHeight * 0.8f),
        strokeWidth = 1.2.dp.toPx()
    )

    val midX = (startX + endX) / 2f
    val midY = (startY + endY) / 2f
    drawLine(color = Color(0xFF1E293B), start = Offset(midX, midY), end = Offset(midX, midY + winHeight), strokeWidth = 1.dp.toPx())
    drawLine(color = Color(0xFF1E293B), start = Offset(startX, startY + winHeight / 2f), end = Offset(endX, endY + winHeight / 2f), strokeWidth = 1.dp.toPx())
}

private fun DrawScope.drawIsometricWindowOnRightWall(
    wallBottomX: Float,
    wallRightX: Float,
    wallBottomY: Float,
    wallH: Float,
    offsetX: Float,
    offsetY: Float,
    winWidth: Float,
    winHeight: Float
) {
    val slopeX = (wallRightX - wallBottomX)
    val totalDist = wallRightX - wallBottomX
    val tStart = offsetX / totalDist
    val tEnd = (offsetX + winWidth) / totalDist

    val startX = wallBottomX + slopeX * tStart
    val startY = wallBottomY - (tStart * 20.dp.toPx()) - wallH + offsetY
    val endX = wallBottomX + slopeX * tEnd
    val endY = startY - (winWidth * 0.5f)

    val winPath = Path().apply {
        moveTo(startX, startY)
        lineTo(endX, endY)
        lineTo(endX, endY + winHeight)
        lineTo(startX, startY + winHeight)
        close()
    }
    drawPath(path = winPath, color = Color(0xFF334155))

    val inset = 1.2.dp.toPx()
    val glassPath = Path().apply {
        moveTo(startX + inset, startY - inset)
        lineTo(endX - inset, endY - inset)
        lineTo(endX - inset, endY + winHeight - inset)
        lineTo(startX + inset, startY + winHeight - inset)
        close()
    }
    drawPath(
        path = glassPath,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFE0F2FE), Color(0xFFBAE6FD), Color(0xFF7DD3FC)),
            start = Offset(startX, startY),
            end = Offset(endX, endY + winHeight)
        )
    )

    val midX = (startX + endX) / 2f
    val midY = (startY + endY) / 2f
    drawLine(color = Color(0xFF1E293B), start = Offset(midX, midY), end = Offset(midX, midY + winHeight), strokeWidth = 1.dp.toPx())
    drawLine(color = Color(0xFF1E293B), start = Offset(startX, startY + winHeight / 2f), end = Offset(endX, endY + winHeight / 2f), strokeWidth = 1.dp.toPx())
}

private fun DrawScope.drawIsometricWoodenDoor(
    wallBottomX: Float,
    wallRightX: Float,
    wallBottomY: Float,
    wallH: Float,
    offsetX: Float,
    doorWidth: Float,
    doorHeight: Float
) {
    val slopeX = (wallRightX - wallBottomX)
    val totalDist = wallRightX - wallBottomX
    val tStart = offsetX / totalDist
    val tEnd = (offsetX + doorWidth) / totalDist

    val startX = wallBottomX + slopeX * tStart
    val baseStartY = wallBottomY - (tStart * 20.dp.toPx())
    val endX = wallBottomX + slopeX * tEnd
    val baseEndY = baseStartY - (doorWidth * 0.5f)

    // Step
    val stepPath = Path().apply {
        moveTo(startX - 2.dp.toPx(), baseStartY)
        lineTo(endX + 2.dp.toPx(), baseEndY)
        lineTo(endX + 2.dp.toPx(), baseEndY + 2.5.dp.toPx())
        lineTo(startX - 2.dp.toPx(), baseStartY + 2.5.dp.toPx())
        close()
    }
    drawPath(path = stepPath, color = Color(0xFF94A3B8))

    // Frame
    val framePath = Path().apply {
        moveTo(startX, baseStartY)
        lineTo(endX, baseEndY)
        lineTo(endX, baseEndY - doorHeight)
        lineTo(startX, baseStartY - doorHeight)
        close()
    }
    drawPath(path = framePath, color = Color(0xFF451A03))

    // Door Body
    val inset = 1.dp.toPx()
    val doorPath = Path().apply {
        moveTo(startX + inset, baseStartY - inset)
        lineTo(endX - inset, baseEndY - inset)
        lineTo(endX - inset, baseEndY - doorHeight + inset)
        lineTo(startX + inset, baseStartY - doorHeight + inset)
        close()
    }
    drawPath(
        path = doorPath,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFB45309), Color(0xFF92400E), Color(0xFF78350F)),
            start = Offset(startX, baseStartY),
            end = Offset(endX, baseEndY - doorHeight)
        )
    )

    // Upper & Lower Panel
    val midHeight = doorHeight / 2f
    val upPanel = Path().apply {
        moveTo(startX + 2.dp.toPx(), baseStartY - midHeight - 1.dp.toPx())
        lineTo(endX - 2.dp.toPx(), baseEndY - midHeight - 1.dp.toPx())
        lineTo(endX - 2.dp.toPx(), baseEndY - doorHeight + 3.dp.toPx())
        lineTo(startX + 2.dp.toPx(), baseStartY - doorHeight + 3.dp.toPx())
        close()
    }
    drawPath(path = upPanel, color = Color(0xFF78350F))
    drawPath(path = upPanel, color = Color(0xFF451A03), style = Stroke(1.dp.toPx()))

    val lowPanel = Path().apply {
        moveTo(startX + 2.dp.toPx(), baseStartY - 3.dp.toPx())
        lineTo(endX - 2.dp.toPx(), baseEndY - 3.dp.toPx())
        lineTo(endX - 2.dp.toPx(), baseEndY - midHeight + 2.dp.toPx())
        lineTo(startX + 2.dp.toPx(), baseStartY - midHeight + 2.dp.toPx())
        close()
    }
    drawPath(path = lowPanel, color = Color(0xFF78350F))
    drawPath(path = lowPanel, color = Color(0xFF451A03), style = Stroke(1.dp.toPx()))

    // Golden Door Knob
    val knobX = startX + 3.5.dp.toPx()
    val knobY = baseStartY - (midHeight * 0.9f)
    drawCircle(color = Color(0xFFFACC15), radius = 1.8.dp.toPx(), center = Offset(knobX, knobY))
    drawCircle(color = Color(0xFFCA8A04), radius = 1.8.dp.toPx(), center = Offset(knobX, knobY), style = Stroke(0.5.dp.toPx()))
    drawCircle(color = Color.White, radius = 0.6.dp.toPx(), center = Offset(knobX - 0.5.dp.toPx(), knobY - 0.5.dp.toPx()))
}

// =========================================================================
// ROOM LABEL COMPONENT (ONLY SHOWN IN EXPLODED VIEW)
// =========================================================================
@Composable
private fun ExplodedRoomLabel(
    title: String,
    subtitle: String,
    color: Color,
    icon: ImageVector,
    isSelected: Boolean,
    badgeOffsetX: Dp,
    badgeOffsetY: Dp,
    isAlert: Boolean = false,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(targetValue = if (isPressed) 0.94f else 1f, label = "press")
    val isDark = isSystemInDarkTheme()

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) color.copy(alpha = 0.2f) else (if (isDark) Color(0xFF1E293B) else Color.White).copy(alpha = 0.96f),
        border = BorderStroke(1.2.dp, if (isSelected) color else (if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))),
        shadowElevation = if (isSelected) 6.dp else 3.dp,
        modifier = Modifier
            .offset(x = badgeOffsetX, y = badgeOffsetY)
            .scale(pressScale)
            .testTag("exploded_label_${title}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(13.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF1E293B)
                    )
                    if (isAlert) {
                        Spacer(modifier = Modifier.width(3.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                        )
                    }
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
            }
        }
    }
}
