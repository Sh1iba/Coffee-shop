package com.example.coffeeshop.presentation.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coffeeshop.R
import com.example.coffeeshop.data.managers.PrefsManager
import com.example.coffeeshop.navigation.NavigationRoutes
import com.example.coffeeshop.presentation.theme.SoraFontFamily
import com.example.coffeeshop.presentation.theme.colorDarkOrange
import com.example.coffeeshop.presentation.viewmodel.ActiveOrderEvent
import com.example.coffeeshop.presentation.viewmodel.ActiveOrderState
import com.example.coffeeshop.presentation.viewmodel.ActiveOrderViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ActiveOrderScreen(
    navController: NavController,
    deliveryTimeMinutes: Float = 0.5F
) {
    val context = LocalContext.current
    val prefsManager = PrefsManager(context)

    val viewModel: ActiveOrderViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ActiveOrderViewModel(
                    prefsManager = prefsManager,
                    deliveryTimeMinutes = deliveryTimeMinutes
                ) as T
            }
        }
    )

    val state by viewModel.state.collectAsState()

    // Обработка событий навигации
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ActiveOrderEvent.NavigateToHome -> {
                    navController.navigate(NavigationRoutes.HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                else -> {}
            }
        }
    }

    ActiveOrderContent(
        state = state,
        onBackClick = {
            viewModel.onEvent(ActiveOrderEvent.NavigateToHome)
        }
    )
}

@Composable
fun ActiveOrderContent(
    state: ActiveOrderState,
    onBackClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val courierProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.map),
            contentDescription = "Фон с картой",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 280.dp)
                .background(Color.Transparent)
        ) {
            Image(
                painter = painterResource(id = R.drawable.deliver),
                contentDescription = "Точка отправления (A)",
                modifier = Modifier
                    .size(44.dp)
                    .align(Alignment.TopStart)
                    .offset(x = 50.dp, y = 100.dp),
                contentScale = ContentScale.Fit
            )

            Image(
                painter = painterResource(id = R.drawable.location),
                contentDescription = "Точка назначения (B)",
                modifier = Modifier
                    .size(44.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = (-50).dp, y = (-100).dp),
                contentScale = ContentScale.Fit
            )

            if (!state.isOrderDelivered) {
                Image(
                    painter = painterResource(R.drawable.cart),
                    contentDescription = "Курьер в пути",
                    modifier = Modifier
                        .size(50.dp)
                        .offset(
                            x = 50.dp + (courierProgress * 250f).dp,
                            y = 100.dp + (courierProgress * 300f).dp
                        )
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.cart),
                    contentDescription = "Курьер на месте",
                    modifier = Modifier
                        .size(44.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = (-50).dp, y = (-100).dp)
                )
            }
        }

        OrderStatusPanel(
            state = state,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        BackButton(
            onBackClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 68.dp, start = 16.dp)
        )
    }
}

@Composable
fun OrderStatusPanel(
    state: ActiveOrderState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (!state.isOrderDelivered) "Заказ в пути" else "Заказ прибыл!",
                fontSize = 22.sp,
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.W600,
                color = Color.Black
            )

            Spacer(Modifier.height(12.dp))

            if (!state.isOrderDelivered) {
                Text(
                    text = if (state.timeLeft < 60) {
                        "Доставка через ${state.timeLeft} сек"
                    } else {
                        "Доставка через ${state.minutes} мин ${state.seconds} сек"
                    },
                    fontSize = 16.sp,
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W500,
                    color = colorDarkOrange
                )

                Spacer(Modifier.height(24.dp))

                DeliveryProgress(progress = state.progress)
            } else {
                OrderDeliveredMessage()
            }

            Spacer(Modifier.height(32.dp))

            if (state.isOrderDelivered) {
                PickupButton(onClick = { })
            }
        }
    }
}

@Composable
fun DeliveryProgress(progress: Float) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Статус доставки:",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(10.dp)),
            color = colorDarkOrange,
            trackColor = Color(0xFFEAEAEA)
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DeliveryStage(
                text = "Заказ принят",
                isActive = progress > 0f
            )
            DeliveryStage(
                text = "В пути",
                isActive = progress > 0.3f
            )
            DeliveryStage(
                text = "Доставлен",
                isActive = progress >= 1f
            )
        }
    }
}

@Composable
fun DeliveryStage(
    text: String,
    isActive: Boolean
) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = if (isActive) colorDarkOrange else Color.Gray,
        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
    )
}

@Composable
fun OrderDeliveredMessage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ваш заказ ждет вас!",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Курьер уже на месте",
            fontSize = 14.sp,
            color = Color(0xFF4CAF50),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PickupButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorDarkOrange
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = "Забрать заказ",
            fontSize = 18.sp,
            fontWeight = FontWeight.W600
        )
    }
}

@Composable
fun BackButton(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(44.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.leftarrow),
                contentDescription = "Назад",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun ActiveOrderScreenPreview() {
    ActiveOrderContent(
        state = ActiveOrderState(
            timeLeft = 30,
            minutes = 0,
            seconds = 30,
            progress = 0.5f,
            isOrderDelivered = false
        ),
        onBackClick = {}
    )
}
