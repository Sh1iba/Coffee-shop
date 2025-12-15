package com.example.coffeeshop

import CartScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.coffeeshop.data.managers.PrefsManager
import com.example.coffeeshop.data.remote.response.CoffeeCartResponse
import com.example.coffeeshop.data.remote.response.CoffeeResponse
import com.example.coffeeshop.data.remote.response.CoffeeSizeResponse
import com.example.coffeeshop.data.remote.response.CoffeeTypeResponse
import com.example.coffeeshop.navigation.NavigationRoutes
import com.example.coffeeshop.presentation.screens.*
import com.example.coffeeshop.presentation.screens.favorite.FavoriteCoffeeScreen
import com.example.coffeeshop.presentation.theme.CoffeeShopTheme
import com.example.coffeeshop.presentation.ui.screens.OnboardingScreen
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
            // Ключевое изменение: создаем состояние темы
            var darkThemeEnabled by remember {
                mutableStateOf(prefsManager.getBoolean(PrefsManager.KEY_DARK_MODE, false))
            }

            // Оборачиваем ВСЕ приложение в тему с этим состоянием
            CoffeeShopTheme(darkTheme = darkThemeEnabled) {
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                        if (currentRoute in listOf(
                                NavigationRoutes.HOME,
                                NavigationRoutes.FAVORITE,
                                NavigationRoutes.CART,
                                NavigationRoutes.SETTINGS
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
                            route = "${NavigationRoutes.DETAIL}/{coffeeId}/{coffeeName}/{coffeeType}/{coffeeDescription}/{imageName}?sizes={sizes}&favoriteSize={favoriteSize}",
                            arguments = listOf(
                                navArgument("coffeeId") { type = NavType.IntType },
                                navArgument("coffeeName") { type = NavType.StringType },
                                navArgument("coffeeType") { type = NavType.StringType },
                                navArgument("coffeeDescription") { type = NavType.StringType },
                                navArgument("imageName") { type = NavType.StringType },
                                navArgument("sizes") {
                                    type = NavType.StringType
                                    defaultValue = ""
                                },
                                navArgument("favoriteSize") {
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
                            val favoriteSize = backStackEntry.arguments?.getString("favoriteSize") ?: ""

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

                            CoffeeDetailScreen(
                                navController = navController,
                                coffee = coffee,
                                favoriteSize = favoriteSize
                            )
                        }
                        composable(NavigationRoutes.FAVORITE) {
                            FavoriteCoffeeScreen(navController)
                        }
                        composable(NavigationRoutes.CART) {
                            CartScreen(navController)
                        }
                        composable(
                            route = "${NavigationRoutes.ORDER}?selectedItems={selectedItems}&totalPrice={totalPrice}",
                            arguments = listOf(
                                navArgument("selectedItems") {
                                    type = NavType.StringType
                                    defaultValue = ""
                                },
                                navArgument("totalPrice") {
                                    type = NavType.FloatType
                                    defaultValue = 0f
                                }
                            )
                        ) { backStackEntry ->
                            val selectedItemsJson = backStackEntry.arguments?.getString("selectedItems") ?: ""
                            val totalPrice = backStackEntry.arguments?.getFloat("totalPrice") ?: 0f

                            val selectedItems = if (selectedItemsJson.isNotEmpty()) {
                                try {
                                    val decoded = URLDecoder.decode(selectedItemsJson, "UTF-8")
                                    val gson = Gson()
                                    val type = object : TypeToken<List<CoffeeCartResponse>>() {}.type
                                    gson.fromJson<List<CoffeeCartResponse>>(decoded, type) ?: emptyList()
                                } catch (e: Exception) {
                                    emptyList()
                                }
                            } else {
                                emptyList()
                            }

                            OrderScreen(
                                navController = navController,
                                selectedItems = selectedItems,
                                totalPrice = totalPrice.toDouble()
                            )
                        }
                        composable(NavigationRoutes.ACTIVE_ORDER) {
                            ActiveOrderScreen(navController = navController)
                        }
                        composable(NavigationRoutes.PICKUP_READY_ORDER) {
                            PickupReadyScreen(navController = navController)
                        }
                        composable(NavigationRoutes.SETTINGS) {
                            // Передаем функцию обновления темы в SettingsScreen
                            SettingsScreen(
                                navController = navController,
                                onThemeChanged = { newTheme ->
                                    darkThemeEnabled = newTheme
                                    prefsManager.saveBoolean(PrefsManager.KEY_DARK_MODE, newTheme)
                                }
                            )
                        }
                        composable(NavigationRoutes.ORDER_HISTORY) {
                            OrderHistoryScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}