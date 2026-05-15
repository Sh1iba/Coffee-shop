package com.example.coffeeshop.presentation.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.coffeeshop.R
import com.example.coffeeshop.navigation.NavigationRoutes
import com.example.coffeeshop.presentation.theme.SoraFontFamily
import com.example.coffeeshop.presentation.theme.colorDarkOrange
import com.example.coffeeshop.presentation.viewmodel.ActiveOrderEvent
import com.example.coffeeshop.presentation.viewmodel.ActiveOrderState
import com.example.coffeeshop.presentation.viewmodel.ActiveOrderViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ActiveOrderScreen(navController: NavController) {
    val viewModel: ActiveOrderViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is ActiveOrderEvent.NavigateToHome -> {
                    navController.navigate(NavigationRoutes.HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    ActiveOrderContent(
        state = state,
        onBackClick = { viewModel.onEvent(ActiveOrderEvent.NavigateToHome) },
        onPickupClick = { viewModel.onEvent(ActiveOrderEvent.NavigateToHome) }
    )
}

@Composable
fun ActiveOrderContent(
    state: ActiveOrderState,
    onBackClick: () -> Unit,
    onPickupClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "courier")
    val courierProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "courierPos"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.map),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 280.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.deliver),
                contentDescription = null,
                modifier = Modifier
                    .size(44.dp)
                    .align(Alignment.TopStart)
                    .offset(x = 50.dp, y = 100.dp),
                contentScale = ContentScale.Fit
            )
            Image(
                painter = painterResource(id = R.drawable.location),
                contentDescription = null,
                modifier = Modifier
                    .size(44.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = (-50).dp, y = (-100).dp),
                contentScale = ContentScale.Fit
            )
            if (!state.isOrderDelivered) {
                Image(
                    painter = painterResource(R.drawable.cart),
                    contentDescription = null,
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
                    contentDescription = null,
                    modifier = Modifier
                        .size(44.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = (-50).dp, y = (-100).dp)
                )
            }
        }

        OrderStatusPanel(
            state = state,
            onPickupClick = onPickupClick,
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
    onPickupClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colorScheme.surface, RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = colorDarkOrange, modifier = Modifier.size(36.dp))
                Spacer(Modifier.height(12.dp))
                Text("Загружаем статус...", fontFamily = SoraFontFamily, fontSize = 14.sp, color = colorScheme.onSurfaceVariant)
            } else {
                Text(
                    text = state.statusLabel,
                    fontSize = 22.sp,
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    color = colorScheme.outline
                )

                Spacer(Modifier.height(12.dp))

                if (!state.isOrderDelivered && !state.isOrderCancelled) {
                    Text(
                        text = "Обновляется автоматически каждые 5 сек",
                        fontSize = 12.sp,
                        fontFamily = SoraFontFamily,
                        color = colorScheme.outlineVariant
                    )
                    Spacer(Modifier.height(16.dp))
                    DeliveryProgress(status = state.status, progress = state.progress)
                } else if (state.isOrderCancelled) {
                    Text(
                        text = "Свяжитесь с поддержкой, если это ошибка",
                        fontSize = 14.sp,
                        fontFamily = SoraFontFamily,
                        color = colorScheme.error
                    )
                } else {
                    OrderDeliveredMessage()
                }

                Spacer(Modifier.height(32.dp))

                ActiveOrderButton(
                    isOrderDelivered = state.isOrderDelivered || state.isOrderCancelled,
                    isCancelled = state.isOrderCancelled,
                    onPickupClick = onPickupClick
                )
            }
        }
    }
}

@Composable
fun DeliveryProgress(status: String, progress: Float) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        Text(
            text = "Статус доставки:",
            fontSize = 14.sp,
            color = colorScheme.outlineVariant,
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

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            DeliveryStage("Принят", status !in listOf(""))
            DeliveryStage("Готовится", status in listOf("COOKING", "READY_FOR_PICKUP", "DELIVERED"))
            DeliveryStage("Готов", status in listOf("READY_FOR_PICKUP", "DELIVERED"))
            DeliveryStage("Доставлен", status == "DELIVERED")
        }
    }
}

@Composable
fun DeliveryStage(text: String, isActive: Boolean) {
    Text(
        text = text,
        fontSize = 11.sp,
        color = if (isActive) colorDarkOrange else Color.Gray,
        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
    )
}

@Composable
fun OrderDeliveredMessage() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Ваш заказ ждёт вас!",
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
fun ActiveOrderButton(
    isOrderDelivered: Boolean,
    isCancelled: Boolean = false,
    onPickupClick: () -> Unit
) {
    Button(
        onClick = onPickupClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isCancelled) colorScheme.error else colorDarkOrange
        ),
        enabled = isOrderDelivered,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = when {
                isCancelled     -> "Заказ отменён — на главную"
                isOrderDelivered -> "Заказ получен!"
                else             -> "Доставка в процессе..."
            },
            fontSize = 17.sp,
            fontWeight = FontWeight.W600,
            fontFamily = SoraFontFamily,
            color = Color.White
        )
    }
}

@Composable
fun BackButton(onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(44.dp)
                .background(colorScheme.surface, RoundedCornerShape(12.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.leftarrow),
                contentDescription = "Назад",
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outline)
            )
        }
    }
}
