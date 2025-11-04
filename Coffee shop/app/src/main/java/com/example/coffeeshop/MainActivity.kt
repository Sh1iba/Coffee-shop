package com.example.coffeeshop

import FavoriteCoffeeScreen
import com.example.coffeeshop.data.managers.PrefsManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.coffeeshop.data.remote.response.CoffeeResponse
import com.example.coffeeshop.data.remote.response.CoffeeTypeResponse
import com.example.coffeeshop.navigation.NavigationRoutes
import com.example.coffeeshop.presentation.screens.CoffeeDetailScreen
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
                    composable(
                        route = "${NavigationRoutes.DETAIL}/{coffeeId}/{coffeeName}/{coffeeType}/{coffeePrice}/{coffeeDescription}/{imageName}",
                        arguments = listOf(
                            navArgument("coffeeId") { type = NavType.IntType },
                            navArgument("coffeeName") { type = NavType.StringType },
                            navArgument("coffeeType") { type = NavType.StringType },
                            navArgument("coffeePrice") { type = NavType.FloatType },
                            navArgument("coffeeDescription") { type = NavType.StringType },
                            navArgument("imageName") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val coffeeId = backStackEntry.arguments?.getInt("coffeeId") ?: 0
                        val coffeeName = backStackEntry.arguments?.getString("coffeeName") ?: ""
                        val coffeeType = backStackEntry.arguments?.getString("coffeeType") ?: ""
                        val coffeePrice = backStackEntry.arguments?.getFloat("coffeePrice") ?: 0f
                        val coffeeDescription = backStackEntry.arguments?.getString("coffeeDescription") ?: ""
                        val imageName = backStackEntry.arguments?.getString("imageName") ?: ""

                        val coffee = CoffeeResponse(
                            id = coffeeId,
                            type = CoffeeTypeResponse(0, coffeeType),
                            name = coffeeName,
                            description = coffeeDescription,
                            price = coffeePrice,
                            imageName = imageName
                        )

                        CoffeeDetailScreen(navController = navController, coffee = coffee)
                    }
                    composable(NavigationRoutes.FAVORITE) {
                        FavoriteCoffeeScreen(navController)
                    }
                }
            }
        }
    }
}