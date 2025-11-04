package com.example.coffeeshop.presentation.screens

import android.R.attr.top
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.coffeeshop.data.remote.response.CoffeeResponse
import com.example.coffeeshop.data.remote.response.CoffeeTypeResponse
import com.example.coffeeshop.presentation.theme.SoraFontFamily
import com.example.coffeeshop.presentation.theme.colorDarkOrange
import com.example.coffeeshop.presentation.theme.colorFoundationGrey
import com.example.coffeeshop.presentation.theme.colorLightGrey
import com.example.coffeeshop.presentation.theme.colorSelectOrange
import com.example.coffeeshop.presentation.viewmodel.CoffeeDetailViewModel

@Composable
fun CoffeeDetailScreen(
    navController: NavController,
    viewModel: CoffeeDetailViewModel? = null
) {
    val coffee = remember {
        CoffeeResponse(
            id = 2,
            type = CoffeeTypeResponse(2, "Latte"),
            name = "Caffe Mocha",
            description = "Sweet caramel coffee with smooth milk and rich espresso.",
            price = 5.25f,
            imageName = "mocha.jpg"
        )
    }

    val onBackClick = { navController.popBackStack() }

    val localIsFavorite = remember { mutableStateOf(false) }
    val isFavorite = if (viewModel != null) {
        viewModel.isFavorite.collectAsState().value
    } else {
        localIsFavorite.value
    }

    var selectedSize by remember { mutableStateOf("M") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Box(
            modifier = Modifier
                .padding(top = 68.dp)
                .wrapContentSize()
                .padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = com.example.coffeeshop.R.drawable.leftarrow),
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp),
                    )
                }

                Text(
                    text = "Подробно",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    lineHeight = 19.2.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            if (viewModel != null) {
                                viewModel.toggleFavorite()
                            } else {
                                localIsFavorite.value = !localIsFavorite.value
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isFavorite) {
                        Image(
                            painter = painterResource(id = com.example.coffeeshop.R.drawable.heart),
                            contentDescription = "Favorite",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(Color.Red)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = com.example.coffeeshop.R.drawable.heart),
                            contentDescription = "Favorite",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(
                                Color(0xFF242424)
                            )
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp)

        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(327f / 202f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Gray)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF727070)),
                    contentAlignment = Alignment.Center
                ) {
                }
            }
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .wrapContentHeight()
                .padding(top = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Caffe Mocha",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 20.sp,
                    lineHeight = 30.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "Ice/Hot",
                    fontWeight = FontWeight.W400,
                    fontSize = 12.sp,
                    lineHeight = 14.4.sp,
                    color = colorLightGrey
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        ) {
                            append("⭐ 4.8 ")
                        }
                        withStyle(
                            style = SpanStyle(
                                color = colorLightGrey,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W400
                            )
                        ) {
                            append("(150)")
                        }
                    },
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                    color = colorLightGrey
                )
            }
            Box(
                modifier = Modifier.height(85.dp),
                contentAlignment = Alignment.CenterEnd

            ) {
                Row(
                    modifier = Modifier
                        .wrapContentWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = painterResource(id = com.example.coffeeshop.R.drawable.fast_delivery),
                        contentDescription = "Image 1",
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    Image(
                        painter = painterResource(id = com.example.coffeeshop.R.drawable.quality_bean),
                        contentDescription = "Image 2",
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )

                    Image(
                        painter = painterResource(id = com.example.coffeeshop.R.drawable.extra_milk),
                        contentDescription = "Image 3",
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }

            }

        }

        Spacer(modifier = Modifier.height(5.dp))

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            color = colorLightGrey.copy(alpha = 0.3f),
            thickness = 1.dp
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 20.dp)
        ) {
            Text(
                text = "Описание",
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = coffee.description,
                fontSize = 14.sp,
                lineHeight = 24.sp,
                color = colorLightGrey
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Размер",
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SizeOption(
                    size = "S",
                    isSelected = selectedSize == "S",
                    onClick = { selectedSize = "S" }
                )

                SizeOption(
                    size = "M",
                    isSelected = selectedSize == "M",
                    onClick = { selectedSize = "M" }
                )

                SizeOption(
                    size = "L",
                    isSelected = selectedSize == "L",
                    onClick = { selectedSize = "L" }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        BottomOrderPanel(
            price = when (selectedSize) {
                "S" -> "$3.99"
                "M" -> "$4.99"
                "L" -> "$5.99"
                else -> "$4.99"
            },
            onBuyNowClick = {

            }
        )
    }
}

@Composable
fun SizeOption(
    size: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        colorSelectOrange
    } else {
        Color.Transparent
    }

    val textColor = if (isSelected) {
        colorDarkOrange
    } else {
        MaterialTheme.colorScheme.onBackground
    }

    val borderColor = if (isSelected) {
        colorDarkOrange
    } else {
        colorLightGrey.copy(alpha = 0.5f)
    }

    Box(
        modifier = Modifier
            .height(44.dp)
            .width(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = size,
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
                color = textColor
            )
        }
    }
}

@Composable
fun BottomOrderPanel(
    price: String,
    onBuyNowClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Цена",
                    fontSize = 14.sp,
                    lineHeight = 16.8.sp,
                    fontWeight = FontWeight.W400,
                    color = colorLightGrey
                )
                Text(
                    text = price,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,
                    color = colorDarkOrange,
                    fontFamily = SoraFontFamily
                )
            }

            Box(
                modifier = Modifier
                    .height(48.dp)
                    .width(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorDarkOrange)
                    .clickable { onBuyNowClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Купить сейчас",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W600,
                    color = Color.White,
                    fontFamily = SoraFontFamily
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "With Favorite")
@Composable
fun CoffeeDetailScreenWithFavoritePreview() {
    MaterialTheme {
        val navController = rememberNavController()
        CoffeeDetailScreen(
            navController = navController
        )
    }
}

