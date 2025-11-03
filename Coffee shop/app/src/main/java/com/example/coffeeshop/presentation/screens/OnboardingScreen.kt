package com.example.coffeeshop.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.coffeeshop.R
import com.example.coffeeshop.navigation.NavigationRoutes
import com.example.coffeeshop.presentation.theme.SoraFontFamily
import com.example.coffeeshop.presentation.theme.colorDarkOrange
import com.example.coffeeshop.presentation.theme.colorGrey

@Composable
fun OnboardingScreen(navController: NavController) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val imageMaxHeight = when {
        screenHeight < 600.dp -> 350.dp
        screenHeight > 800.dp -> 550.dp
        else -> 500.dp
    }

    val imageMinHeight = when {
        screenHeight < 600.dp -> 200.dp
        else -> 300.dp
    }

    val titleFontSize = when {
        screenHeight < 600.dp -> 24.sp
        screenHeight > 800.dp -> 36.sp
        else -> 32.sp
    }

    val titleLineHeight = when {
        screenHeight < 600.dp -> 32.sp
        screenHeight > 800.dp -> 54.sp
        else -> 48.sp
    }

    val buttonWidthFraction = when {
        screenWidth < 360.dp -> 0.95f
        else -> 0.9f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = imageMinHeight, max = imageMaxHeight)
        ) {
            Image(
                painter = painterResource(id = R.drawable.coffee),
                contentDescription = "Coffee Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(35.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Влюбитесь в кофе в блаженном восторге!",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = titleFontSize,
                    lineHeight = titleLineHeight,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Добро пожаловать в наш уютный уголок, где каждая чашка - это наслаждение для вас.",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = colorGrey,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = {
                    navController.navigate(NavigationRoutes.REGISTRATION) {
                        popUpTo(NavigationRoutes.ONBOARDING) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(buttonWidthFraction)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorDarkOrange
                )
            ) {
                Text(
                    text = "Начать",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "pre")
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen(navController = rememberNavController())
}