package com.example.coffeeshop.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val LightColorScheme = lightColorScheme(
//    // Основные цвета
//    primary = colorFoundationGrey,
//    onPrimary = colorLightWhite,
//    secondary = colorLightGrey,
//    onSecondary = colorLightWhite,
//    tertiary = colorDarkOrange,
//    onTertiary = colorLightWhite,

    //задний фон
    background = colorBackgroudWhite, //Color(0xfff9f9f9)

    //карточки и нижняя панель
    surface = colorLightWhite, // Color(0xFFFFFFFF)

    //текст жирный
    outline = colorBlackText, // Color(0xFF2F2D2C)

    //Текст серый
    outlineVariant = colorGrayText, //Color(0xFFa2a2a2)

    surfaceTint = colorLightWhiteRow, //Color(0xFFf5f5f5)

    primaryContainer = colorSelectOrange,// Color(0xFFf9f2ed)
//    onBackground = colorFoundationGrey,
//    surface = colorLightWhite,
//    onSurface = colorFoundationGrey,
//    surfaceVariant = colorLightRecGrey,
//    onSurfaceVariant = colorLightGrey,

//    // Контуры
//    outline = colorGreyWhite,
//    outlineVariant = colorGrey,
//
//    // Ошибки
//    error = Color(0xFFBA1A1A),
//    onError = colorLightWhite,
//
//    // Дополнительные
//    primaryContainer = colorSelectOrange,
//    onPrimaryContainer = colorFoundationGrey,
//    secondaryContainer = colorLightRecGrey,
//    onSecondaryContainer = colorFoundationGrey,
//    tertiaryContainer = colorDarkOrange,
//    onTertiaryContainer = colorLightWhite
)


private val DarkColorScheme = darkColorScheme(

    //задний фон
    background = colorBackgroudDark, //Color(0xFF141414)

    //карточки и нижняя панель
    surface = colorLightDark,//Color(0xFF222222)

    //текст жирный
    outline = colorWhiteText, //Color(0xFFe1e3e6)

    //Текст серый
    outlineVariant = colorGrayText, //Color(0xFFa2a2a2)

    surfaceTint = colorLightDark,//Color(0xFFf5f5f5)

    primaryContainer = colorSelectGray,

)

@Composable
fun CoffeeShopTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}