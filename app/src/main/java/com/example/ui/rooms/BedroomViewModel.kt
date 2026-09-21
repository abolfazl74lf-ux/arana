package com.example.ui.rooms

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.PersonalRoutineItem
import com.example.data.local.entity.SpaceDeclutterItem
import com.example.data.local.entity.WardrobeItem
import com.example.data.repository.HomeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Dedicated ViewModel for Master Bedroom management:
 * - Wardrobe care and seasonal laundry tracking
 * - Personal routines and daily habit streaks
 * - Space decluttering and organization items
 */
class BedroomViewModel(
    application: Application,
    private val repository: HomeRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application,
        HomeRepository(AppDatabase.getDatabase(application).homeManagementDao())
    )

    val wardrobeItems: StateFlow<List<WardrobeItem>> = repository.allWardrobeItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val personalRoutines: StateFlow<List<PersonalRoutineItem>> = repository.allPersonalRoutines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val declutterItems: StateFlow<List<SpaceDeclutterItem>> = repository.allDeclutterItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleWardrobeItem(item: WardrobeItem) = viewModelScope.launch {
        repository.toggleWardrobeItem(item)
    }

    fun addWardrobeItem(title: String, season: String, careType: String, nextDate: String) {
        viewModelScope.launch {
            repository.addWardrobeItem(
                WardrobeItem(title = title, season = season, careType = careType, nextScheduleDate = nextDate)
            )
        }
    }

    fun togglePersonalRoutine(item: PersonalRoutineItem) = viewModelScope.launch {
        repository.togglePersonalRoutine(item)
    }

    fun addPersonalRoutine(title: String, timeOfDay: String) {
        viewModelScope.launch {
            repository.addPersonalRoutine(
                PersonalRoutineItem(title = title, timeOfDay = timeOfDay)
            )
        }
    }

    fun toggleDeclutterItem(item: SpaceDeclutterItem) = viewModelScope.launch {
        repository.toggleDeclutterItem(item)
    }

    fun addDeclutterItem(area: String, priority: String, notes: String) {
        viewModelScope.launch {
            repository.addDeclutterItem(
                SpaceDeclutterItem(areaName = area, priority = priority, reminderNotes = notes)
            )
        }
    }
}
