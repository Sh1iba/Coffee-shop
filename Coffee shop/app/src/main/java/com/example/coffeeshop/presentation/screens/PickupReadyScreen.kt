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
import androidx.navigation.NavController
import com.example.coffeeshop.R
import com.example.coffeeshop.navigation.NavigationRoutes
import com.example.coffeeshop.presentation.theme.SoraFontFamily
import com.example.coffeeshop.presentation.theme.colorDarkOrange
import kotlinx.coroutines.delay

@Composable
fun PickupReadyScreen(
    navController: NavController,
    preparationTimeMinutes: Float = 0.5f
) {
    val totalSeconds = (preparationTimeMinutes * 60).toInt()
    var timeLeft by remember { mutableStateOf(totalSeconds) }
    var isOrderReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        isOrderReady = true
    }

    val progress = 1f - (timeLeft.toFloat() / totalSeconds.toFloat())
    val minutes = timeLeft / 60
    val seconds = timeLeft % 60

    Box(
        modifier = Modifier
            .fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.coffee_back),
            contentDescription = "Фон с кофе",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 68.dp, start = 24.dp)
        ) {
            IconButton(
                onClick = {
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


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
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
            Spacer(modifier = Modifier.height(44.dp))
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
                if (!isOrderReady) {
                    Text(
                        text = "Заказ готовится",
                        fontSize = 22.sp,
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W600,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (totalSeconds < 60) {
                            "Готовность через ${timeLeft} сек"
                        } else {
                            "Готовность через ${minutes} мин ${seconds} сек"
                        },
                        fontSize = 16.sp,
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W500,
                        color = colorDarkOrange
                    )

                    Spacer(modifier = Modifier.height(24.dp))

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
                            Text(
                                text = "Заказ принят",
                                fontSize = 12.sp,
                                color = if (progress > 0f) colorDarkOrange else Color.Gray,
                                fontWeight = if (progress > 0f) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = "Готовится",
                                fontSize = 12.sp,
                                color = if (progress > 0.3f) colorDarkOrange else Color.Gray,
                                fontWeight = if (progress > 0.3f) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = "Готово",
                                fontSize = 12.sp,
                                color = if (progress >= 1f) colorDarkOrange else Color.Gray,
                                fontWeight = if (progress >= 1f) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                } else {
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
                            text = "Номер заказа: #${(1000..9999).random()}",
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

                Spacer(modifier = Modifier.height(32.dp))

                if (isOrderReady) {
                    Button(
                        onClick = {
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
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun PickupReadyScreenPreview() {
    PickupReadyScreen(navController = androidx.navigation.compose.rememberNavController())
}