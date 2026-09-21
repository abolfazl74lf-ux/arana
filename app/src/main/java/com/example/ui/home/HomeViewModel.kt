package com.example.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ApplianceCleanRoutine
import com.example.data.local.entity.ChoreItem
import com.example.data.local.entity.GardenCalendarItem
import com.example.data.local.entity.GuestChecklistItem
import com.example.data.local.entity.KidsShoppingItem
import com.example.data.local.entity.LivingCleaningTask
import com.example.data.local.entity.MaintenanceItem
import com.example.data.local.entity.MealPlanItem
import com.example.data.local.entity.PantryItem
import com.example.data.local.entity.PersonalRoutineItem
import com.example.data.local.entity.PlantCareItem
import com.example.data.local.entity.SmartShoppingItem
import com.example.data.local.entity.SpaceDeclutterItem
import com.example.data.local.entity.StudyTask
import com.example.data.local.entity.ToolItem
import com.example.data.local.entity.WardrobeItem
import com.example.data.repository.HomeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeOverviewStats(
    val kitchenExpiringCount: Int = 0,
    val kitchenShoppingCount: Int = 0,
    val plantsNeedingWaterCount: Int = 0,
    val livingCleaningPendingCount: Int = 0,
    val bedroomPendingRoutinesCount: Int = 0,
    val kidsCompletedPoints: Int = 0,
    val kidsPendingChores: Int = 0,
    val yardMaintenancePendingCount: Int = 0
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HomeRepository

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = HomeRepository(db.homeManagementDao())
    }

    // Kitchen
    val pantryItems: StateFlow<List<PantryItem>> = repository.allPantryItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val shoppingItems: StateFlow<List<SmartShoppingItem>> = repository.allShoppingItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mealPlans: StateFlow<List<MealPlanItem>> = repository.allMealPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val applianceRoutines: StateFlow<List<ApplianceCleanRoutine>> = repository.allApplianceCleanRoutines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Living Room
    val livingCleaningTasks: StateFlow<List<LivingCleaningTask>> = repository.allLivingCleaningTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val plants: StateFlow<List<PlantCareItem>> = repository.allPlants
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val guestChecklist: StateFlow<List<GuestChecklistItem>> = repository.allGuestChecklist
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Bedroom
    val wardrobeItems: StateFlow<List<WardrobeItem>> = repository.allWardrobeItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val personalRoutines: StateFlow<List<PersonalRoutineItem>> = repository.allPersonalRoutines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val declutterItems: StateFlow<List<SpaceDeclutterItem>> = repository.allDeclutterItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Kids Room
    val chores: StateFlow<List<ChoreItem>> = repository.allChores
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studyTasks: StateFlow<List<StudyTask>> = repository.allStudyTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val kidsShopping: StateFlow<List<KidsShoppingItem>> = repository.allKidsShopping
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Yard & Outdoors
    val gardenTasks: StateFlow<List<GardenCalendarItem>> = repository.allGardenTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val maintenanceItems: StateFlow<List<MaintenanceItem>> = repository.allMaintenanceItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tools: StateFlow<List<ToolItem>> = repository.allTools
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Overview Stats for Interactive House View
    val overviewStats: StateFlow<HomeOverviewStats> = combine(
        combine(pantryItems, shoppingItems, plants) { p, s, pl ->
            Triple(p, s, pl)
        },
        combine(livingCleaningTasks, personalRoutines, chores, maintenanceItems) { c, r, ch, m ->
            listOf(c, r, ch, m)
        }
    ) { (pList, sList, plList), secondGroup ->
        @Suppress("UNCHECKED_CAST")
        val cList = secondGroup[0] as List<LivingCleaningTask>
        @Suppress("UNCHECKED_CAST")
        val rList = secondGroup[1] as List<PersonalRoutineItem>
        @Suppress("UNCHECKED_CAST")
        val chList = secondGroup[2] as List<ChoreItem>
        @Suppress("UNCHECKED_CAST")
        val mList = secondGroup[3] as List<MaintenanceItem>

        HomeOverviewStats(
            kitchenExpiringCount = pList.count { it.expiryDaysLeft <= 3 },
            kitchenShoppingCount = sList.count { !it.isBought },
            plantsNeedingWaterCount = plList.count { it.daysUntilWatering <= 2 },
            livingCleaningPendingCount = cList.count { !it.isDone },
            bedroomPendingRoutinesCount = rList.count { !it.isDoneToday },
            kidsCompletedPoints = chList.filter { it.isCompleted }.sumOf { it.rewardPoints },
            kidsPendingChores = chList.count { !it.isCompleted },
            yardMaintenancePendingCount = mList.count { !it.isDone }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeOverviewStats())

    // --- Actions ---

    // Kitchen Actions
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

    fun deletePantryItem(id: Long) = viewModelScope.launch { repository.deletePantryItem(id) }

    fun addShoppingItem(name: String, quantity: String, category: String) {
        viewModelScope.launch {
            repository.addShoppingItem(
                SmartShoppingItem(itemName = name, quantity = quantity, category = category, addedFromRoom = "Manual")
            )
        }
    }

    fun toggleShoppingItem(item: SmartShoppingItem) = viewModelScope.launch { repository.toggleShoppingItem(item) }
    fun deleteShoppingItem(id: Long) = viewModelScope.launch { repository.deleteShoppingItem(id) }

    fun addMealPlan(day: String, mealType: String, recipe: String, prepTime: Int, notes: String) {
        viewModelScope.launch {
            repository.addMealPlan(
                MealPlanItem(dayOfWeek = day, mealType = mealType, recipeTitle = recipe, prepTimeMinutes = prepTime, notes = notes)
            )
        }
    }

    fun deleteMealPlan(id: Long) = viewModelScope.launch { repository.deleteMealPlan(id) }

    fun toggleApplianceClean(routine: ApplianceCleanRoutine) = viewModelScope.launch { repository.toggleApplianceClean(routine) }

    // Living Room Actions
    fun toggleLivingCleaningTask(task: LivingCleaningTask) = viewModelScope.launch { repository.toggleLivingCleaningTask(task) }
    fun addLivingCleaningTask(title: String, frequency: String, minutes: Int) {
        viewModelScope.launch {
            repository.addLivingCleaningTask(LivingCleaningTask(title = title, frequency = frequency, estimatedMinutes = minutes))
        }
    }

    fun waterPlant(plant: PlantCareItem) = viewModelScope.launch { repository.waterPlant(plant) }
    fun togglePlantLeafClean(plant: PlantCareItem) = viewModelScope.launch { repository.togglePlantLeafClean(plant) }
    fun togglePlantFertilizer(plant: PlantCareItem) = viewModelScope.launch { repository.togglePlantFertilizer(plant) }
    fun addPlant(name: String, location: String, interval: Int, sunlight: String) {
        viewModelScope.launch {
            repository.addPlant(
                PlantCareItem(plantName = name, location = location, waterIntervalDays = interval, daysUntilWatering = interval, sunlightLevel = sunlight)
            )
        }
    }

    fun toggleGuestChecklist(item: GuestChecklistItem) = viewModelScope.launch { repository.toggleGuestChecklist(item) }
    fun addGuestChecklist(title: String, category: String) {
        viewModelScope.launch {
            repository.addGuestChecklistItem(GuestChecklistItem(taskTitle = title, category = category))
        }
    }

    // Bedroom Actions
    fun toggleWardrobeItem(item: WardrobeItem) = viewModelScope.launch { repository.toggleWardrobeItem(item) }
    fun addWardrobeItem(title: String, season: String, careType: String, nextDate: String) {
        viewModelScope.launch {
            repository.addWardrobeItem(WardrobeItem(title = title, season = season, careType = careType, nextScheduleDate = nextDate))
        }
    }

    fun togglePersonalRoutine(item: PersonalRoutineItem) = viewModelScope.launch { repository.togglePersonalRoutine(item) }
    fun addPersonalRoutine(title: String, timeOfDay: String) {
        viewModelScope.launch {
            repository.addPersonalRoutine(PersonalRoutineItem(title = title, timeOfDay = timeOfDay))
        }
    }

    fun toggleDeclutterItem(item: SpaceDeclutterItem) = viewModelScope.launch { repository.toggleDeclutterItem(item) }
    fun addDeclutterItem(area: String, priority: String, notes: String) {
        viewModelScope.launch {
            repository.addDeclutterItem(SpaceDeclutterItem(areaName = area, priority = priority, reminderNotes = notes))
        }
    }

    // Kids Actions
    fun toggleChore(chore: ChoreItem) = viewModelScope.launch { repository.toggleChore(chore) }
    fun addChore(title: String, child: String, points: Int, emoji: String) {
        viewModelScope.launch {
            repository.addChore(ChoreItem(title = title, childName = child, rewardPoints = points, emoji = emoji))
        }
    }

    fun toggleStudyTask(task: StudyTask) = viewModelScope.launch { repository.toggleStudyTask(task) }
    fun addStudyTask(subject: String, description: String, dueDate: String) {
        viewModelScope.launch {
            repository.addStudyTask(StudyTask(subject = subject, taskDescription = description, dueDate = dueDate))
        }
    }

    fun toggleKidsShopping(item: KidsShoppingItem) = viewModelScope.launch { repository.toggleKidsShopping(item) }
    fun addKidsShopping(item: String, category: String, child: String) {
        viewModelScope.launch {
            repository.addKidsShopping(KidsShoppingItem(itemName = item, category = category, childName = child))
        }
    }

    // Yard Actions
    fun toggleGardenTask(task: GardenCalendarItem) = viewModelScope.launch { repository.toggleGardenTask(task) }
    fun addGardenTask(title: String, area: String, season: String, date: String) {
        viewModelScope.launch {
            repository.addGardenTask(GardenCalendarItem(taskTitle = title, targetAreaOrPlant = area, seasonTag = season, scheduledDate = date))
        }
    }

    fun toggleMaintenanceItem(item: MaintenanceItem) = viewModelScope.launch { repository.toggleMaintenanceItem(item) }
    fun addMaintenanceItem(title: String, frequency: String, status: String) {
        viewModelScope.launch {
            repository.addMaintenanceItem(MaintenanceItem(title = title, frequencyLabel = frequency, statusText = status))
        }
    }

    fun toggleToolAvailability(tool: ToolItem) = viewModelScope.launch { repository.toggleToolAvailability(tool) }
    fun addTool(name: String, location: String, category: String) {
        viewModelScope.launch {
            repository.addTool(ToolItem(toolName = name, storageLocation = location, category = category))
        }
    }

    // --- Room Custom Image URIs ---
    private val _roomImageUris = kotlinx.coroutines.flow.MutableStateFlow<Map<com.example.domain.model.RoomType, String>>(emptyMap())
    val roomImageUris: StateFlow<Map<com.example.domain.model.RoomType, String>> = _roomImageUris

    fun setRoomImageUri(room: com.example.domain.model.RoomType, uri: String?) {
        val current = _roomImageUris.value.toMutableMap()
        if (uri != null) {
            current[room] = uri
        } else {
            current.remove(room)
        }
        _roomImageUris.value = current
    }

    // --- Housekeeping Tricks State ---
    private val _trickIndices = kotlinx.coroutines.flow.MutableStateFlow<Map<com.example.domain.model.RoomType, Int>>(emptyMap())
    val trickIndices: StateFlow<Map<com.example.domain.model.RoomType, Int>> = _trickIndices

    private val _selectedTrickForDetails = kotlinx.coroutines.flow.MutableStateFlow<com.example.domain.model.HousekeepingTrick?>(null)
    val selectedTrickForDetails: StateFlow<com.example.domain.model.HousekeepingTrick?> = _selectedTrickForDetails

    fun nextTrickForRoom(room: com.example.domain.model.RoomType) {
        val currentMap = _trickIndices.value.toMutableMap()
        val currentIndex = currentMap[room] ?: 0
        val tricksCount = com.example.domain.model.HousekeepingTricksProvider.getTricksForRoom(room).size
        val nextIndex = if (tricksCount > 0) (currentIndex + 1) % tricksCount else 0
        currentMap[room] = nextIndex
        _trickIndices.value = currentMap
    }

    fun openTrickDetails(trick: com.example.domain.model.HousekeepingTrick) {
        _selectedTrickForDetails.value = trick
    }

    fun dismissTrickDetails() {
        _selectedTrickForDetails.value = null
    }
}
