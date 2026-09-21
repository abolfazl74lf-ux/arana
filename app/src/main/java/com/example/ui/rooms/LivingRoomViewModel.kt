package com.example.ui.rooms

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.GuestChecklistItem
import com.example.data.local.entity.LivingCleaningTask
import com.example.data.local.entity.PlantCareItem
import com.example.data.repository.HomeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Dedicated ViewModel for Living Room management:
 * - Living room cleaning routines and recurring intervals
 * - Houseplant care schedules (watering, fertilizing, leaf wiping)
 * - Guest readiness checklist
 */
class LivingRoomViewModel(
    application: Application,
    private val repository: HomeRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application,
        HomeRepository(AppDatabase.getDatabase(application).homeManagementDao())
    )

    val livingCleaningTasks: StateFlow<List<LivingCleaningTask>> = repository.allLivingCleaningTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val plants: StateFlow<List<PlantCareItem>> = repository.allPlants
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val guestChecklist: StateFlow<List<GuestChecklistItem>> = repository.allGuestChecklist
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleLivingCleaningTask(task: LivingCleaningTask) = viewModelScope.launch {
        repository.toggleLivingCleaningTask(task)
    }

    fun addLivingCleaningTask(title: String, frequency: String, minutes: Int) {
        viewModelScope.launch {
            repository.addLivingCleaningTask(
                LivingCleaningTask(title = title, frequency = frequency, estimatedMinutes = minutes)
            )
        }
    }

    fun waterPlant(plant: PlantCareItem) = viewModelScope.launch {
        repository.waterPlant(plant)
    }

    fun togglePlantLeafClean(plant: PlantCareItem) = viewModelScope.launch {
        repository.togglePlantLeafClean(plant)
    }

    fun togglePlantFertilizer(plant: PlantCareItem) = viewModelScope.launch {
        repository.togglePlantFertilizer(plant)
    }

    fun addPlant(name: String, location: String, interval: Int, sunlight: String) {
        viewModelScope.launch {
            repository.addPlant(
                PlantCareItem(
                    plantName = name,
                    location = location,
                    waterIntervalDays = interval,
                    daysUntilWatering = interval,
                    sunlightLevel = sunlight
                )
            )
        }
    }

    fun toggleGuestChecklist(item: GuestChecklistItem) = viewModelScope.launch {
        repository.toggleGuestChecklist(item)
    }

    fun addGuestChecklist(title: String, category: String) {
        viewModelScope.launch {
            repository.addGuestChecklistItem(GuestChecklistItem(taskTitle = title, category = category))
        }
    }
}
