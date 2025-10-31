package com.example.coffeeshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.coffeeshop.navigation.NavigationRoutes
import com.example.coffeeshop.screens.HomeScreen
import com.example.coffeeshop.screens.OnBoardingScreen
import com.example.coffeeshop.screens.RegistrationScreen
import com.example.coffeeshop.screens.SignInScreen
import com.example.coffeeshop.ui.theme.CoffeeShopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoffeeShopTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = NavigationRoutes.HOME
                ) {
                    composable(NavigationRoutes.ONBOARDING) {
                        OnBoardingScreen(navController)
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