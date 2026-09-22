package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Reusable Composable that encapsulates Material 3 SwipeToDismissBox with:
 * 1. Fluid swipe-to-dismiss gesture (Left-to-Right & Right-to-Left).
 * 2. High-contrast red background with animated trash can icon & delete indicator.
 * 3. Optional alternative delete button (trash icon) that opens a confirmation dialog.
 * 4. State keying for seamless undo-restore without stale dismiss states.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableTaskCard(
    itemKey: Any,
    taskTitle: String,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    enableSwipe: Boolean = true,
    showDeleteIcon: Boolean = true,
    confirmDialogMessage: String = "آیا از حذف این وظیفه اطمینان دارید؟ در صورت نیاز می‌توانید آن را از نوار پایین بازگردانی کنید.",
    content: @Composable () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    val currentOnDelete by rememberUpdatedState(onDelete)

    // Keying by itemKey ensures that when item is restored via Undo, state resets to Settled
    key(itemKey) {
        val dismissState = rememberSwipeToDismissBoxState(
            confirmValueChange = { dismissValue ->
                if (dismissValue == SwipeToDismissBoxValue.StartToEnd || dismissValue == SwipeToDismissBoxValue.EndToStart) {
                    currentOnDelete()
                    true
                } else {
                    false
                }
            }
        )

        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(shape)
                .testTag("swipeable_task_$itemKey")
        ) {
            SwipeToDismissBox(
                state = dismissState,
                enableDismissFromStartToEnd = enableSwipe,
                enableDismissFromEndToStart = enableSwipe,
                backgroundContent = {
                    DismissBackground(dismissState = dismissState, shape = shape)
                },
                content = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        content()

                        if (showDeleteIcon) {
                            IconButton(
                                onClick = { showConfirmDialog = true },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(32.dp)
                                    .testTag("btn_delete_$itemKey")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "حذف وظیفه $taskTitle",
                                    tint = Color(0xFF9CA3AF),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            )
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.WarningAmber,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = "تأیید حذف وظیفه",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1F2937)
                )
            },
            text = {
                Text(
                    text = if (taskTitle.isNotBlank()) "«$taskTitle»\n\n$confirmDialogMessage" else confirmDialogMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4B5563)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        currentOnDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_confirm_delete_$itemKey")
                ) {
                    Text("حذف", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showConfirmDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("انصراف", color = Color(0xFF4B5563))
                }
            },
            shape = RoundedCornerShape(18.dp),
            containerColor = Color.White
        )
    }
}

/**
 * Animated background showing red warning surface with animated trash can icon
 * on both left and right edges depending on swipe progress.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DismissBackground(
    dismissState: SwipeToDismissBoxState,
    shape: Shape
) {
    val direction = dismissState.dismissDirection
    val isSwiping = direction != SwipeToDismissBoxValue.Settled

    val color by animateColorAsState(
        targetValue = if (isSwiping) Color(0xFFDC2626) else Color(0xFFEF4444),
        label = "dismiss_bg_color"
    )

    val scale by animateFloatAsState(
        targetValue = if (isSwiping) 1.15f else 0.85f,
        label = "dismiss_icon_scale"
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .clip(shape)
            .background(color)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Start side indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "حذف وظیفه",
                tint = Color.White,
                modifier = Modifier
                    .size(26.dp)
                    .scale(scale)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "حذف",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        // End side indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "حذف",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "حذف وظیفه",
                tint = Color.White,
                modifier = Modifier
                    .size(26.dp)
                    .scale(scale)
            )
        }
    }
}
