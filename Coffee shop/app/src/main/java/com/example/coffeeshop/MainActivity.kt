package com.example.coffeeshop

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
import com.example.coffeeshop.data.remote.response.CartItemResponse
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.example.coffeeshop.data.remote.response.ProductResponse
import com.example.coffeeshop.data.remote.response.ProductVariantResponse
import com.example.coffeeshop.data.remote.response.ProductCategoryResponse
import com.example.coffeeshop.navigation.NavigationRoutes
import com.example.coffeeshop.presentation.screens.*
import com.example.coffeeshop.presentation.screens.favorite.FavoriteCoffeeScreen
import com.example.coffeeshop.presentation.theme.CoffeeShopTheme
import com.example.coffeeshop.presentation.ui.screens.OnboardingScreen
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.URLDecoder

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var prefsManager: PrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val startDestination = if (prefsManager.isFirstLaunch()) {
            prefsManager.setFirstLaunchCompleted()
            NavigationRoutes.ONBOARDING
        } else if (prefsManager.isLoggedIn()) {
            NavigationRoutes.HOME
        } else {
            NavigationRoutes.SIGN_IN
        }

        setContent {
            var darkThemeEnabled by remember {
                mutableStateOf(prefsManager.getBoolean(PrefsManager.KEY_DARK_MODE, false))
            }

            CoffeeShopTheme(darkTheme = darkThemeEnabled) {
                val navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                        if (currentRoute in listOf(
                                NavigationRoutes.HOME,
                                NavigationRoutes.FAVORITE,
                                NavigationRoutes.CART,
                                NavigationRoutes.ORDER_HISTORY,
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
                            route = "${NavigationRoutes.DETAIL}/{coffeeId}/{coffeeName}/{coffeeType}/{coffeeDescription}/{imageName}?sizes={sizes}&favoriteSize={favoriteSize}&sellerId={sellerId}",
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
                                },
                                navArgument("sellerId") {
                                    type = NavType.LongType
                                    defaultValue = -1L
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
                            val sellerId = backStackEntry.arguments?.getLong("sellerId") ?: -1L

                            val sizes = if (sizesEncoded.isEmpty()) {
                                emptyList()
                            } else {
                                try {
                                    val decoded = URLDecoder.decode(sizesEncoded, "UTF-8")
                                    decoded.split(",").mapNotNull { sizePrice ->
                                        val parts = sizePrice.split(":")
                                        if (parts.size == 2) {
                                            ProductVariantResponse(
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

                            val coffee = ProductResponse(
                                id = coffeeId,
                                type = ProductCategoryResponse(0, coffeeType),
                                name = coffeeName,
                                description = coffeeDescription,
                                sizes = sizes,
                                imageName = imageName,
                                sellerId = sellerId.takeIf { it > 0 }
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
                                    val type = object : TypeToken<List<CartItemResponse>>() {}.type
                                    gson.fromJson<List<CartItemResponse>>(decoded, type) ?: emptyList()
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
                        composable(
                            route = "${NavigationRoutes.ACTIVE_ORDER}/{orderId}",
                            arguments = listOf(
                                navArgument("orderId") { type = NavType.LongType; defaultValue = 0L }
                            )
                        ) {
                            ActiveOrderScreen(navController = navController)
                        }
                        composable(NavigationRoutes.PICKUP_READY_ORDER) {
                            PickupReadyScreen(navController = navController)
                        }
                        composable(NavigationRoutes.SETTINGS) {
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
                        composable(NavigationRoutes.SELLER_DASHBOARD) {
                            SellerDashboardScreen(navController = navController)
                        }
                        composable(
                            route = "${NavigationRoutes.SELLER_STORE}/{sellerId}",
                            arguments = listOf(
                                navArgument("sellerId") { type = NavType.LongType }
                            )
                        ) { backStackEntry ->
                            val sellerId = backStackEntry.arguments?.getLong("sellerId") ?: return@composable
                            SellerStoreScreen(navController = navController, sellerId = sellerId)
                        }
                    }
                }
            }
        }
    }
}