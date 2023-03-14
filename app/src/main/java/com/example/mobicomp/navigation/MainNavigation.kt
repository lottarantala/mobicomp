package com.example.mobicomp.navigation

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mobicomp.ui.home.Home
import com.example.mobicomp.ui.login.LoginScreen
import com.example.mobicomp.ui.maps.ReminderLocation
import com.example.mobicomp.ui.profile.Profile
import com.example.mobicomp.ui.reminder.ReminderEditScreen
import com.example.mobicomp.ui.reminder.ReminderScreen

@Composable
fun MainNavigation(
    sharedPreferences: SharedPreferences
) {
    val context = LocalContext.current
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable(route = "login") {
            LoginScreen(navController = navController, prefs = sharedPreferences, context)
        }
        composable(route = "home") {
            Home(navController = navController)
        }
        composable(route = "reminder") {
            ReminderScreen(navController = navController)
        }
        composable(route = "profile") {
            Profile(sharedPreferences, navController = navController)
        }
        composable(
            route = "editReminder/{reminderId}/{message}/{reminderTime}",
            arguments = listOf(
                navArgument("reminderId") { type = NavType.LongType },
                navArgument("message") { type = NavType.StringType },
                navArgument("reminderTime") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val reminderId = backStackEntry.arguments?.getLong("reminderId")
            val message = backStackEntry.arguments?.getString("message") ?: ""
            if (reminderId != null) {
                ReminderEditScreen(
                    navController = navController,
                    reminderId,
                    message
                )
            }
        }
        composable(route = "map") {
            ReminderLocation(navController = navController)
        }
    }
}