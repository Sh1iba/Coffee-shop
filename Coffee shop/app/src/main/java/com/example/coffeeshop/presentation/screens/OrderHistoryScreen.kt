package com.example.coffeeshop.presentation.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coffeeshop.R
import com.example.coffeeshop.data.managers.PrefsManager
import com.example.coffeeshop.data.remote.api.ApiClient
import com.example.coffeeshop.data.remote.response.OrderItemResponse
import com.example.coffeeshop.data.remote.response.OrderResponse
import com.example.coffeeshop.data.repository.CoffeeRepository
import com.example.coffeeshop.navigation.NavigationRoutes
import com.example.coffeeshop.presentation.screens.orderhistory.OrderHistoryViewModel
import com.example.coffeeshop.presentation.screens.orderhistory.OrderHistoryViewModelFactory
import com.example.coffeeshop.presentation.theme.*
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(navController: NavController) {
    val context = LocalContext.current
    val prefsManager = remember { PrefsManager(context) }

    val viewModel: OrderHistoryViewModel = viewModel(
        factory = OrderHistoryViewModelFactory(
            repository = CoffeeRepository(ApiClient.coffeeApi),
            prefsManager = prefsManager
        )
    )

    val orders by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadOrderHistory()
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 68.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
                        .height(44.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { navController.navigateUp() },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.leftarrow),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp),
                        )
                    }

                    Text(
                        text = "История заказов",
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = 20.sp,
                        lineHeight = 30.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )

                    // Пустой элемент для симметрии (такой же размер как кнопка назад)
                    Box(
                        modifier = Modifier.size(44.dp)
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colorDarkOrange)
                    }
                }

                error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Ошибка загрузки",
                            fontFamily = SoraFontFamily,
                            fontWeight = FontWeight.W600,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error!!,
                            fontFamily = SoraFontFamily,
                            fontWeight = FontWeight.W400,
                            fontSize = 14.sp,
                            lineHeight = 24.sp,
                            color = colorLightGrey,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                viewModel.clearError()
                                viewModel.loadOrderHistory()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorDarkOrange
                            )
                        ) {
                            Text(
                                text = "Повторить",
                                fontFamily = SoraFontFamily,
                                fontWeight = FontWeight.W600,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                orders.isEmpty() -> {
                    EmptyOrderHistoryState(
                        navController = navController,
                        onNavigateToCatalog = {
                            navController.navigate(NavigationRoutes.HOME)
                        }
                    )
                }

                else -> {
                    OrderHistoryList(orders = orders)
                }
            }
        }
    }
}

@Composable
private fun OrderHistoryList(orders: List<OrderResponse>) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(orders) { order ->
            OrderCard(order = order)
        }
    }
}

@Composable
fun OrderCard(order: OrderResponse) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Заказ #${order.id}",
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = 20.sp,
                        lineHeight = 30.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = order.orderDate.format(dateFormatter),
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W400,
                        fontSize = 12.sp,
                        lineHeight = 14.4.sp,
                        color = colorLightGrey
                    )
                }

            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Адрес",
                    modifier = Modifier.size(16.dp),
                    tint = colorLightGrey
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = order.deliveryAddress,
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W500,
                    fontSize = 14.sp,
                    lineHeight = 24.sp,
                    color = colorLightGrey,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(
                thickness = 1.dp,
                color = colorLightGrey.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            order.items.forEachIndexed { index, item ->
                OrderItemRow(item = item)
                if (index < order.items.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                if (order.deliveryFee > BigDecimal.ZERO) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Доставка",
                            fontFamily = SoraFontFamily,
                            fontWeight = FontWeight.W400,
                            fontSize = 14.sp,
                            lineHeight = 24.sp,
                            color = colorLightGrey
                        )
                        Text(
                            text = currencyFormatter.format(order.deliveryFee),
                            fontFamily = SoraFontFamily,
                            fontWeight = FontWeight.W500,
                            fontSize = 14.sp,
                            lineHeight = 24.sp,
                            color = colorLightGrey
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Итого",
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = 18.sp,
                        lineHeight = 27.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = currencyFormatter.format(order.totalAmount),
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = 18.sp,
                        lineHeight = 27.sp,
                        color = colorDarkOrange
                    )
                }
            }
        }
    }
}

@Composable
fun OrderItemRow(item: OrderItemResponse) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Информация о товаре
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.coffeeName,
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row {
                Text(
                    text = "Размер: ${item.selectedSize}",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W400,
                    fontSize = 12.sp,
                    lineHeight = 14.4.sp,
                    color = colorLightGrey
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${item.quantity} шт.",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W400,
                    fontSize = 12.sp,
                    lineHeight = 14.4.sp,
                    color = colorLightGrey
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = currencyFormatter.format(item.totalPrice),
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${currencyFormatter.format(item.unitPrice)} × ${item.quantity}",
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 12.sp,
                lineHeight = 14.4.sp,
                color = colorLightGrey
            )
        }
    }
}

@Composable
fun EmptyOrderHistoryState(
    navController: NavController,
    onNavigateToCatalog: () -> Unit = { navController.popBackStack() }
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(shape = RoundedCornerShape(20.dp))
                .background(Color(0xFFEDE0D4).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "No Orders",
                modifier = Modifier.size(48.dp),
                tint = colorDarkOrange
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Заказов пока нет",
            fontFamily = SoraFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = 20.sp,
            lineHeight = 30.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Совершите свой первый заказ, и он появится здесь",
            fontFamily = SoraFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp,
            lineHeight = 24.sp,
            color = colorLightGrey,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNavigateToCatalog,
            modifier = Modifier
                .height(48.dp)
                .width(200.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorDarkOrange
            )
        ) {
            Text(
                text = "Перейти в каталог",
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = Color.White
            )
        }
    }
}