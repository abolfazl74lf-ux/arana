package com.example.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Deck
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class RoomType(
    val id: String,
    val titleFa: String,
    val titleEn: String,
    val descriptionFa: String,
    val icon: ImageVector,
    val primaryColor: Color,
    val secondaryColor: Color,
    val badgeSummary: String
) {
    KITCHEN(
        id = "kitchen",
        titleFa = "آشپزخانه",
        titleEn = "Kitchen",
        descriptionFa = "موجودی، لیست خرید هوشمند، برنامه غذایی و نظافت وسایل",
        icon = Icons.Default.Kitchen,
        primaryColor = Color(0xFFD97736),
        secondaryColor = Color(0xFFFFF0E5),
        badgeSummary = "یخچال و مواد غذایی"
    ),
    LIVING_ROOM(
        id = "living_room",
        titleFa = "پذیرایی",
        titleEn = "Living Room",
        descriptionFa = "تقویم نظافت، نگهداری گیاهان آپارتمانی و چک‌لیست مهمان",
        icon = Icons.Default.Weekend,
        primaryColor = Color(0xFF388E3C),
        secondaryColor = Color(0xFFE8F5E9),
        badgeSummary = "گیاهان و مهمانداری"
    ),
    BEDROOM(
        id = "bedroom",
        titleFa = "اتاق خواب",
        titleEn = "Bedroom",
        descriptionFa = "کمد و شستشوی لباس‌ها، روتین‌های شخصی و مدیریت فضا",
        icon = Icons.Default.Bed,
        primaryColor = Color(0xFF5E35B1),
        secondaryColor = Color(0xFFEDE7F6),
        badgeSummary = "کمد و روتین فردی"
    ),
    KIDS_ROOM(
        id = "kids_room",
        titleFa = "اتاق بچه‌ها",
        titleEn = "Kids' Room",
        descriptionFa = "چارت وظایف و امتیازدهی، ردیاب تحصیلی و خرید اختصاصی",
        icon = Icons.Default.ChildCare,
        primaryColor = Color(0xFFE65100),
        secondaryColor = Color(0xFFFFF3E0),
        badgeSummary = "چارت وظایف و مدرسه"
    ),
    YARD(
        id = "yard",
        titleFa = "حیاط و بیرون",
        titleEn = "Yard & Outdoors",
        descriptionFa = "تقویم باغبانی، چک‌لیست تعمیرات دوره‌ای و مدیریت ابزارها",
        icon = Icons.Default.Deck,
        primaryColor = Color(0xFF00796B),
        secondaryColor = Color(0xFFE0F2F1),
        badgeSummary = "باغبانی و نگهداری"
    )
}
