package com.example.ui.personal

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PersonalTask(
    val id: String,
    val title: String,
    val category: String,
    val isCompleted: Boolean = false
)

data class StretchItem(
    val id: String,
    val title: String,
    val durationText: String,
    val instruction: String,
    val isDone: Boolean = false
)

enum class MoodType(val emoji: String, val labelFa: String, val description: String) {
    CALM("🧘", "آرام و متمرکز", "ذهن آرام و آماده"),
    HAPPY("😊", "خوشحال و راضی", "حس خوب و امیدبخش"),
    ENERGETIC("⚡", "پرانرژی", "انگیزه بالا برای کارها"),
    TIRED("🥱", "کمی خسته", "نیاز به استراحت کوتاه"),
    STRESSED("😣", "تحت فشار", "نیاز به تنفس عمیق و خلوت")
}

enum class BreathingPhase(val labelFa: String, val durationSec: Int) {
    INHALE("دم عمیق", 4),
    HOLD("حبس نفس", 4),
    EXHALE("بازدم آرام", 4)
}

data class PersonalCareState(
    // 1. Physical Health
    val waterGlasses: Int = 5,
    val waterTargetGlasses: Int = 8,
    val lastSleepHours: Float = 7.5f,
    val sleepQuality: String = "خوب و عمیق",
    val stretches: List<StretchItem> = listOf(
        StretchItem(
            id = "str_neck",
            title = "کشش آرام گردن و سرشانه",
            durationText = "۳۰ ثانیه",
            instruction = "سر را به آرامی به چپ و راست متمایل کنید تا تنش شانه ناشی از کارهای خانه رها شود."
        ),
        StretchItem(
            id = "str_back",
            title = "حرکت کششی گربه و گاو (کمر)",
            durationText = "۴۵ ثانیه",
            instruction = "ستون فقرات را نرم و به آرامی قوس دهید تا خستگی خم شدن و بلند کردن وسایل برطرف شود."
        ),
        StretchItem(
            id = "str_wrist",
            title = "نرمش و چرخش مچ دست‌ها",
            durationText = "۳۰ ثانیه",
            instruction = "دست‌ها را مشت کرده و دایره‌وار بچرخانید تا مفصل مچ دست استراحت کند."
        )
    ),

    // 2. Mental Well-being
    val selectedMood: MoodType = MoodType.CALM,
    val meTimeMinutesScheduled: Int = 30,
    val isMeTimeActive: Boolean = false,
    val meTimeRemainingSeconds: Int = 30 * 60,
    val isBreathingActive: Boolean = false,
    val breathingPhase: BreathingPhase = BreathingPhase.INHALE,
    val breathingCycleSecondsLeft: Int = 4,

    // 3. Personal Growth & Tasks
    val personalTasks: List<PersonalTask> = listOf(
        PersonalTask(
            id = "p_task_1",
            title = "مطالعه کتاب «مثل یک هنرمند بدزدید»",
            category = "مطالعه و الهام",
            isCompleted = false
        ),
        PersonalTask(
            id = "p_task_2",
            title = "ایده‌پردازی برای پروژه جدید",
            category = "خلاقیت و توسعه",
            isCompleted = false
        ),
        PersonalTask(
            id = "p_task_3",
            title = "طراحی مفاهیم مینی‌گیم ایزومتریک",
            category = "طراحی و هنر",
            isCompleted = true
        )
    )
)

class PersonalCareViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(PersonalCareState())
    val uiState: StateFlow<PersonalCareState> = _uiState.asStateFlow()

    private var meTimeJob: Job? = null
    private var breathingJob: Job? = null

    // --- Physical Health Operations ---
    fun addWaterGlass() {
        val current = _uiState.value.waterGlasses
        if (current < 16) {
            _uiState.value = _uiState.value.copy(waterGlasses = current + 1)
        }
    }

    fun removeWaterGlass() {
        val current = _uiState.value.waterGlasses
        if (current > 0) {
            _uiState.value = _uiState.value.copy(waterGlasses = current - 1)
        }
    }

    fun resetWater() {
        _uiState.value = _uiState.value.copy(waterGlasses = 0)
    }

    fun toggleStretch(stretchId: String) {
        val updated = _uiState.value.stretches.map {
            if (it.id == stretchId) it.copy(isDone = !it.isDone) else it
        }
        _uiState.value = _uiState.value.copy(stretches = updated)
    }

    fun updateSleep(hours: Float, quality: String) {
        _uiState.value = _uiState.value.copy(
            lastSleepHours = hours.coerceIn(3f, 12f),
            sleepQuality = quality
        )
    }

    // --- Mental Well-being Operations ---
    fun selectMood(mood: MoodType) {
        _uiState.value = _uiState.value.copy(selectedMood = mood)
    }

    fun toggleMeTime(minutes: Int = 30) {
        val currentActive = _uiState.value.isMeTimeActive
        if (currentActive) {
            meTimeJob?.cancel()
            _uiState.value = _uiState.value.copy(
                isMeTimeActive = false,
                meTimeRemainingSeconds = minutes * 60
            )
        } else {
            _uiState.value = _uiState.value.copy(
                isMeTimeActive = true,
                meTimeMinutesScheduled = minutes,
                meTimeRemainingSeconds = minutes * 60
            )
            meTimeJob = viewModelScope.launch {
                while (_uiState.value.meTimeRemainingSeconds > 0 && _uiState.value.isMeTimeActive) {
                    delay(1000)
                    val remaining = _uiState.value.meTimeRemainingSeconds - 1
                    _uiState.value = _uiState.value.copy(meTimeRemainingSeconds = remaining)
                }
                if (_uiState.value.meTimeRemainingSeconds <= 0) {
                    _uiState.value = _uiState.value.copy(isMeTimeActive = false)
                }
            }
        }
    }

    fun toggleBreathingExercise() {
        val isActive = _uiState.value.isBreathingActive
        if (isActive) {
            breathingJob?.cancel()
            _uiState.value = _uiState.value.copy(isBreathingActive = false)
        } else {
            _uiState.value = _uiState.value.copy(
                isBreathingActive = true,
                breathingPhase = BreathingPhase.INHALE,
                breathingCycleSecondsLeft = 4
            )
            breathingJob = viewModelScope.launch {
                val phases = listOf(BreathingPhase.INHALE, BreathingPhase.HOLD, BreathingPhase.EXHALE)
                var phaseIndex = 0
                while (_uiState.value.isBreathingActive) {
                    val currentPhase = phases[phaseIndex]
                    for (sec in currentPhase.durationSec downTo 1) {
                        _uiState.value = _uiState.value.copy(
                            breathingPhase = currentPhase,
                            breathingCycleSecondsLeft = sec
                        )
                        delay(1000)
                    }
                    phaseIndex = (phaseIndex + 1) % phases.size
                }
            }
        }
    }

    // --- Personal Growth & Tasks Operations ---
    fun togglePersonalTask(taskId: String) {
        val updated = _uiState.value.personalTasks.map {
            if (it.id == taskId) it.copy(isCompleted = !it.isCompleted) else it
        }
        _uiState.value = _uiState.value.copy(personalTasks = updated)
    }

    fun addPersonalTask(title: String, category: String = "علاقه‌مندی شخصی") {
        if (title.isBlank()) return
        val newTask = PersonalTask(
            id = "p_task_${System.currentTimeMillis()}",
            title = title.trim(),
            category = category.trim().ifEmpty { "شخصی" },
            isCompleted = false
        )
        _uiState.value = _uiState.value.copy(
            personalTasks = listOf(newTask) + _uiState.value.personalTasks
        )
    }

    private var lastDeletedPersonalTask: Pair<Int, PersonalTask>? = null

    fun onDeleteTask(task: PersonalTask): PersonalTask? {
        val currentTasks = _uiState.value.personalTasks
        val index = currentTasks.indexOfFirst { it.id == task.id }
        if (index >= 0) {
            lastDeletedPersonalTask = Pair(index, task)
            val updated = currentTasks.toMutableList().apply { removeAt(index) }
            _uiState.value = _uiState.value.copy(personalTasks = updated)
            return task
        }
        return null
    }

    fun onUndoDelete(): PersonalTask? {
        val (savedIndex, task) = lastDeletedPersonalTask ?: return null
        val currentTasks = _uiState.value.personalTasks.toMutableList()
        val insertIndex = savedIndex.coerceIn(0, currentTasks.size)
        currentTasks.add(insertIndex, task)
        _uiState.value = _uiState.value.copy(personalTasks = currentTasks)
        lastDeletedPersonalTask = null
        return task
    }

    fun deletePersonalTask(taskId: String) {
        val task = _uiState.value.personalTasks.firstOrNull { it.id == taskId }
        if (task != null) {
            onDeleteTask(task)
        } else {
            val updated = _uiState.value.personalTasks.filterNot { it.id == taskId }
            _uiState.value = _uiState.value.copy(personalTasks = updated)
        }
    }

    override fun onCleared() {
        super.onCleared()
        meTimeJob?.cancel()
        breathingJob?.cancel()
    }
}
