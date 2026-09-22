package com.example.ui.timeline

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.SwipeableTaskCard
import com.example.ui.notification.NotificationCenterDialog
import com.example.ui.notification.RequestNotificationPermissionEffect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyTimelineScreen(
    onBack: () -> Unit,
    viewModel: DailyTimelineViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showRoutineSheet by remember { mutableStateOf(false) }
    var showNotificationCenter by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Request notification permissions for Android 13+ (POST_NOTIFICATIONS)
    RequestNotificationPermissionEffect()

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .testTag("timeline_snackbar_host")
            )
        },
        topBar = {
            TimelineDateTopAppBar(
                currentDateText = state.currentDateText,
                onPreviousDay = { viewModel.navigatePreviousDay() },
                onNextDay = { viewModel.navigateNextDay() },
                onBack = onBack,
                onOpenNotifications = { showNotificationCenter = true }
            )
        },
        bottomBar = {
            TimelineBottomNavigationBar(
                selectedTab = state.selectedTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showRoutineSheet = true },
                containerColor = Color(0xFFD32F2F), // Bright Red FAB as requested
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 8.dp, end = 4.dp)
                    .testTag("btn_timeline_add_event")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "انتخاب و افزودن روتین به تایملاین",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9FAFB))
                .padding(innerPadding)
        ) {
            // Mini Header / Day Progress Summary
            TimelineSummaryHeader(eventsCount = state.events.size)

            // Timeline List with continuous vertical line
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("timeline_lazy_column"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp)
            ) {
                itemsIndexed(state.events, key = { _, item -> item.id }) { index, event ->
                    TimelineEventRow(
                        event = event,
                        isFirst = index == 0,
                        isLast = index == state.events.size - 1,
                        onToggleDone = { viewModel.toggleEventDone(event.id) },
                        onDelete = {
                            val deleted = viewModel.onDeleteTask(event)
                            if (deleted != null) {
                                coroutineScope.launch {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    val result = snackbarHostState.showSnackbar(
                                        message = "وظیفه «${deleted.title}» حذف شد",
                                        actionLabel = "بازگردانی",
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.onUndoDelete()
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    // ModalBottomSheet for Selecting from Routines
    if (showRoutineSheet) {
        ModalBottomSheet(
            onDismissRequest = { showRoutineSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            modifier = Modifier.testTag("bottom_sheet_routines")
        ) {
            RoutinesSelectionSheetContent(
                routines = state.availableRoutines,
                onRoutineSelected = { routine ->
                    viewModel.addRoutineToTimeline(routine)
                    coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showRoutineSheet = false
                        }
                    }
                },
                onDeleteRoutine = { routine ->
                    val deleted = viewModel.onDeleteRoutine(routine)
                    if (deleted != null) {
                        coroutineScope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            val result = snackbarHostState.showSnackbar(
                                message = "روتین «${deleted.title}» حذف شد",
                                actionLabel = "بازگردانی",
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.onUndoDeleteRoutine()
                            }
                        }
                    }
                }
            )
        }
    }

    // Notification Center & Tester Dialog
    if (showNotificationCenter) {
        NotificationCenterDialog(
            onDismiss = { showNotificationCenter = false }
        )
    }
}

@Composable
fun RoutinesSelectionSheetContent(
    routines: List<AvailableRoutine>,
    onRoutineSelected: (AvailableRoutine) -> Unit,
    onDeleteRoutine: (AvailableRoutine) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFEBEE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlaylistAddCheck,
                        contentDescription = null,
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "انتخاب از روتین‌ها",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = "وظایف از پیش ساخته شده برای اضافه شدن به تایملاین",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                        color = Color(0xFF6B7280)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF3F4F6)
            ) {
                Text(
                    text = "${routines.size} روتین آماده",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = Color(0xFF4B5563),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Routine Items List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("list_routines_sheet"),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(routines, key = { it.id }) { routine ->
                SwipeableTaskCard(
                    itemKey = routine.id,
                    taskTitle = routine.title,
                    onDelete = { onDeleteRoutine(routine) },
                    shape = RoundedCornerShape(16.dp),
                    confirmDialogMessage = "آیا از حذف روتین «${routine.title}» مطمئن هستید؟",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RoutineSelectionCard(
                        routine = routine,
                        onClick = { onRoutineSelected(routine) }
                    )
                }
            }
        }
    }
}

@Composable
fun RoutineSelectionCard(
    routine: AvailableRoutine,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_routine_item_${routine.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(routine.chipBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = routine.icon,
                    contentDescription = null,
                    tint = routine.categoryColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title and details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = routine.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    ),
                    color = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Room or category badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                    ) {
                        Text(
                            text = routine.roomOrCategory,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = routine.categoryColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Duration
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = routine.durationText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Add Plus Button
            Surface(
                onClick = onClick,
                shape = CircleShape,
                color = Color(0xFFFFEBEE),
                border = BorderStroke(1.dp, Color(0xFFFFCDD2)),
                modifier = Modifier
                    .size(34.dp)
                    .testTag("btn_add_routine_${routine.id}")
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "افزودن به تایملاین",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineDateTopAppBar(
    currentDateText: String,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onBack: () -> Unit,
    onOpenNotifications: () -> Unit = {}
) {
    Surface(
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF3F4F6))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        IconButton(
                            onClick = onPreviousDay,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("btn_timeline_prev_day")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight, // RTL forward/prev
                                contentDescription = "روز قبل",
                                tint = Color(0xFF374151),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Today,
                                contentDescription = null,
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currentDateText,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = Color(0xFF1F2937)
                            )
                        }

                        IconButton(
                            onClick = onNextDay,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("btn_timeline_next_day")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft, // RTL backward/next
                                contentDescription = "روز بعد",
                                tint = Color(0xFF374151),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("btn_timeline_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت به خانه",
                            tint = Color(0xFF1F2937)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onOpenNotifications,
                        modifier = Modifier.testTag("btn_timeline_notifications")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF3F4F6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "هشدارهای نوتیفیکیشن",
                                tint = Color(0xFF4F46E5),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.testTag("timeline_top_bar")
            )
        }
    }
}

@Composable
fun TimelineSummaryHeader(eventsCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "برنامه‌ریزی و تقویم زمانی پیوسته",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF1F2937)
            )
            Text(
                text = "یکپارچه‌سازی وظایف خانه، پروژه‌های علمی و روتین شخصی",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = Color(0xFF6B7280)
            )
        }

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFEEF2FF)
        ) {
            Text(
                text = "$eventsCount فعالیت",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                ),
                color = Color(0xFF4F46E5),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun TimelineEventRow(
    event: TimelineEvent,
    isFirst: Boolean,
    isLast: Boolean,
    onToggleDone: () -> Unit,
    onDelete: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Left Column: Time Indicator (06:30a, etc.)
        Box(
            modifier = Modifier
                .width(62.dp)
                .padding(top = 10.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Text(
                text = event.startTime,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = if (event.isDone) Color(0xFF9CA3AF) else Color(0xFF4B5563)
            )
        }

        // Connecting Timeline Spine (Vertical line + node dot)
        Box(
            modifier = Modifier
                .width(26.dp)
                .fillMaxHeight()
                .drawBehind {
                    val centerX = size.width / 2
                    val dotCenterY = 22.dp.toPx()

                    // Vertical line connecting previous to current
                    if (!isFirst) {
                        drawLine(
                            color = Color(0xFFE5E7EB),
                            start = Offset(centerX, 0f),
                            end = Offset(centerX, dotCenterY),
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    // Vertical line connecting current to next
                    if (!isLast) {
                        drawLine(
                            color = Color(0xFFE5E7EB),
                            start = Offset(centerX, dotCenterY),
                            end = Offset(centerX, size.height),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                },
            contentAlignment = Alignment.TopCenter
        ) {
            // Node circle
            Box(
                modifier = Modifier
                    .padding(top = 14.dp)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(3.dp, event.categoryColor, CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Right Column: Task Card wrapped in SwipeableTaskCard
        SwipeableTaskCard(
            itemKey = event.id,
            taskTitle = event.title,
            onDelete = onDelete,
            shape = RoundedCornerShape(16.dp),
            confirmDialogMessage = "آیا از حذف فعالیت «${event.title}» مطمئن هستید؟",
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_timeline_event_${event.id}")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    // Colored Chip with Icon and Title
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = event.chipBgColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleDone() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = event.icon,
                                    contentDescription = null,
                                    tint = event.categoryColor,
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.5.sp
                                ),
                                textDecoration = if (event.isDone) TextDecoration.LineThrough else TextDecoration.None,
                                color = if (event.isDone) Color(0xFF9CA3AF) else Color(0xFF1F2937),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 20.dp)
                            )

                            if (event.isDone) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(event.categoryColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Duration text in smaller gray font
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = event.duration,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Color(0xFF6B7280)
                            )
                        }

                        Text(
                            text = event.categoryName,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Bottom navigation with exactly 3 symmetrical tabs:
 * Schedule, Templates, Calendar (Tags tab completely removed).
 */
@Composable
fun TimelineBottomNavigationBar(
    selectedTab: TimelineTab,
    onTabSelected: (TimelineTab) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("timeline_bottom_navigation")
    ) {
        NavigationBarItem(
            selected = selectedTab == TimelineTab.SCHEDULE,
            onClick = { onTabSelected(TimelineTab.SCHEDULE) },
            icon = { Icon(Icons.Default.Schedule, contentDescription = "برنامه") },
            label = { Text("Schedule", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2563EB),
                selectedTextColor = Color(0xFF2563EB),
                indicatorColor = Color(0xFFDBEAFE)
            )
        )

        NavigationBarItem(
            selected = selectedTab == TimelineTab.TEMPLATES,
            onClick = { onTabSelected(TimelineTab.TEMPLATES) },
            icon = { Icon(Icons.Default.Layers, contentDescription = "قالب‌ها") },
            label = { Text("Templates", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2563EB),
                selectedTextColor = Color(0xFF2563EB),
                indicatorColor = Color(0xFFDBEAFE)
            )
        )

        NavigationBarItem(
            selected = selectedTab == TimelineTab.CALENDAR,
            onClick = { onTabSelected(TimelineTab.CALENDAR) },
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "تقویم") },
            label = { Text("Calendar", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF2563EB),
                selectedTextColor = Color(0xFF2563EB),
                indicatorColor = Color(0xFFDBEAFE)
            )
        )
    }
}
