package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.ui.personal.MoodType
import com.example.ui.personal.PersonalCareViewModel
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
class PersonalCareViewModelTest {

    private lateinit var context: Application
    private lateinit var viewModel: PersonalCareViewModel

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        viewModel = PersonalCareViewModel(context)
    }

    @Test
    fun `test initial personal care state`() {
        val state = viewModel.uiState.value
        assertEquals(5, state.waterGlasses)
        assertEquals(8, state.waterTargetGlasses)
        assertEquals(3, state.stretches.size)
        assertEquals(MoodType.CALM, state.selectedMood)
        assertEquals(3, state.personalTasks.size)
        assertTrue(state.personalTasks.any { it.title.contains("مثل یک هنرمند بدزدید") })
        assertTrue(state.personalTasks.any { it.title.contains("ایده‌پردازی برای پروژه جدید") })
        assertTrue(state.personalTasks.any { it.title.contains("طراحی مفاهیم مینی‌گیم ایزومتریک") })
    }

    @Test
    fun `test water tracker operations`() {
        val initial = viewModel.uiState.value.waterGlasses
        viewModel.addWaterGlass()
        assertEquals(initial + 1, viewModel.uiState.value.waterGlasses)

        viewModel.removeWaterGlass()
        assertEquals(initial, viewModel.uiState.value.waterGlasses)

        viewModel.resetWater()
        assertEquals(0, viewModel.uiState.value.waterGlasses)
    }

    @Test
    fun `test stretching toggle`() {
        val stretchId = viewModel.uiState.value.stretches.first().id
        assertFalse(viewModel.uiState.value.stretches.first().isDone)

        viewModel.toggleStretch(stretchId)
        assertTrue(viewModel.uiState.value.stretches.first { it.id == stretchId }.isDone)

        viewModel.toggleStretch(stretchId)
        assertFalse(viewModel.uiState.value.stretches.first { it.id == stretchId }.isDone)
    }

    @Test
    fun `test mood selection`() {
        viewModel.selectMood(MoodType.ENERGETIC)
        assertEquals(MoodType.ENERGETIC, viewModel.uiState.value.selectedMood)
    }

    @Test
    fun `test personal tasks add and toggle`() {
        val initialCount = viewModel.uiState.value.personalTasks.size
        viewModel.addPersonalTask("یادگیری ترکیب رنگ پاستلی در جت‌پک کامپوز", "طراحی و توسعه")
        assertEquals(initialCount + 1, viewModel.uiState.value.personalTasks.size)

        val newTask = viewModel.uiState.value.personalTasks.first()
        assertEquals("یادگیری ترکیب رنگ پاستلی در جت‌پک کامپوز", newTask.title)
        assertFalse(newTask.isCompleted)

        viewModel.togglePersonalTask(newTask.id)
        assertTrue(viewModel.uiState.value.personalTasks.first { it.id == newTask.id }.isCompleted)

        viewModel.deletePersonalTask(newTask.id)
        assertEquals(initialCount, viewModel.uiState.value.personalTasks.size)
    }
}
