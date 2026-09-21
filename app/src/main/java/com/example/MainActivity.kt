package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.domain.model.RoomType
import com.example.ui.home.HomeOverviewScreen
import com.example.ui.home.HomeViewModel
import com.example.ui.navigation.Screen
import com.example.ui.rooms.BedroomScreen
import com.example.ui.rooms.KidsRoomScreen
import com.example.ui.rooms.KitchenScreen
import com.example.ui.rooms.LivingRoomScreen
import com.example.ui.rooms.YardScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeManagementApp()
                }
            }
        }
    }
}

@Composable
fun HomeManagementApp(
    viewModel: HomeViewModel = viewModel()
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(320)) + fadeIn(tween(320))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(320)) + fadeOut(tween(320))
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(320)) + fadeIn(tween(320))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(320)) + fadeOut(tween(320))
        }
    ) {
        composable(Screen.Home.route) {
            HomeOverviewScreen(
                viewModel = viewModel,
                onNavigateToRoom = { room ->
                    when (room) {
                        RoomType.KITCHEN -> navController.navigate(Screen.Kitchen.route)
                        RoomType.LIVING_ROOM -> navController.navigate(Screen.LivingRoom.route)
                        RoomType.BEDROOM -> navController.navigate(Screen.Bedroom.route)
                        RoomType.KIDS_ROOM -> navController.navigate(Screen.KidsRoom.route)
                        RoomType.YARD -> navController.navigate(Screen.Yard.route)
                    }
                }
            )
        }

        composable(Screen.Kitchen.route) {
            KitchenScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.LivingRoom.route) {
            LivingRoomScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Bedroom.route) {
            BedroomScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.KidsRoom.route) {
            KidsRoomScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Yard.route) {
            YardScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

