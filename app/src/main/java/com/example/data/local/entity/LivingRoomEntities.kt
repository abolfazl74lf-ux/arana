package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "living_cleaning_tasks")
data class LivingCleaningTask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String, // جاروبرقی، تی کشیدن، گردگیری مبلمان
    val frequency: String, // روزانه، هفتگی
    val isDone: Boolean = false,
    val estimatedMinutes: Int = 15
)

@Entity(tableName = "plant_care_items")
data class PlantCareItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plantName: String, // سانسوریا، مونسترا، پتوس
    val location: String = "کنار پنجره جنوبی",
    val waterIntervalDays: Int = 5,
    val daysUntilWatering: Int = 2,
    val needsFertilizer: Boolean = false,
    val needsLeafCleaning: Boolean = false,
    val sunlightLevel: String = "نور غیرمستقیم"
)

@Entity(tableName = "guest_checklist_items")
data class GuestChecklistItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskTitle: String, // آماده‌سازی حوله‌های تمیز، میوه و پذیرایی، مرتب‌سازی ورودی
    val category: String = "پذیرایی", // تدارکات، نظافت، پذیرایی
    val isDone: Boolean = false
)
