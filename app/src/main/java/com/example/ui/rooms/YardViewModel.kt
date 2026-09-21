package com.example.ui.rooms

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.GardenCalendarItem
import com.example.data.local.entity.MaintenanceItem
import com.example.data.local.entity.ToolItem
import com.example.data.repository.HomeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Dedicated ViewModel for Yard and Outdoor spaces:
 * - Seasonal garden calendar and planting schedule
 * - Outdoor home maintenance inspections and repairs
 * - Gardening and DIY workshop tool inventory
 */
class YardViewModel(
    application: Application,
    private val repository: HomeRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application,
        HomeRepository(AppDatabase.getDatabase(application).homeManagementDao())
    )

    val gardenTasks: StateFlow<List<GardenCalendarItem>> = repository.allGardenTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val maintenanceItems: StateFlow<List<MaintenanceItem>> = repository.allMaintenanceItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tools: StateFlow<List<ToolItem>> = repository.allTools
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleGardenTask(task: GardenCalendarItem) = viewModelScope.launch {
        repository.toggleGardenTask(task)
    }

    fun addGardenTask(title: String, area: String, season: String, date: String) {
        viewModelScope.launch {
            repository.addGardenTask(
                GardenCalendarItem(taskTitle = title, targetAreaOrPlant = area, seasonTag = season, scheduledDate = date)
            )
        }
    }

    fun toggleMaintenanceItem(item: MaintenanceItem) = viewModelScope.launch {
        repository.toggleMaintenanceItem(item)
    }

    fun addMaintenanceItem(title: String, frequency: String, status: String) {
        viewModelScope.launch {
            repository.addMaintenanceItem(
                MaintenanceItem(title = title, frequencyLabel = frequency, statusText = status)
            )
        }
    }

    fun toggleToolAvailability(tool: ToolItem) = viewModelScope.launch {
        repository.toggleToolAvailability(tool)
    }

    fun addTool(name: String, location: String, category: String) {
        viewModelScope.launch {
            repository.addTool(
                ToolItem(toolName = name, storageLocation = location, category = category)
            )
        }
    }
}
