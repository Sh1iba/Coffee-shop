package com.example.coffeeshop.presentation.screens

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.coffeeshop.R
import com.example.coffeeshop.navigation.NavigationRoutes
import com.example.coffeeshop.presentation.theme.SoraFontFamily
import com.example.coffeeshop.presentation.theme.colorDarkOrange
import com.example.coffeeshop.presentation.viewmodel.PickupOrderEvent
import com.example.coffeeshop.presentation.viewmodel.PickupOrderState
import com.example.coffeeshop.presentation.viewmodel.PickupReadyViewModel
import kotlinx.coroutines.flow.collectLatest
import java.nio.file.WatchEvent

@Composable
fun PickupReadyScreen(
    navController: NavController,
    preparationTimeMinutes: Float = 0.5f
) {
    val viewModel: PickupReadyViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PickupReadyViewModel(
                    preparationTimeMinutes = preparationTimeMinutes
                ) as T
            }
        }
    )

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is PickupOrderEvent.NavigateToHome -> {
                    navController.navigate(NavigationRoutes.HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                else -> {}
            }
        }
    }

    PickupReadyContent(
        state = state,
        onBackClick = {
            viewModel.onEvent(PickupOrderEvent.NavigateToHome)
        },
        onPickupClick = {
            viewModel.onEvent(PickupOrderEvent.NavigateToHome)
        }
    )
}

@Composable
fun PickupReadyContent(
    state: PickupOrderState,
    onBackClick: () -> Unit,
    onPickupClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage()

        BackButton(
            onBackClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 68.dp, start = 24.dp)
        )

        CoffeeImage(isOrderReady = state.isOrderReady)

        OrderStatusPanel(
            state = state,
            onPickupClick = onPickupClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun BackgroundImage() {
    Image(
        painter = painterResource(id = R.drawable.coffee_back),
        contentDescription = "Фон с кофе",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillBounds
    )
}

@Composable
fun CoffeeImage(isOrderReady: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight()
            .padding(bottom = 90.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(
                    id = R.drawable.coffee_mascot
                ),
                contentDescription = if (!isOrderReady) "Кофе готовится" else "Готово",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun OrderStatusPanel(
    state: PickupOrderState,
    onPickupClick: () -> Unit,
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
            if (!state.isOrderReady) {
                PreparingOrderContent(state = state)
            } else {
                OrderReadyContent(state = state)
            }

            Spacer(modifier = Modifier.height(32.dp))

            OrderActionButton(
                isOrderReady = state.isOrderReady,
                onPickupClick = onPickupClick
            )
        }
    }
}

@Composable
fun PreparingOrderContent(state: PickupOrderState) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Заказ готовится",
            fontSize = 22.sp,
            fontFamily = SoraFontFamily,
            fontWeight = FontWeight.W600,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (state.timeLeft < 60) {
                "Готовность через ${state.timeLeft} сек"
            } else {
                "Готовность через ${state.minutes} мин ${state.seconds} сек"
            },
            fontSize = 16.sp,
            fontFamily = SoraFontFamily,
            fontWeight = FontWeight.W500,
            color = colorDarkOrange
        )

        Spacer(modifier = Modifier.height(24.dp))

        PreparationProgress(progress = state.progress)
    }
}

@Composable
fun PreparationProgress(progress: Float) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Статус приготовления:",
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

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PreparationStage(
                text = "Заказ принят",
                isActive = progress > 0f
            )
            PreparationStage(
                text = "Готовится",
                isActive = progress > 0.3f
            )
            PreparationStage(
                text = "Готово",
                isActive = progress >= 1f
            )
        }
    }
}

@Composable
fun PreparationStage(
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
fun OrderReadyContent(state: PickupOrderState) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Заказ готов!",
            fontSize = 22.sp,
            fontFamily = SoraFontFamily,
            fontWeight = FontWeight.W600,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Номер заказа: ${state.orderNumber}",
                fontSize = 16.sp,
                fontWeight = FontWeight.W500,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Адрес кофейни: ул. Кофейная, д. 15",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Стойка выдачи заказов",
                fontSize = 14.sp,
                color = colorDarkOrange,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

@Composable
fun OrderActionButton(
    isOrderReady: Boolean,
    onPickupClick: () -> Unit
) {
    if (isOrderReady) {
        Button(
            onClick = onPickupClick,
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
    } else {
        Button(
            onClick = {},
            enabled = false,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE0E0E0),
                disabledContainerColor = Color(0xFFE0E0E0)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Заказ готовится...",
                fontSize = 18.sp,
                fontWeight = FontWeight.W600,
                color = Color.Gray
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun PickupReadyScreenPreview() {
    PickupReadyContent(
        state = PickupOrderState(
            timeLeft = 30,
            minutes = 0,
            seconds = 30,
            progress = 0.5f,
            isOrderReady = false,
            orderNumber = "#1234"
        ),
        onBackClick = {},
        onPickupClick = {}
    )
}

