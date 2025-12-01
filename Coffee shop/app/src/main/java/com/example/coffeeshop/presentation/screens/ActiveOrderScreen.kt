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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.coffeeshop.R
import com.example.coffeeshop.navigation.NavigationRoutes
import com.example.coffeeshop.presentation.theme.SoraFontFamily
import com.example.coffeeshop.presentation.theme.colorDarkOrange
import kotlinx.coroutines.delay

@Composable
fun ActiveOrderScreen(
    navController: NavController,
    deliveryTimeMinutes: Float = 0.5F
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val prefsManager = com.example.coffeeshop.data.managers.PrefsManager(context)

    val totalSeconds = (deliveryTimeMinutes * 60).toInt()
    var timeLeft by remember { mutableStateOf(totalSeconds) }
    var isOrderDelivered by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition()
    val courierProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    LaunchedEffect(Unit) {
        prefsManager.saveLong("order_start_ts", System.currentTimeMillis())

        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        isOrderDelivered = true
    }

    val progress = 1f - (timeLeft.toFloat() / totalSeconds.toFloat())
    val minutes = timeLeft / 60
    val seconds = timeLeft % 60

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
                .background(Color(0x00000000))
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

            if (!isOrderDelivered) {
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

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (!isOrderDelivered) "Заказ в пути" else "Заказ прибыл!",
                    fontSize = 22.sp,
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    color = Color.Black
                )

                Spacer(Modifier.height(12.dp))

                if (!isOrderDelivered) {
                    Text(
                        text = if (totalSeconds < 60) {
                            "Доставка через ${timeLeft} сек"
                        } else {
                            "Доставка через ${minutes} мин ${seconds} сек"
                        },
                        fontSize = 16.sp,
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W500,
                        color = colorDarkOrange
                    )

                    Spacer(Modifier.height(24.dp))

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
                            Text(
                                text = "Заказ принят",
                                fontSize = 12.sp,
                                color = if (progress > 0f) colorDarkOrange else Color.Gray,
                                fontWeight = if (progress > 0f) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = "В пути",
                                fontSize = 12.sp,
                                color = if (progress > 0.3f) colorDarkOrange else Color.Gray,
                                fontWeight = if (progress > 0.3f) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = "Доставлен",
                                fontSize = 12.sp,
                                color = if (progress >= 1f) colorDarkOrange else Color.Gray,
                                fontWeight = if (progress >= 1f) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                } else {
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

                Spacer(Modifier.height(32.dp))

                if (isOrderDelivered) {
                    Button(
                        onClick = {
                            prefsManager.saveLong("order_start_ts", 0)
                            navController.navigate(NavigationRoutes.HOME) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
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
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 68.dp, start = 16.dp)
        ) {
            IconButton(
                onClick = {
                    prefsManager.saveLong("order_start_ts", 0)
                    navController.navigate(NavigationRoutes.HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                },
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
}