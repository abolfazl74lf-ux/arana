package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Kitchen : Screen("kitchen")
    object LivingRoom : Screen("living_room")
    object Bedroom : Screen("bedroom")
    object KidsRoom : Screen("kids_room")
    object Yard : Screen("yard")
    object PersonalCare : Screen("personal_care")
    object DailyTimeline : Screen("daily_timeline")
}
