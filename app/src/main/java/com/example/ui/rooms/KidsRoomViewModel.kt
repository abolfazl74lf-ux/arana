package com.example.ui.rooms

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ChoreItem
import com.example.data.local.entity.KidsShoppingItem
import com.example.data.local.entity.StudyTask
import com.example.data.repository.HomeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Dedicated ViewModel for Kids' Room management:
 * - Chores checklist with gamified star points
 * - Homework and study tracker
 * - Dedicated school and clothing shopping list
 */
class KidsRoomViewModel(
    application: Application,
    private val repository: HomeRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application,
        HomeRepository(AppDatabase.getDatabase(application).homeManagementDao())
    )

    val chores: StateFlow<List<ChoreItem>> = repository.allChores
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studyTasks: StateFlow<List<StudyTask>> = repository.allStudyTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val kidsShopping: StateFlow<List<KidsShoppingItem>> = repository.allKidsShopping
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleChore(chore: ChoreItem) = viewModelScope.launch {
        repository.toggleChore(chore)
    }

    fun addChore(title: String, child: String, points: Int, emoji: String) {
        viewModelScope.launch {
            repository.addChore(
                ChoreItem(title = title, childName = child, rewardPoints = points, emoji = emoji)
            )
        }
    }

    fun toggleStudyTask(task: StudyTask) = viewModelScope.launch {
        repository.toggleStudyTask(task)
    }

    fun addStudyTask(subject: String, description: String, dueDate: String) {
        viewModelScope.launch {
            repository.addStudyTask(
                StudyTask(subject = subject, taskDescription = description, dueDate = dueDate)
            )
        }
    }

    fun toggleKidsShopping(item: KidsShoppingItem) = viewModelScope.launch {
        repository.toggleKidsShopping(item)
    }

    fun addKidsShopping(item: String, category: String, child: String) {
        viewModelScope.launch {
            repository.addKidsShopping(
                KidsShoppingItem(itemName = item, category = category, childName = child)
            )
        }
    }
}
