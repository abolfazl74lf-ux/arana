package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeManagementDao {

    // --- KITCHEN ---
    @Query("SELECT * FROM pantry_items ORDER BY expiryDaysLeft ASC")
    fun getAllPantryItems(): Flow<List<PantryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPantryItem(item: PantryItem): Long

    @Query("DELETE FROM pantry_items WHERE id = :id")
    suspend fun deletePantryItem(id: Long)

    @Query("SELECT * FROM smart_shopping_items ORDER BY isBought ASC, id DESC")
    fun getAllShoppingItems(): Flow<List<SmartShoppingItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingItem(item: SmartShoppingItem): Long

    @Update
    suspend fun updateShoppingItem(item: SmartShoppingItem)

    @Query("DELETE FROM smart_shopping_items WHERE id = :id")
    suspend fun deleteShoppingItem(id: Long)

    @Query("SELECT * FROM meal_plan_items ORDER BY id ASC")
    fun getAllMealPlans(): Flow<List<MealPlanItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealPlan(item: MealPlanItem): Long

    @Query("DELETE FROM meal_plan_items WHERE id = :id")
    suspend fun deleteMealPlan(id: Long)

    @Query("SELECT * FROM appliance_clean_routines ORDER BY daysRemaining ASC")
    fun getAllApplianceCleanRoutines(): Flow<List<ApplianceCleanRoutine>>

    @Update
    suspend fun updateApplianceCleanRoutine(item: ApplianceCleanRoutine)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApplianceCleanRoutine(item: ApplianceCleanRoutine): Long

    // --- LIVING ROOM ---
    @Query("SELECT * FROM living_cleaning_tasks ORDER BY isDone ASC, id ASC")
    fun getAllLivingCleaningTasks(): Flow<List<LivingCleaningTask>>

    @Update
    suspend fun updateLivingCleaningTask(task: LivingCleaningTask)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLivingCleaningTask(task: LivingCleaningTask): Long

    @Query("DELETE FROM living_cleaning_tasks WHERE id = :id")
    suspend fun deleteLivingCleaningTask(id: Long)

    @Query("SELECT * FROM plant_care_items ORDER BY daysUntilWatering ASC")
    fun getAllPlants(): Flow<List<PlantCareItem>>

    @Update
    suspend fun updatePlant(plant: PlantCareItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: PlantCareItem): Long

    @Query("SELECT * FROM guest_checklist_items ORDER BY isDone ASC, id ASC")
    fun getAllGuestChecklist(): Flow<List<GuestChecklistItem>>

    @Update
    suspend fun updateGuestChecklist(item: GuestChecklistItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuestChecklist(item: GuestChecklistItem): Long

    // --- BEDROOM ---
    @Query("SELECT * FROM wardrobe_items ORDER BY isDone ASC")
    fun getAllWardrobeItems(): Flow<List<WardrobeItem>>

    @Update
    suspend fun updateWardrobeItem(item: WardrobeItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWardrobeItem(item: WardrobeItem): Long

    @Query("SELECT * FROM personal_routine_items ORDER BY timeOfDay ASC")
    fun getAllPersonalRoutines(): Flow<List<PersonalRoutineItem>>

    @Update
    suspend fun updatePersonalRoutine(item: PersonalRoutineItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonalRoutine(item: PersonalRoutineItem): Long

    @Query("SELECT * FROM space_declutter_items ORDER BY isOrganized ASC")
    fun getAllDeclutterItems(): Flow<List<SpaceDeclutterItem>>

    @Update
    suspend fun updateDeclutterItem(item: SpaceDeclutterItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeclutterItem(item: SpaceDeclutterItem): Long

    // --- KIDS ROOM ---
    @Query("SELECT * FROM chore_items ORDER BY isCompleted ASC")
    fun getAllChores(): Flow<List<ChoreItem>>

    @Update
    suspend fun updateChore(item: ChoreItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChore(item: ChoreItem): Long

    @Query("SELECT * FROM study_tasks ORDER BY isFinished ASC")
    fun getAllStudyTasks(): Flow<List<StudyTask>>

    @Update
    suspend fun updateStudyTask(task: StudyTask)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyTask(task: StudyTask): Long

    @Query("SELECT * FROM kids_shopping_items ORDER BY isPurchased ASC")
    fun getAllKidsShopping(): Flow<List<KidsShoppingItem>>

    @Update
    suspend fun updateKidsShopping(item: KidsShoppingItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKidsShopping(item: KidsShoppingItem): Long

    // --- YARD & OUTDOORS ---
    @Query("SELECT * FROM garden_calendar_items ORDER BY isDone ASC")
    fun getAllGardenTasks(): Flow<List<GardenCalendarItem>>

    @Update
    suspend fun updateGardenTask(item: GardenCalendarItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGardenTask(item: GardenCalendarItem): Long

    @Query("SELECT * FROM maintenance_items ORDER BY isDone ASC")
    fun getAllMaintenanceItems(): Flow<List<MaintenanceItem>>

    @Update
    suspend fun updateMaintenanceItem(item: MaintenanceItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenanceItem(item: MaintenanceItem): Long

    @Query("SELECT * FROM tool_items ORDER BY category ASC")
    fun getAllTools(): Flow<List<ToolItem>>

    @Update
    suspend fun updateTool(tool: ToolItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTool(tool: ToolItem): Long
}
