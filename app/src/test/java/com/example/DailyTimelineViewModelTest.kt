package com.example

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Science
import androidx.compose.ui.graphics.Color
import com.example.ui.timeline.DailyTimelineViewModel
import com.example.ui.timeline.TimelineTab
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DailyTimelineViewModelTest {

    private lateinit var viewModel: DailyTimelineViewModel

    @Before
    fun setUp() {
        viewModel = DailyTimelineViewModel()
    }

    @Test
    fun `test initial state and requested mock timeline events`() {
        val state = viewModel.uiState.value
        assertEquals("Mon, Sep 21, 2026", state.currentDateText)
        assertEquals(TimelineTab.SCHEDULE, state.selectedTab)
        assertEquals(7, state.events.size)

        // Verify required mock events from prompt
        val event1 = state.events[0]
        assertEquals("06:30a", event1.startTime)
        assertEquals("صرف صبحانه و روتین شخصی", event1.title)
        assertEquals("1 h", event1.duration)

        val event2 = state.events[1]
        assertEquals("07:30a", event2.startTime)
        assertEquals("بررسی کدهای پایتون پردازش تصویر", event2.title)
        assertEquals("1 h 30 m", event2.duration)

        val event3 = state.events[2]
        assertEquals("09:00a", event3.startTime)
        assertEquals("آبیاری گیاهان پذیرایی و مرتب‌سازی", event3.title)
        assertEquals("30 min", event3.duration)

        val event4 = state.events[3]
        assertEquals("09:30a", event4.startTime)
        assertEquals("سنتز هیدروژل در آزمایشگاه", event4.title)
        assertEquals("2 h 30 m", event4.duration)

        val event5 = state.events[4]
        assertEquals("12:00p", event5.startTime)
        assertEquals("استراحت و ناهار", event5.title)
        assertEquals("1 h", event5.duration)

        val event6 = state.events[5]
        assertEquals("01:00p", event6.startTime)
        assertEquals("جلسه مشاوره پایان‌نامه با میرسلیمی", event6.title)
        assertEquals("1 h 30 m", event6.duration)

        val event7 = state.events[6]
        assertEquals("02:30p", event7.startTime)
        assertEquals("نوشتن فصل پیش‌بینی و ایده‌پردازی کتاب", event7.title)
        assertEquals("2 h", event7.duration)
    }

    @Test
    fun `test day navigation`() {
        viewModel.navigateNextDay()
        assertEquals("Tue, Sep 22, 2026", viewModel.uiState.value.currentDateText)

        viewModel.navigatePreviousDay()
        assertEquals("Mon, Sep 21, 2026", viewModel.uiState.value.currentDateText)
    }

    @Test
    fun `test event toggle done`() {
        val eventId = viewModel.uiState.value.events.first().id
        assertFalse(viewModel.uiState.value.events.first().isDone)

        viewModel.toggleEventDone(eventId)
        assertTrue(viewModel.uiState.value.events.first { it.id == eventId }.isDone)

        viewModel.toggleEventDone(eventId)
        assertFalse(viewModel.uiState.value.events.first { it.id == eventId }.isDone)
    }

    @Test
    fun `test add custom event`() {
        val initialSize = viewModel.uiState.value.events.size
        viewModel.addEvent(
            startTime = "04:30p",
            duration = "45 min",
            title = "ورزش هوازی و طناب زدن",
            categoryName = "سلامتی",
            color = Color(0xFF00897B),
            icon = Icons.Default.Science
        )

        assertEquals(initialSize + 1, viewModel.uiState.value.events.size)
        val lastEvent = viewModel.uiState.value.events.last()
        assertEquals("04:30p", lastEvent.startTime)
        assertEquals("ورزش هوازی و طناب زدن", lastEvent.title)
    }

    @Test
    fun `test select bottom tab`() {
        viewModel.selectTab(TimelineTab.CALENDAR)
        assertEquals(TimelineTab.CALENDAR, viewModel.uiState.value.selectedTab)

        viewModel.selectTab(TimelineTab.TEMPLATES)
        assertEquals(TimelineTab.TEMPLATES, viewModel.uiState.value.selectedTab)
    }

    @Test
    fun `test available routines mock data and adding from sheet`() {
        val routines = viewModel.uiState.value.availableRoutines
        assertEquals(5, routines.size)

        val r1 = routines[0]
        assertEquals("جاروبرقی کامل فرش‌ها و زیر مبل‌ها", r1.title)
        assertEquals("پذیرایی", r1.roomOrCategory)
        assertEquals("20 دقیقه", r1.durationText)

        val r2 = routines[1]
        assertEquals("تی کشیدن پارکت و سرامیک سالن", r2.title)
        assertEquals("پذیرایی", r2.roomOrCategory)
        assertEquals("15 دقیقه", r2.durationText)

        val r3 = routines[2]
        assertEquals("گردگیری میز تلویزیون و قفسه کتابخانه", r3.title)
        assertEquals("پذیرایی", r3.roomOrCategory)
        assertEquals("10 دقیقه", r3.durationText)

        val r4 = routines[3]
        assertEquals("بررسی کدهای پایتون پردازش تصویر", r4.title)
        assertEquals("توسعه فردی", r4.roomOrCategory)
        assertEquals("90 دقیقه", r4.durationText)

        val r5 = routines[4]
        assertEquals("سنتز هیدروژل در آزمایشگاه", r5.title)
        assertEquals("پروژه", r5.roomOrCategory)
        assertEquals("150 دقیقه", r5.durationText)

        // Test adding routine to timeline
        val initialEventsCount = viewModel.uiState.value.events.size
        viewModel.addRoutineToTimeline(r1)
        assertEquals(initialEventsCount + 1, viewModel.uiState.value.events.size)

        val addedEvent = viewModel.uiState.value.events.last()
        assertEquals(r1.title, addedEvent.title)
        assertEquals(r1.roomOrCategory, addedEvent.categoryName)
        assertEquals(r1.durationText, addedEvent.duration)
        assertFalse(addedEvent.isDone)
    }
}
