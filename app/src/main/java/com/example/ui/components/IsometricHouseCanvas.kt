package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Roofing
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.RoomType
import com.example.ui.engine.isometric.HomeIsometricScene
import com.example.ui.engine.isometric.IsoProjection
import com.example.ui.home.HomeOverviewStats
import kotlinx.coroutines.launch

/**
 * Visual interactive representation of the home using a custom isometric rendering engine
 * built upon Jetpack Compose's Canvas API.
 * Supports interactive room selection, volumetric lighting, touch hit-testing,
 * animated exploded multi-floor views, and rich touch feedback animations (spring lifts,
 * expanding pulse ripples, and subtle haptic feedback).
 */
@Composable
fun IsometricHouseCanvas(
    stats: HomeOverviewStats,
    selectedRoom: RoomType?,
    onRoomClick: (RoomType) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    // Mode: Exploded View (تفکیک طبقات) separates floors along the Z axis to reveal interiors
    var isExplodedView by remember { mutableStateOf(false) }

    // Smooth animated Z separation for the upper level
    val upperLevelZOffset by animateFloatAsState(
        targetValue = if (isExplodedView) 1.25f else 0f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 300f),
        label = "upperLevelZOffset"
    )

    // Touch Feedback: Animated Z-lift for each room when selected or tapped
    val yardLift by animateFloatAsState(
        targetValue = if (selectedRoom == RoomType.YARD) 0.30f else 0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 420f),
        label = "yardLift"
    )
    val kitchenLift by animateFloatAsState(
        targetValue = if (selectedRoom == RoomType.KITCHEN) 0.45f else 0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 420f),
        label = "kitchenLift"
    )
    val livingLift by animateFloatAsState(
        targetValue = if (selectedRoom == RoomType.LIVING_ROOM) 0.45f else 0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 420f),
        label = "livingLift"
    )
    val kidsLift by animateFloatAsState(
        targetValue = if (selectedRoom == RoomType.KIDS_ROOM) 0.45f else 0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 420f),
        label = "kidsLift"
    )
    val bedLift by animateFloatAsState(
        targetValue = if (selectedRoom == RoomType.BEDROOM) 0.45f else 0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 420f),
        label = "bedLift"
    )

    // Touch Ripple feedback: expanding ring animation at touch coordinate
    var rippleCenter by remember { mutableStateOf<Offset?>(null) }
    var rippleColor by remember { mutableStateOf(Color.White) }
    val rippleProgress = remember { Animatable(0f) }

    // Alert indicator pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "canvasAnimations")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Subtle breathing pulse for currently selected room badge
    val selectionPulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "selectionPulse"
    )

    // Chimney smoke particle loop
    val smokeFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "smokePuffs"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .testTag("isometric_house_canvas_container"),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F5F0)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFBF8F4),
                            Color(0xFFF2ECE1),
                            Color(0xFFE5DDD0)
                        )
                    )
                )
        ) {
            val canvasW = constraints.maxWidth.toFloat()
            val canvasH = constraints.maxHeight.toFloat()

            // Dynamic projection centered within constraints
            val projection = remember(canvasW, canvasH, isExplodedView) {
                IsoProjection.createCentered(
                    canvasWidth = canvasW,
                    canvasHeight = canvasH,
                    worldGridUnits = 7.6f,
                    verticalOffsetRatio = if (isExplodedView) 0.62f else 0.54f
                )
            }

            // Map animated lifts to the 3D scene builder
            val activeRoomLifts = remember(yardLift, kitchenLift, livingLift, kidsLift, bedLift) {
                mapOf(
                    RoomType.YARD to yardLift,
                    RoomType.KITCHEN to kitchenLift,
                    RoomType.LIVING_ROOM to livingLift,
                    RoomType.KIDS_ROOM to kidsLift,
                    RoomType.BEDROOM to bedLift
                )
            }

            // Build current 3D architectural scene with dynamic lift elevations
            val scene = remember(selectedRoom, upperLevelZOffset, isExplodedView, activeRoomLifts) {
                HomeIsometricScene.buildScene(
                    selectedRoom = selectedRoom,
                    upperLevelZOffset = upperLevelZOffset,
                    isRoofVisible = !isExplodedView,
                    activeRoomLifts = activeRoomLifts
                )
            }

            // Trigger ripple effect helper
            fun triggerTouchFeedback(hitRoom: RoomType, tapPos: Offset) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                rippleCenter = tapPos
                rippleColor = hitRoom.primaryColor
                coroutineScope.launch {
                    rippleProgress.snapTo(0f)
                    rippleProgress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 480, easing = FastOutSlowInEasing)
                    )
                    rippleCenter = null
                }
                onRoomClick(hitRoom)
            }

            // 1. Isometric Canvas rendering layer with Touch Hit Detection and animated feedback
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(projection, scene) {
                        detectTapGestures { tapOffset ->
                            val hit = scene.hitTest(tapOffset, projection)
                            if (hit is RoomType) {
                                triggerTouchFeedback(hit, tapOffset)
                            }
                        }
                    }
            ) {
                // Render all faces using Painter's algorithm
                scene.render(this, projection)

                // Render chimney smoke if roof is active and not exploded
                if (!isExplodedView) {
                    val chimneyApex = projection.toScreen(4.9f, 2.3f, 5.0f)
                    val smokeY1 = chimneyApex.y - (smokeFloat * 30f)
                    val smokeRadius1 = 4f + (smokeFloat * 6f)
                    val alpha1 = (1f - smokeFloat) * 0.45f
                    drawCircle(
                        color = Color.Gray.copy(alpha = alpha1),
                        radius = smokeRadius1,
                        center = Offset(chimneyApex.x + smokeFloat * 6f, smokeY1)
                    )

                    val smokeOffset2 = (smokeFloat + 0.5f) % 1f
                    val smokeY2 = chimneyApex.y - (smokeOffset2 * 34f)
                    val smokeRadius2 = 5f + (smokeOffset2 * 7f)
                    val alpha2 = (1f - smokeOffset2) * 0.40f
                    drawCircle(
                        color = Color.Gray.copy(alpha = alpha2),
                        radius = smokeRadius2,
                        center = Offset(chimneyApex.x - smokeOffset2 * 5f, smokeY2)
                    )
                }

                // Render dynamic touch ripple expansion ring
                rippleCenter?.let { center ->
                    val progress = rippleProgress.value
                    if (progress in 0f..1f) {
                        val maxRadius = 80f
                        val currentRadius = progress * maxRadius
                        val alpha = (1f - progress) * 0.75f

                        // Outer ring stroke
                        drawCircle(
                            color = rippleColor.copy(alpha = alpha),
                            radius = currentRadius,
                            center = center,
                            style = Stroke(width = 3.5f * (1f - progress * 0.5f))
                        )
                        // Inner soft glow fill
                        drawCircle(
                            color = rippleColor.copy(alpha = alpha * 0.25f),
                            radius = currentRadius * 0.6f,
                            center = center
                        )
                    }
                }
            }

            // 2. View Mode Toggle Controls (Top Bar)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        isExplodedView = !isExplodedView
                    },
                    label = {
                        Text(
                            text = if (isExplodedView) "نمای یکپارچه" else "تفکیک طبقات",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isExplodedView) Icons.Default.Roofing else Icons.Default.Layers,
                            contentDescription = "تغییر نمای ایزومتریک",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
                    )
                )

                Text(
                    text = "نمای ایزومتریک تعاملی",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            // 3. Floating Interactive Room Status Badges with scale feedback
            // Kids' Room (Top-Start)
            RoomOverlayBadge(
                title = "اتاق بچه",
                subtitle = if (stats.kidsPendingChores > 0) "${stats.kidsPendingChores} وظیفه باقی‌مانده" else "وظایف کامل ⭐",
                color = RoomType.KIDS_ROOM.primaryColor,
                isSelected = selectedRoom == RoomType.KIDS_ROOM,
                selectionPulse = if (selectedRoom == RoomType.KIDS_ROOM) selectionPulse else 1f,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 12.dp, top = 48.dp),
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onRoomClick(RoomType.KIDS_ROOM)
                }
            )

            // Bedroom (Top-End)
            RoomOverlayBadge(
                title = "اتاق خواب",
                subtitle = if (stats.bedroomPendingRoutinesCount > 0) "${stats.bedroomPendingRoutinesCount} روتین منتظر" else "آرام و مرتب",
                color = RoomType.BEDROOM.primaryColor,
                isSelected = selectedRoom == RoomType.BEDROOM,
                selectionPulse = if (selectedRoom == RoomType.BEDROOM) selectionPulse else 1f,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 12.dp, top = 48.dp),
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onRoomClick(RoomType.BEDROOM)
                }
            )

            // Kitchen (Center-Start)
            RoomOverlayBadge(
                title = "آشپزخانه",
                subtitle = if (stats.kitchenExpiringCount > 0) "⚠️ ${stats.kitchenExpiringCount} انقضا نزدیک" else "موجودی کامل",
                color = RoomType.KITCHEN.primaryColor,
                isAlert = stats.kitchenExpiringCount > 0,
                pulseAlpha = pulseAlpha,
                isSelected = selectedRoom == RoomType.KITCHEN,
                selectionPulse = if (selectedRoom == RoomType.KITCHEN) selectionPulse else 1f,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 12.dp, top = 88.dp),
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onRoomClick(RoomType.KITCHEN)
                }
            )

            // Living Room (Center-End)
            RoomOverlayBadge(
                title = "پذیرایی",
                subtitle = if (stats.plantsNeedingWaterCount > 0) "💧 ${stats.plantsNeedingWaterCount} گیاه تشنه" else "گل‌ها شاداب",
                color = RoomType.LIVING_ROOM.primaryColor,
                isAlert = stats.plantsNeedingWaterCount > 0,
                pulseAlpha = pulseAlpha,
                isSelected = selectedRoom == RoomType.LIVING_ROOM,
                selectionPulse = if (selectedRoom == RoomType.LIVING_ROOM) selectionPulse else 1f,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp, top = 88.dp),
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onRoomClick(RoomType.LIVING_ROOM)
                }
            )

            // Yard & Outdoors (Bottom-Center)
            RoomOverlayBadge(
                title = "حیاط و بیرون",
                subtitle = if (stats.yardMaintenancePendingCount > 0) "${stats.yardMaintenancePendingCount} مورد سرویس" else "فضای سبز آراسته",
                color = RoomType.YARD.primaryColor,
                isSelected = selectedRoom == RoomType.YARD,
                selectionPulse = if (selectedRoom == RoomType.YARD) selectionPulse else 1f,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp),
                onClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onRoomClick(RoomType.YARD)
                }
            )
        }
    }
}

@Composable
private fun RoomOverlayBadge(
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
    isAlert: Boolean = false,
    isSelected: Boolean = false,
    pulseAlpha: Float = 1f,
    selectionPulse: Float = 1f,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Smooth press scale animation
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 600f),
        label = "pressScale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) color.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
        shadowElevation = if (isSelected) 6.dp else 2.dp,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, color) else null,
        modifier = modifier
            .scale(pressScale * selectionPulse)
            .testTag("room_badge_${title}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isAlert) Color.Red.copy(alpha = pulseAlpha) else color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "انتخاب‌شده",
                            tint = color,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = if (isAlert) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
