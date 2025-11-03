package com.example.coffeeshop.presentation.screens

import android.R.attr.top
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.R
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.coffeeshop.data.remote.response.CoffeeResponse
import com.example.coffeeshop.data.remote.response.CoffeeTypeResponse
import com.example.coffeeshop.presentation.theme.SoraFontFamily
import com.example.coffeeshop.presentation.theme.colorFoundationGrey
import com.example.coffeeshop.presentation.viewmodel.CoffeeDetailViewModel

@Composable
fun CoffeeDetailScreen(
    coffee: CoffeeResponse,
    onBackClick: () -> Unit,
    viewModel: CoffeeDetailViewModel? = null
) {

    val localIsFavorite = remember { mutableStateOf(false) }
    val isFavorite = if (viewModel != null) {
        viewModel.isFavorite.collectAsState().value
    } else {
        localIsFavorite.value
    }

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
                    text = "Описание",
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
                    Text(
                        text = "Coffee Image",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp)
        ) {
            Text(
                text = coffee.name,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = coffee.type.type,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = coffee.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "$${"%.2f".format(coffee.price)}",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Preview с избранным
@Preview(showBackground = true, name = "With Favorite")
@Composable
fun CoffeeDetailScreenWithFavoritePreview() {
    MaterialTheme {
        CoffeeDetailScreen(
            coffee = CoffeeResponse(
                id = 2,
                type = CoffeeTypeResponse(2, "Latte"),
                name = "Caramel Latte",
                description = "Sweet caramel coffee with smooth milk and rich espresso. Perfect balance of sweetness and coffee bitterness. Made with our signature Arabica beans and fresh milk.",
                price = 5.25f,
                imageName = "latte.jpg"
            ),
            onBackClick = {}
        )
    }
}

// Дополнительный preview для тестирования
@Preview(showBackground = true, name = "Dark Theme")
@Composable
fun CoffeeDetailScreenDarkPreview() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        CoffeeDetailScreen(
            coffee = CoffeeResponse(
                id = 1,
                type = CoffeeTypeResponse(1, "Cappuccino"),
                name = "Classic Cappuccino",
                description = "Traditional Italian coffee drink prepared with espresso, hot milk, and steamed milk foam.",
                price = 4.50f,
                imageName = "cappuccino.jpg"
            ),
            onBackClick = {}
        )
    }
}
