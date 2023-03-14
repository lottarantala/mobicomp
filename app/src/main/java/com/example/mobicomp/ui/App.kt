package com.example.mobicomp.ui

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mobicomp.AppState
import com.example.mobicomp.rememberAppState
import com.example.mobicomp.ui.home.Home
import com.example.mobicomp.ui.login.LoginScreen
import com.example.mobicomp.ui.profile.Profile
import com.example.mobicomp.ui.reminder.ReminderScreen

@Composable
fun App(
    appState: AppState = rememberAppState(),
    sharedPreferences: SharedPreferences
) {
    val context = LocalContext.current
    NavHost(
        navController = appState.navController,
        startDestination = "login"
    ) {
        composable(route = "login") {
            LoginScreen(navController = appState.navController, prefs = sharedPreferences, context)
        }
        composable(route = "home") {
            Home(navController = appState.navController)
        }
        composable(route = "reminder") {
            ReminderScreen(navController = appState.navController)
        }
        composable(route = "profile") {
            Profile(sharedPreferences, navController = appState.navController)
        }
    }
}