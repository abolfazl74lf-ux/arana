package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wardrobe_items")
data class WardrobeItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String, // پالتوها و کت‌های زمستانی، ملحفه‌ها و روبالشتی‌ها
    val season: String, // بهاره/تابستانه، پاییزه/زمستانه
    val careType: String, // خشکشویی، شستشو با ماشین، شستشوی ظریف
    val nextScheduleDate: String = "هفته آینده",
    val isDone: Boolean = false
)

@Entity(tableName = "personal_routine_items")
data class PersonalRoutineItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String, // کشش صبحگاهی ۱۰ دقیقه، روتین پوستی شب، مطالعه ۲۰ دقیقه
    val timeOfDay: String, // صبح، شب
    val streakDays: Int = 3,
    val isDoneToday: Boolean = false
)

@Entity(tableName = "space_declutter_items")
data class SpaceDeclutterItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val areaName: String, // کشوی جوراب‌ها، میز آرایش، کمد دیواری بالا
    val priority: String = "متوسط", // بالا، متوسط، پایین
    val isOrganized: Boolean = false,
    val reminderNotes: String = ""
)
