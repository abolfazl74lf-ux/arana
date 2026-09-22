package com.example.ui.personal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.SwipeableTaskCard
import kotlinx.coroutines.launch

// Gentle, soothing pastel palette for personal care
private val PastelGreenBg = Color(0xFFF1F8F4)
private val PastelGreenPrimary = Color(0xFF2E7D32)
private val PastelGreenAccent = Color(0xFFA5D6A7)

private val PastelRoseBg = Color(0xFFFDF2F4)
private val PastelRosePrimary = Color(0xFFC2185B)
private val PastelRoseAccent = Color(0xFFF8BBD0)

private val PastelSkyBg = Color(0xFFF0F7FD)
private val PastelSkyPrimary = Color(0xFF0277BD)
private val PastelSkyAccent = Color(0xFF81D4FA)

private val PastelAmberBg = Color(0xFFFFFBEB)
private val PastelAmberPrimary = Color(0xFFB45309)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalCareScreen(
    onBack: () -> Unit,
    viewModel: PersonalCareViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddTaskDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.testTag("personal_care_snackbar_host")
            )
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PastelGreenAccent.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Spa,
                                contentDescription = null,
                                tint = PastelGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "فضای شخصی مدیر خانه",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "مراقبت از خود • سلامت روان • اهداف فردی",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("btn_personal_care_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت به خانه"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.testTag("personal_care_top_bar")
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("personal_care_lazy_column"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Calm Inspirational Banner
            item {
                CalmInspirationalBanner()
            }

            // SECTION 1: PHYSICAL HEALTH
            item {
                SectionHeader(
                    icon = Icons.Default.FitnessCenter,
                    title = "۱. سلامت جسمی (Physical Health)",
                    subtitle = "آبرسانی منظم، رفع گرفتگی عضلات و پایش خواب",
                    accentColor = PastelSkyPrimary
                )
            }

            item {
                WaterTrackerCard(
                    glasses = state.waterGlasses,
                    target = state.waterTargetGlasses,
                    onAdd = { viewModel.addWaterGlass() },
                    onRemove = { viewModel.removeWaterGlass() },
                    onReset = { viewModel.resetWater() }
                )
            }

            item {
                StretchingReminderCard(
                    stretches = state.stretches,
                    onToggleStretch = { viewModel.toggleStretch(it) }
                )
            }

            item {
                SleepLogCard(
                    hours = state.lastSleepHours,
                    quality = state.sleepQuality,
                    onUpdateHours = { viewModel.updateSleep(it, state.sleepQuality) }
                )
            }

            // SECTION 2: MENTAL WELL-BEING
            item {
                Spacer(modifier = Modifier.height(4.dp))
                SectionHeader(
                    icon = Icons.Default.SelfImprovement,
                    title = "۲. سلامت روان و آرامش (Mental Well-being)",
                    subtitle = "تایمر وقت اختصاصی، پایش خلق‌وخو و تنفس آرامش‌بخش",
                    accentColor = PastelRosePrimary
                )
            }

            item {
                MeTimeSchedulerCard(
                    isMeTimeActive = state.isMeTimeActive,
                    scheduledMinutes = state.meTimeMinutesScheduled,
                    remainingSeconds = state.meTimeRemainingSeconds,
                    onToggle = { viewModel.toggleMeTime(30) }
                )
            }

            item {
                MoodTrackerCard(
                    selectedMood = state.selectedMood,
                    onSelectMood = { viewModel.selectMood(it) }
                )
            }

            item {
                BreathingExerciseCard(
                    isActive = state.isBreathingActive,
                    phase = state.breathingPhase,
                    secondsLeft = state.breathingCycleSecondsLeft,
                    onToggle = { viewModel.toggleBreathingExercise() }
                )
            }

            // SECTION 3: PERSONAL GROWTH & TASKS
            item {
                Spacer(modifier = Modifier.height(4.dp))
                SectionHeader(
                    icon = Icons.Default.Lightbulb,
                    title = "۳. توسعه فردی و علایق (Personal Growth)",
                    subtitle = "اهداف، کتاب‌ها و ایده‌های مستقل از کارهای خانه",
                    accentColor = PastelAmberPrimary
                )
            }

            item {
                PersonalTasksHeader(
                    onAddNewTaskClick = { showAddTaskDialog = true }
                )
            }

            items(state.personalTasks, key = { it.id }) { task ->
                SwipeableTaskCard(
                    itemKey = task.id,
                    taskTitle = task.title,
                    onDelete = {
                        val deleted = viewModel.onDeleteTask(task)
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
                    },
                    showDeleteIcon = false,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    PersonalTaskItemCard(
                        task = task,
                        onToggle = { viewModel.togglePersonalTask(task.id) },
                        onDelete = {
                            val deleted = viewModel.onDeleteTask(task)
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

            item {
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    if (showAddTaskDialog) {
        AddPersonalTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { title, category ->
                viewModel.addPersonalTask(title, category)
                showAddTaskDialog = false
            }
        )
    }
}

// --- SUB-COMPONENTS: HEADER & BANNERS ---

@Composable
fun CalmInspirationalBanner() {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = PastelGreenBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, PastelGreenAccent.copy(alpha = 0.6f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("banner_calm_quote")
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🌿", fontSize = 22.sp)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "«مراقبت از خود یک انتخاب نیست، یک اولویت است.»",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 22.sp
                    ),
                    color = PastelGreenPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "زمانی کوتاه برای خودت اختصاص بده تا با انرژی و آرامش بیشتری به زندگی لبخند بزنی.",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --- PHYSICAL HEALTH COMPONENTS ---

@Composable
fun WaterTrackerCard(
    glasses: Int,
    target: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    onReset: () -> Unit
) {
    val progress = (glasses.toFloat() / target.toFloat()).coerceIn(0f, 1f)
    val milliliters = glasses * 250
    val targetMl = target * 250

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PastelSkyBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, PastelSkyAccent.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_water_tracker")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = PastelSkyPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ردیاب نوشیدن آب روزانه",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PastelSkyAccent.copy(alpha = 0.35f)
                ) {
                    Text(
                        text = "$glasses از $target لیوان ($milliliters/$targetMl میلی‌لیتر)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp
                        ),
                        color = PastelSkyPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                strokeCap = StrokeCap.Round,
                color = PastelSkyPrimary,
                trackColor = PastelSkyAccent.copy(alpha = 0.35f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Glass indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in 1..target) {
                    val isDrank = i <= glasses
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (isDrank) PastelSkyPrimary else Color.White)
                            .border(1.dp, PastelSkyAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalDrink,
                            contentDescription = null,
                            tint = if (isDrank) Color.White else PastelSkyAccent,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAdd,
                    colors = ButtonDefaults.buttonColors(containerColor = PastelSkyPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1.5f)
                        .testTag("btn_add_water")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("نوشیدن یک لیوان")
                }

                OutlinedButton(
                    onClick = onRemove,
                    shape = RoundedCornerShape(12.dp),
                    enabled = glasses > 0,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("-۱ لیوان")
                }

                IconButton(
                    onClick = onReset,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "ریست آب",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun StretchingReminderCard(
    stretches: List<StretchItem>,
    onToggleStretch: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_stretching_reminder")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🧘‍♂️", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "حرکات کششی کوتاه رفع خستگی",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "کاهش فشار روی گردن و کمر پس از ایستادن و کارهای فیزیکی خانه",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            stretches.forEach { stretch ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (stretch.isDone) PastelGreenBg else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onToggleStretch(stretch.id) }
                        .testTag("stretch_item_${stretch.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (stretch.isDone) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline,
                            contentDescription = null,
                            tint = if (stretch.isDone) PastelGreenPrimary else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = stretch.title,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    textDecoration = if (stretch.isDone) TextDecoration.LineThrough else TextDecoration.None,
                                    color = if (stretch.isDone) PastelGreenPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = stretch.durationText,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stretch.instruction,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SleepLogCard(
    hours: Float,
    quality: String,
    onUpdateHours: (Float) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5).copy(alpha = 0.6f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCE93D8).copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_sleep_log")
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF8E24AA).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bedtime,
                    contentDescription = null,
                    tint = Color(0xFF6A1B9A),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ثبت وضعیت خواب دیشب",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "میزان خواب: $hours ساعت • کیفیت: $quality",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                FilledIconButton(
                    onClick = { onUpdateHours(hours - 0.5f) },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF6A1B9A)
                    ),
                    modifier = Modifier.size(32.dp)
                ) {
                    Text("-", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(4.dp))
                FilledIconButton(
                    onClick = { onUpdateHours(hours + 0.5f) },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF6A1B9A)
                    ),
                    modifier = Modifier.size(32.dp)
                ) {
                    Text("+", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

// --- MENTAL WELL-BEING COMPONENTS ---

@Composable
fun MeTimeSchedulerCard(
    isMeTimeActive: Boolean,
    scheduledMinutes: Int,
    remainingSeconds: Int,
    onToggle: () -> Unit
) {
    val minutesLeft = remainingSeconds / 60
    val secondsLeft = remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutesLeft, secondsLeft)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PastelRoseBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, PastelRoseAccent.copy(alpha = 0.6f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_me_time")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LockClock,
                        contentDescription = null,
                        tint = PastelRosePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "زمانبندی «وقت برای خود» (Me-Time)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isMeTimeActive) PastelRosePrimary else PastelRoseAccent.copy(alpha = 0.4f)
                ) {
                    Text(
                        text = if (isMeTimeActive) "فعال • $timeFormatted" else "$scheduledMinutes دقیقه",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isMeTimeActive) Color.White else PastelRosePrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isMeTimeActive)
                    "لحظاتی بدون توجه به شستشو و پخت‌وپز، فقط برای استراحت و تنفس عمیق خودتان."
                else
                    "یک زمان ۳۰ دقیقه‌ای بدون کار رزرو کنید و اعلانات کارهای منزل را به تعویق بیندازید.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onToggle,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isMeTimeActive) Color(0xFFD32F2F) else PastelRosePrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_me_time_toggle")
            ) {
                Icon(
                    imageVector = if (isMeTimeActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isMeTimeActive) "پایان وقت اختصاصی" else "شروع ۳۰ دقیقه وقت برای خود")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MoodTrackerCard(
    selectedMood: MoodType,
    onSelectMood: (MoodType) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_mood_tracker")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🎭", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ردیاب خلق‌وخوی امروز شما",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MoodType.values().forEach { mood ->
                    val isSelected = mood == selectedMood
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier
                            .clickable { onSelectMood(mood) }
                            .testTag("mood_chip_${mood.name}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = mood.emoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = mood.labelFa,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "حس و حال کنونی: ${selectedMood.description}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun BreathingExerciseCard(
    isActive: Boolean,
    phase: BreathingPhase,
    secondsLeft: Int,
    onToggle: () -> Unit
) {
    val scaleAnim by animateFloatAsState(
        targetValue = when {
            !isActive -> 1f
            phase == BreathingPhase.INHALE -> 1.35f
            phase == BreathingPhase.HOLD -> 1.35f
            else -> 0.85f
        },
        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
        label = "breath_scale"
    )

    val circleColor by animateColorAsState(
        targetValue = when (phase) {
            BreathingPhase.INHALE -> Color(0xFF81D4FA)
            BreathingPhase.HOLD -> Color(0xFFFFF59D)
            BreathingPhase.EXHALE -> Color(0xFFA5D6A7)
        },
        animationSpec = tween(1000),
        label = "breath_color"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC5E1A5)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_breathing_exercise")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    tint = Color(0xFF33691E),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تمرین تنفسی ۱ دقیقه‌ای (کاهش استرس)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1B5E20)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Animated Pulsing Breathing Orb
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .scale(scaleAnim)
                    .clip(CircleShape)
                    .background(circleColor)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isActive) phase.labelFa else "شروع",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF1B5E20)
                    )
                    if (isActive) {
                        Text(
                            text = "$secondsLeft ثانیه",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color(0xFF1B5E20)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onToggle,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isActive) Color(0xFFE57373) else Color(0xFF558B2F)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("btn_breathing_toggle")
            ) {
                Icon(
                    imageVector = if (isActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isActive) "توقف تمرین تنفس" else "شروع تمرین تنفس ۴-۴-۴")
            }
        }
    }
}

// --- PERSONAL GROWTH & TASKS COMPONENTS ---

@Composable
fun PersonalTasksHeader(onAddNewTaskClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "اهداف و لیست کارهای شخصی",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "فضایی برای ایده‌ها، پروژه‌ها و کتاب‌های خواندنی شما",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Button(
            onClick = onAddNewTaskClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PastelAmberPrimary),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            modifier = Modifier.testTag("btn_add_personal_task_dialog")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("هدف جدید", fontSize = 11.5.sp)
        }
    }
}

@Composable
fun PersonalTaskItemCard(
    task: PersonalTask,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("personal_task_card_${task.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggle,
                modifier = Modifier
                    .size(32.dp)
                    .testTag("checkbox_task_${task.id}")
            ) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline,
                    contentDescription = null,
                    tint = if (task.isCompleted) PastelGreenPrimary else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.SemiBold
                    ),
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = PastelAmberBg
                ) {
                    Text(
                        text = task.category,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
                        color = PastelAmberPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "حذف تسک",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun AddPersonalTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("مطالعه و خلاقیت") }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("dialog_add_personal_task")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "افزودن هدف یا ایده شخصی",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان هدف / ایده") },
                    placeholder = { Text("مثلاً: نقاشی دیجیتال، یادگیری زبان...") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("دسته‌بندی") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp)) {
                        Text("انصراف")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onConfirm(title, category)
                            }
                        },
                        enabled = title.isNotBlank(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PastelAmberPrimary)
                    ) {
                        Text("ثبت هدف")
                    }
                }
            }
        }
    }
}
