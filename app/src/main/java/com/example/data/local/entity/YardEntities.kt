package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "garden_calendar_items")
data class GardenCalendarItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskTitle: String, // هرس درختان میوه، سمپاشی بهاره، کاشت بذر چمن
    val targetAreaOrPlant: String = "باغچه حیاط جلو",
    val seasonTag: String = "بهار",
    val scheduledDate: String = "اوایل اردیبهشت",
    val isDone: Boolean = false
)

@Entity(tableName = "maintenance_items")
data class MaintenanceItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String, // بررسی دوره‌ای ناودان‌ها، سرویس لامپ‌های حیاط، نقاشی نرده‌ها
    val frequencyLabel: String = "هر ۶ ماه",
    val statusText: String = "نیاز به بررسی",
    val isDone: Boolean = false
)

@Entity(tableName = "tool_items")
data class ToolItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val toolName: String, // قیچی باغبانی، دریل شارژی، اره برقی، شلنگ آبیاری
    val storageLocation: String, // طبقه دوم انبار، قفسه ابزار A، کمد حیاط
    val category: String = "باغبانی", // فنی، باغبانی، ایمنی
    val isAvailable: Boolean = true
)
