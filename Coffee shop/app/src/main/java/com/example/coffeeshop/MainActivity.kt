package com.example.coffeeshop

import CartScreen
import com.example.coffeeshop.data.managers.PrefsManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.coffeeshop.data.remote.response.CoffeeResponse
import com.example.coffeeshop.data.remote.response.CoffeeSizeResponse
import com.example.coffeeshop.data.remote.response.CoffeeTypeResponse
import com.example.coffeeshop.navigation.NavigationRoutes
import com.example.coffeeshop.presentation.screens.BottomMenu
import com.example.coffeeshop.presentation.screens.CoffeeDetailScreen
import com.example.coffeeshop.presentation.theme.CoffeeShopTheme
import com.example.coffeeshop.presentation.ui.screens.OnboardingScreen
import com.example.coffeeshop.presentation.screens.HomeScreen
import com.example.coffeeshop.presentation.screens.RegistrationScreen
import com.example.coffeeshop.presentation.screens.SignInScreen
import com.example.coffeeshop.presentation.screens.favorite.FavoriteCoffeeScreen
import java.net.URLDecoder


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

                Scaffold(
                    bottomBar = {
                        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                        if (currentRoute in listOf(
                                NavigationRoutes.HOME,
                                NavigationRoutes.FAVORITE,
                                NavigationRoutes.CART,
                                NavigationRoutes.PROFILE
                            )) {
                            BottomMenu(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
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
                            route = "${NavigationRoutes.DETAIL}/{coffeeId}/{coffeeName}/{coffeeType}/{coffeeDescription}/{imageName}?sizes={sizes}",
                            arguments = listOf(
                                navArgument("coffeeId") { type = NavType.IntType },
                                navArgument("coffeeName") { type = NavType.StringType },
                                navArgument("coffeeType") { type = NavType.StringType },
                                navArgument("coffeeDescription") { type = NavType.StringType },
                                navArgument("imageName") { type = NavType.StringType },
                                navArgument("sizes") {
                                    type = NavType.StringType
                                    defaultValue = ""
                                }
                            )
                        ) { backStackEntry ->
                            val coffeeId = backStackEntry.arguments?.getInt("coffeeId") ?: 0
                            val coffeeName = backStackEntry.arguments?.getString("coffeeName") ?: ""
                            val coffeeType = backStackEntry.arguments?.getString("coffeeType") ?: ""
                            val coffeeDescription = backStackEntry.arguments?.getString("coffeeDescription") ?: ""
                            val imageName = backStackEntry.arguments?.getString("imageName") ?: ""
                            val sizesEncoded = backStackEntry.arguments?.getString("sizes") ?: ""

                            val sizes = if (sizesEncoded.isEmpty()) {
                                emptyList()
                            } else {
                                try {
                                    val decoded = URLDecoder.decode(sizesEncoded, "UTF-8")
                                    decoded.split(",").mapNotNull { sizePrice ->
                                        val parts = sizePrice.split(":")
                                        if (parts.size == 2) {
                                            CoffeeSizeResponse(
                                                size = parts[0],
                                                price = parts[1].toFloatOrNull() ?: 0f
                                            )
                                        } else {
                                            null
                                        }
                                    }
                                } catch (e: Exception) {
                                    emptyList()
                                }
                            }

                            val coffee = CoffeeResponse(
                                id = coffeeId,
                                type = CoffeeTypeResponse(0, coffeeType),
                                name = coffeeName,
                                description = coffeeDescription,
                                sizes = sizes,
                                imageName = imageName
                            )

                            CoffeeDetailScreen(navController = navController, coffee = coffee)
                        }
                        composable(NavigationRoutes.FAVORITE) {
                            FavoriteCoffeeScreen(navController)
                        }
                        composable(NavigationRoutes.CART) {
                            CartScreen(navController)
                        }
                    }
                }
            }
        }
    }
}