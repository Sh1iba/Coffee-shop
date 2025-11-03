package com.example.coffeeshop

import com.example.coffeeshop.data.managers.PrefsManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.coffeeshop.navigation.NavigationRoutes
import com.example.coffeeshop.presentation.theme.CoffeeShopTheme
import com.example.coffeeshop.presentation.ui.screens.OnboardingScreen
import com.example.coffeeshop.presentation.screens.HomeScreen
import com.example.coffeeshop.presentation.screens.RegistrationScreen
import com.example.coffeeshop.presentation.screens.SignInScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefsManager = PrefsManager(this)

        val startDestination = if (prefsManager.isFirstLaunch()) {
            prefsManager.setFirstLaunchCompleted()
            NavigationRoutes.ONBOARDING
        } else if (prefsManager.isLoggedIn()) {
            NavigationRoutes.HOME
        } else {
            NavigationRoutes.SIGN_IN
        }

        setContent {
            CoffeeShopTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable(NavigationRoutes.ONBOARDING) {
                        OnboardingScreen(navController)
                    }
                    composable(NavigationRoutes.REGISTRATION) {
                        RegistrationScreen(navController)
                    }
                    composable(NavigationRoutes.HOME) {
                        HomeScreen(navController)
                    }
                    composable(NavigationRoutes.SIGN_IN) {
                        SignInScreen(navController)
                    }
                }
            }
        }
    }
}