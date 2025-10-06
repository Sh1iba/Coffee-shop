package com.example.coffeeshop.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.coffeeshop.R
import com.example.coffeeshop.navigation.NavigationRoutes
import com.example.coffeeshop.ui.theme.SoraFontFamily
import com.example.coffeeshop.ui.theme.colorDarkOrange
import com.example.coffeeshop.ui.theme.colorGrey

@Composable
fun OnBoardingScreen(navController: NavController) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(536.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.coffee),
                contentDescription = "Coffee Image",
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .offset(y = 452.dp)
                .zIndex(1f),
                verticalArrangement = Arrangement.spacedBy(35.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(215.dp)
                    .padding(start = 24.dp,top = 24.dp, end = 24.dp)

            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Влюбитесь в кофе в блаженном восторге!",
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = 32.sp,
                        lineHeight = 48.sp,
                        color = Color.White,
                        letterSpacing = 0.005.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .width(300.dp)
                            .height(144.dp)

                    )

                    Text(
                        text = "Добро пожаловать в наш уютный уголок, где каждая чашка - это наслаждение для вас.",
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W400,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                        color = colorGrey,
                        letterSpacing = 0.01.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    )

                }
            }
            Button(
                onClick = {
                    navController.navigate(NavigationRoutes.REGISTRATION) {
                        popUpTo(NavigationRoutes.ONBOARDING) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .width(327.dp)
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
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = Color.White,
                    modifier = Modifier
                        .width(116.dp)
                        .height(24.dp)
                )
            }
        }
    }

}

@Preview(showBackground = true, showSystemUi = true, name = "pre")
@Composable
fun OnboardingScreenPreview() {
    OnBoardingScreen(navController = rememberNavController())
}