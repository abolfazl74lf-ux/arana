package com.example.ui.rooms

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ApplianceCleanRoutine
import com.example.data.local.entity.MealPlanItem
import com.example.data.local.entity.PantryItem
import com.example.data.local.entity.SmartShoppingItem
import com.example.data.repository.HomeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Dedicated ViewModel for Kitchen management:
 * - Pantry inventory and low-stock auto-replenishment
 * - Smart grocery shopping list
 * - Weekly meal planner
 * - Kitchen appliances cleaning routines
 */
class KitchenViewModel(
    application: Application,
    private val repository: HomeRepository
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application,
        HomeRepository(AppDatabase.getDatabase(application).homeManagementDao())
    )

    val pantryItems: StateFlow<List<PantryItem>> = repository.allPantryItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shoppingItems: StateFlow<List<SmartShoppingItem>> = repository.allShoppingItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mealPlans: StateFlow<List<MealPlanItem>> = repository.allMealPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val applianceRoutines: StateFlow<List<ApplianceCleanRoutine>> = repository.allApplianceCleanRoutines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addPantryItem(name: String, category: String, quantity: Double, unit: String, expiryDays: Int, isLowStock: Boolean) {
        viewModelScope.launch {
            repository.addPantryItem(
                PantryItem(
                    name = name,
                    category = category,
                    quantity = quantity,
                    unit = unit,
                    expiryDaysLeft = expiryDays,
                    isLowStock = isLowStock
                )
            )
        }
    }

    fun deletePantryItem(id: Long) = viewModelScope.launch {
        repository.deletePantryItem(id)
    }

    fun addShoppingItem(name: String, quantity: String, category: String) {
        viewModelScope.launch {
            repository.addShoppingItem(
                SmartShoppingItem(itemName = name, quantity = quantity, category = category, addedFromRoom = "Manual")
            )
        }
    }

    fun toggleShoppingItem(item: SmartShoppingItem) = viewModelScope.launch {
        repository.toggleShoppingItem(item)
    }

    fun deleteShoppingItem(id: Long) = viewModelScope.launch {
        repository.deleteShoppingItem(id)
    }

    fun addMealPlan(day: String, mealType: String, recipe: String, prepTime: Int, notes: String) {
        viewModelScope.launch {
            repository.addMealPlan(
                MealPlanItem(dayOfWeek = day, mealType = mealType, recipeTitle = recipe, prepTimeMinutes = prepTime, notes = notes)
            )
        }
    }

    fun deleteMealPlan(id: Long) = viewModelScope.launch {
        repository.deleteMealPlan(id)
    }

    fun toggleApplianceClean(routine: ApplianceCleanRoutine) = viewModelScope.launch {
        repository.toggleApplianceClean(routine)
    }
}
