package com.example.ui.timeline

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FreeBreakfast
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Yard
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TimelineEvent(
    val id: String,
    val startTime: String,
    val duration: String,
    val title: String,
    val categoryName: String,
    val categoryColor: Color,
    val chipBgColor: Color,
    val icon: ImageVector,
    val isDone: Boolean = false
)

data class AvailableRoutine(
    val id: String,
    val title: String,
    val roomOrCategory: String,
    val durationText: String,
    val durationMin: Int,
    val categoryColor: Color,
    val chipBgColor: Color,
    val icon: ImageVector
)

enum class TimelineTab(val labelFa: String, val labelEn: String) {
    SCHEDULE("برنامه", "Schedule"),
    TEMPLATES("قالب‌ها", "Templates"),
    CALENDAR("تقویم", "Calendar")
}

data class DailyTimelineState(
    val currentDateText: String = "Mon, Sep 21, 2026",
    val dayOffset: Int = 0,
    val selectedTab: TimelineTab = TimelineTab.SCHEDULE,
    val availableRoutines: List<AvailableRoutine> = listOf(
        AvailableRoutine(
            id = "rt_1",
            title = "جاروبرقی کامل فرش‌ها و زیر مبل‌ها",
            roomOrCategory = "پذیرایی",
            durationText = "20 دقیقه",
            durationMin = 20,
            categoryColor = Color(0xFF2E7D32),
            chipBgColor = Color(0xFFE8F5E9),
            icon = Icons.Default.Yard
        ),
        AvailableRoutine(
            id = "rt_2",
            title = "تی کشیدن پارکت و سرامیک سالن",
            roomOrCategory = "پذیرایی",
            durationText = "15 دقیقه",
            durationMin = 15,
            categoryColor = Color(0xFF00838F),
            chipBgColor = Color(0xFFE0F7FA),
            icon = Icons.Default.Fastfood
        ),
        AvailableRoutine(
            id = "rt_3",
            title = "گردگیری میز تلویزیون و قفسه کتابخانه",
            roomOrCategory = "پذیرایی",
            durationText = "10 دقیقه",
            durationMin = 10,
            categoryColor = Color(0xFFEF6C00),
            chipBgColor = Color(0xFFFFF3E0),
            icon = Icons.Default.AutoStories
        ),
        AvailableRoutine(
            id = "rt_4",
            title = "بررسی کدهای پایتون پردازش تصویر",
            roomOrCategory = "توسعه فردی",
            durationText = "90 دقیقه",
            durationMin = 90,
            categoryColor = Color(0xFF8E24AA),
            chipBgColor = Color(0xFFF3E5F5),
            icon = Icons.Default.Code
        ),
        AvailableRoutine(
            id = "rt_5",
            title = "سنتز هیدروژل در آزمایشگاه",
            roomOrCategory = "پروژه",
            durationText = "150 دقیقه",
            durationMin = 150,
            categoryColor = Color(0xFF1976D2),
            chipBgColor = Color(0xFFE3F2FD),
            icon = Icons.Default.Science
        )
    ),
    val events: List<TimelineEvent> = listOf(
        TimelineEvent(
            id = "evt_1",
            startTime = "06:30a",
            duration = "1 h",
            title = "صرف صبحانه و روتین شخصی",
            categoryName = "روتین شخصی",
            categoryColor = Color(0xFFE91E63), // Pink
            chipBgColor = Color(0xFFFCE4EC),
            icon = Icons.Default.FreeBreakfast
        ),
        TimelineEvent(
            id = "evt_2",
            startTime = "07:30a",
            duration = "1 h 30 m",
            title = "بررسی کدهای پایتون پردازش تصویر",
            categoryName = "کدنویسی و تحقیق",
            categoryColor = Color(0xFF8E24AA), // Purple
            chipBgColor = Color(0xFFF3E5F5),
            icon = Icons.Default.Code
        ),
        TimelineEvent(
            id = "evt_3",
            startTime = "09:00a",
            duration = "30 min",
            title = "آبیاری گیاهان پذیرایی و مرتب‌سازی",
            categoryName = "رسیدگی به خانه",
            categoryColor = Color(0xFF2E7D32), // Green
            chipBgColor = Color(0xFFE8F5E9),
            icon = Icons.Default.Yard
        ),
        TimelineEvent(
            id = "evt_4",
            startTime = "09:30a",
            duration = "2 h 30 m",
            title = "سنتز هیدروژل در آزمایشگاه",
            categoryName = "آزمایشگاه و پژوهش",
            categoryColor = Color(0xFF1976D2), // Blue
            chipBgColor = Color(0xFFE3F2FD),
            icon = Icons.Default.Science
        ),
        TimelineEvent(
            id = "evt_5",
            startTime = "12:00p",
            duration = "1 h",
            title = "استراحت و ناهار",
            categoryName = "استراحت و تجدید قوا",
            categoryColor = Color(0xFF6D4C41), // Brown
            chipBgColor = Color(0xFFEFEBE9),
            icon = Icons.Default.Fastfood
        ),
        TimelineEvent(
            id = "evt_6",
            startTime = "01:00p",
            duration = "1 h 30 m",
            title = "جلسه مشاوره پایان‌نامه با میرسلیمی",
            categoryName = "جلسه دانشگاهی",
            categoryColor = Color(0xFFD32F2F), // Red
            chipBgColor = Color(0xFFFFEBEE),
            icon = Icons.Default.School
        ),
        TimelineEvent(
            id = "evt_7",
            startTime = "02:30p",
            duration = "2 h",
            title = "نوشتن فصل پیش‌بینی و ایده‌پردازی کتاب",
            categoryName = "نویسندگی و تألیف",
            categoryColor = Color(0xFFE65100), // Orange
            chipBgColor = Color(0xFFFFF3E0),
            icon = Icons.Default.AutoStories
        )
    )
)

class DailyTimelineViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DailyTimelineState())
    val uiState: StateFlow<DailyTimelineState> = _uiState.asStateFlow()

    private val datesList = listOf(
        "Sun, Sep 20, 2026",
        "Mon, Sep 21, 2026",
        "Tue, Sep 22, 2026",
        "Wed, Sep 23, 2026",
        "Thu, Sep 24, 2026"
    )

    fun navigatePreviousDay() {
        val newOffset = (_uiState.value.dayOffset - 1).coerceAtLeast(-1)
        val dateIdx = (newOffset + 1).coerceIn(0, datesList.size - 1)
        _uiState.value = _uiState.value.copy(
            dayOffset = newOffset,
            currentDateText = datesList[dateIdx]
        )
    }

    fun navigateNextDay() {
        val newOffset = (_uiState.value.dayOffset + 1).coerceAtMost(3)
        val dateIdx = (newOffset + 1).coerceIn(0, datesList.size - 1)
        _uiState.value = _uiState.value.copy(
            dayOffset = newOffset,
            currentDateText = datesList[dateIdx]
        )
    }

    fun selectTab(tab: TimelineTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun toggleEventDone(eventId: String) {
        val updated = _uiState.value.events.map {
            if (it.id == eventId) it.copy(isDone = !it.isDone) else it
        }
        _uiState.value = _uiState.value.copy(events = updated)
    }

    fun addEvent(
        startTime: String,
        duration: String,
        title: String,
        categoryName: String,
        color: Color,
        icon: ImageVector
    ) {
        val newEvt = TimelineEvent(
            id = "evt_${System.currentTimeMillis()}",
            startTime = startTime,
            duration = duration,
            title = title,
            categoryName = categoryName,
            categoryColor = color,
            chipBgColor = color.copy(alpha = 0.15f),
            icon = icon
        )
        _uiState.value = _uiState.value.copy(
            events = _uiState.value.events + newEvt
        )
    }

    fun addRoutineToTimeline(routine: AvailableRoutine) {
        val nextTime = calculateNextAvailableTime()
        val newEvt = TimelineEvent(
            id = "evt_${System.currentTimeMillis()}",
            startTime = nextTime,
            duration = routine.durationText,
            title = routine.title,
            categoryName = routine.roomOrCategory,
            categoryColor = routine.categoryColor,
            chipBgColor = routine.chipBgColor,
            icon = routine.icon
        )
        _uiState.value = _uiState.value.copy(
            events = _uiState.value.events + newEvt
        )
    }

    private var lastDeletedEvent: Pair<Int, TimelineEvent>? = null
    private var lastDeletedRoutine: Pair<Int, AvailableRoutine>? = null

    /**
     * Deletes a task event from the timeline list and saves it for undo.
     */
    fun onDeleteTask(event: TimelineEvent): TimelineEvent? {
        val currentEvents = _uiState.value.events
        val index = currentEvents.indexOfFirst { it.id == event.id }
        if (index >= 0) {
            lastDeletedEvent = Pair(index, event)
            val updated = currentEvents.toMutableList().apply { removeAt(index) }
            _uiState.value = _uiState.value.copy(events = updated)
            return event
        }
        return null
    }

    fun onDeleteEventById(eventId: String): TimelineEvent? {
        val event = _uiState.value.events.firstOrNull { it.id == eventId } ?: return null
        return onDeleteTask(event)
    }

    /**
     * Restores the last deleted event back to its original position in the timeline.
     */
    fun onUndoDelete(): TimelineEvent? {
        val (savedIndex, event) = lastDeletedEvent ?: return null
        val currentEvents = _uiState.value.events.toMutableList()
        val insertIndex = savedIndex.coerceIn(0, currentEvents.size)
        currentEvents.add(insertIndex, event)
        _uiState.value = _uiState.value.copy(events = currentEvents)
        lastDeletedEvent = null
        return event
    }

    /**
     * Deletes a routine from available routines and saves for undo.
     */
    fun onDeleteRoutine(routine: AvailableRoutine): AvailableRoutine? {
        val currentRoutines = _uiState.value.availableRoutines
        val index = currentRoutines.indexOfFirst { it.id == routine.id }
        if (index >= 0) {
            lastDeletedRoutine = Pair(index, routine)
            val updated = currentRoutines.toMutableList().apply { removeAt(index) }
            _uiState.value = _uiState.value.copy(availableRoutines = updated)
            return routine
        }
        return null
    }

    /**
     * Restores the last deleted routine back to its original position.
     */
    fun onUndoDeleteRoutine(): AvailableRoutine? {
        val (savedIndex, routine) = lastDeletedRoutine ?: return null
        val currentRoutines = _uiState.value.availableRoutines.toMutableList()
        val insertIndex = savedIndex.coerceIn(0, currentRoutines.size)
        currentRoutines.add(insertIndex, routine)
        _uiState.value = _uiState.value.copy(availableRoutines = currentRoutines)
        lastDeletedRoutine = null
        return routine
    }

    private fun calculateNextAvailableTime(): String {
        val times = listOf("04:30p", "05:15p", "06:00p", "07:00p", "08:00p", "09:00p")
        val currentAdded = (_uiState.value.events.size - 7).coerceAtLeast(0)
        return times.getOrElse(currentAdded) { "10:00p" }
    }
}
