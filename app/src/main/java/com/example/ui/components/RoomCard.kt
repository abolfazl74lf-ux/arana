package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.domain.model.HousekeepingTrick
import com.example.domain.model.RoomType

/**
 * Returns default preset drawable thumbnail resource for each room.
 */
@DrawableRes
fun getRoomPresetDrawable(room: RoomType): Int {
    return when (room) {
        RoomType.KITCHEN -> R.drawable.img_preset_kitchen_1789981803824
        RoomType.LIVING_ROOM -> R.drawable.img_preset_living_1789981817856
        RoomType.BEDROOM -> R.drawable.img_preset_bedroom_1789981832836
        RoomType.KIDS_ROOM -> R.drawable.img_preset_kids_1789981845374
        RoomType.YARD -> R.drawable.img_preset_yard_1789981856512
    }
}

/**
 * Returns the list of standard management tools for each room.
 */
fun getToolsForRoom(room: RoomType): List<String> {
    return when (room) {
        RoomType.KITCHEN -> listOf("موجودی یخچال", "لیست خرید", "برنامه غذایی")
        RoomType.LIVING_ROOM -> listOf("تقویم نظافت", "نگهداری گیاهان آپارتمانی", "چک‌لیست مهمان")
        RoomType.BEDROOM -> listOf("مدیریت کمد و شستشو", "روتین‌های روزانه")
        RoomType.KIDS_ROOM -> listOf("چارت وظایف کودک", "ردیاب تکالیف")
        RoomType.YARD -> listOf("تقویم باغبانی", "تعمیرات و نگهداری")
    }
}

/**
 * Enhanced RoomCard featuring:
 * 1. Fixed-size visual photo frame with Coil (Preset or custom user gallery photo) + modern Photo Picker button.
 * 2. Room identity, live status badge, and alert indicator.
 * 3. Management tools section with clean informative chips.
 * 4. Dedicated "Housekeeping Trick of the Day" (ترفند روز) module with 💡 icon, cycling action, and tap-to-read detail modal.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoomCard(
    room: RoomType,
    statusText: String,
    alertCount: Int = 0,
    customImageUri: String? = null,
    currentTrick: HousekeepingTrick? = null,
    onImageSelected: (Uri) -> Unit = {},
    onNextTrick: () -> Unit = {},
    onOpenTrickDetails: (HousekeepingTrick) -> Unit = {},
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Modern zero-permission Android Photo Picker compliant with Play Store policy
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onImageSelected(uri)
        }
    }

    val imageModel = customImageUri?.let { Uri.parse(it) } ?: getRoomPresetDrawable(room)
    val roomTools = getToolsForRoom(room)

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 5.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("room_card_${room.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // --- TOP ROW: Thumbnail Photo + Header Info + Action Chevron ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Fixed-size Visual Frame with ContentScale.Crop and Camera Action
                Box(
                    modifier = Modifier
                        .size(width = 88.dp, height = 88.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(room.secondaryColor)
                        .border(
                            width = 1.dp,
                            color = room.primaryColor.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .testTag("room_thumbnail_container_${room.id}")
                ) {
                    // Image with Coil (Preset or User Photo)
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageModel)
                            .crossfade(true)
                            .build(),
                        contentDescription = "تصویر ${room.titleFa}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Subtle gradient overlay at bottom of thumbnail
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                                )
                            )
                    )

                    // Quick Camera/Upload Photo Picker Button Overlay
                    FilledIconButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                            contentColor = room.primaryColor
                        ),
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 4.dp, end = 4.dp)
                            .testTag("room_camera_button_${room.id}")
                    ) {
                        Icon(
                            imageVector = if (customImageUri != null) Icons.Default.CameraAlt else Icons.Default.AddPhotoAlternate,
                            contentDescription = "تغییر عکس ${room.titleFa}",
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    // Custom Photo indicator dot
                    if (customImageUri != null) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .border(1.dp, Color.White, CircleShape)
                                .align(Alignment.TopStart)
                                .padding(2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Room Name, Description and Status
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = room.titleFa,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (alertCount > 0) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.errorContainer,
                                modifier = Modifier.padding(start = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "$alertCount هشدار",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = room.secondaryColor,
                                modifier = Modifier.padding(start = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = room.primaryColor,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "مرتب",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 10.sp
                                        ),
                                        color = room.primaryColor
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = room.descriptionFa,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Live Status Pill
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = room.primaryColor.copy(alpha = 0.08f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.5.sp
                            ),
                            color = room.primaryColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "ورود به ${room.titleFa}",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(8.dp))

            // --- TOOLS CHIPS SECTION (ابزارهای مدیریتی) ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "ابزارها:",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    roomTools.forEach { toolTitle ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                        ) {
                            Text(
                                text = toolTitle,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // --- HOUSEKEEPING TRICK OF THE DAY COMPONENT (ترفند روز) ---
            if (currentTrick != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFFBEB), // Soft warm amber pastel
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenTrickDetails(currentTrick) }
                        .testTag("trick_card_${room.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Lightbulb Icon Container
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEF3C7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "💡", fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Animated Trick Content
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "ترفند روز:",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 10.5.sp
                                        ),
                                        color = Color(0xFFB45309)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = currentTrick.title,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        color = Color(0xFF92400E),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            AnimatedContent(
                                targetState = currentTrick.shortTip,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "trick_tip_anim"
                            ) { tipText ->
                                Text(
                                    text = tipText,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                                    color = Color(0xFF78350F),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Next Trick Cycle Button
                        IconButton(
                            onClick = onNextTrick,
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("btn_next_trick_${room.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "ترفند بعدی",
                                tint = Color(0xFFB45309),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
