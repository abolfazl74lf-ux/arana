package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chore_items")
data class ChoreItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String, // جمع کردن لگوها، مرتب کردن تخت، آب دادن به گل کوچک
    val childName: String = "آرتین",
    val rewardPoints: Int = 15,
    val isCompleted: Boolean = false,
    val emoji: String = "⭐"
)

@Entity(tableName = "study_tasks")
data class StudyTask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String, // ریاضی، علوم، نقاشی
    val taskDescription: String, // حل تمرین صفحه ۴۲، پروژه آتشفشان
    val dueDate: String = "فردا ساعت ۱۸",
    val isFinished: Boolean = false
)

@Entity(tableName = "kids_shopping_items")
data class KidsShoppingItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemName: String, // مدادرنگی ۲۴ رنگ، دفتر نقاشی، مسواک کودک
    val category: String = "لوازم‌التحریر", // لوازم‌التحریر، پوشاک، بهداشتی
    val childName: String = "آرتین",
    val isPurchased: Boolean = false
)
