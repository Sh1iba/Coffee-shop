package com.example.coffeeshop.presentation.screens.favorite

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import java.net.URLEncoder
import com.example.coffeeshop.R
import com.example.coffeeshop.data.remote.response.ProductResponse
import com.example.coffeeshop.navigation.NavigationRoutes
import com.example.coffeeshop.presentation.theme.CoffeeShopTheme
import com.example.coffeeshop.presentation.theme.SoraFontFamily
import com.example.coffeeshop.presentation.theme.colorDarkOrange
import com.example.coffeeshop.presentation.theme.colorLightGrey
import com.example.coffeeshop.presentation.viewmodel.FavoriteCoffeeViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteCoffeeScreen(
    navController: NavController
) {
    val viewModel: FavoriteCoffeeViewModel = hiltViewModel()

    val favoriteCoffees by viewModel.favoriteCoffees.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val savedScrollState by viewModel.scrollState.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(favoriteCoffees.isNotEmpty() && !isLoading) {
        if (favoriteCoffees.isNotEmpty() && savedScrollState > 0) {
            listState.scrollToItem(savedScrollState)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Избранное",
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = 20.sp,
                        color = colorScheme.onBackground
                    )
                },
                modifier = Modifier.padding(top = 68.dp),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorScheme.background
                )
            )
        },
        containerColor = colorScheme.background
    ) { padding ->

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorDarkOrange)
                }
            }
            error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Ошибка загрузки",
                        color = colorScheme.error,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error!!,
                        color = colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.clearError()
                            viewModel.loadFavorites()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorDarkOrange
                        )
                    ) {
                        Text("Повторить")
                    }
                }
            }
            favoriteCoffees.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    EmptyFavoritesState(navController)
                }
            }
            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .padding(padding),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(favoriteCoffees) { (coffee, savedSize) ->
                        CoffeeFavoriteCard(
                            coffee = coffee,
                            savedSize = savedSize,
                            viewModel = viewModel,
                            onRemove = {
                                viewModel.removeFromFavorites(coffee.id, savedSize)
                            },
                            navController = navController,
                            onNavigateToDetail = {
                                val firstVisibleItemIndex = listState.firstVisibleItemIndex
                                viewModel.saveScrollPosition(firstVisibleItemIndex)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}


@Composable
fun CoffeeFavoriteCard(
    coffee: ProductResponse,
    savedSize: String,
    viewModel: FavoriteCoffeeViewModel,
    onRemove: () -> Unit,
    navController: NavController,
    onNavigateToDetail: () -> Unit
) {
    val savedPrice = remember(coffee, savedSize) {
        viewModel.getPriceForSavedSize(coffee, savedSize)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                val sizesEncoded = viewModel.encodeSizesForNavigation(coffee)
                val imageUrlEncoded = URLEncoder.encode(coffee.imageUrl, "UTF-8")
                navController.navigate(
                    "${NavigationRoutes.DETAIL}/" +
                            "${coffee.id}/" +
                            "${coffee.name}/" +
                            "${coffee.type.type}/" +
                            "${coffee.description}" +
                            "?imageUrl=$imageUrlEncoded" +
                            "&sizes=$sizesEncoded" +
                            "&favoriteSize=$savedSize" +
                            "&sellerId=${coffee.sellerId ?: -1L}"
                )
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = coffee.imageUrl,
                contentDescription = coffee.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = coffee.name,
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 18.sp,
                    color = colorScheme.outline
                )
                Text(
                    text = coffee.type.type,
                    fontWeight = FontWeight.W400,
                    fontSize = 12.sp,
                    lineHeight = 14.4.sp,
                    color = colorScheme.outlineVariant,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Размер: $savedSize",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W500,
                    fontSize = 13.sp,
                    color = colorScheme.outline,
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                Text(
                    text = "₽${"%.2f".format(savedPrice)}",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                    color = colorDarkOrange
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        onRemove()
                    }
                    .background(colorScheme.surfaceTint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove from favorites",
                    tint = colorDarkOrange,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyFavoritesState(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFEDE0D4).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.heart),
                contentDescription = "Favorite",
                modifier = Modifier.size(48.dp),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Пока ничего нет",
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.W600,
            fontSize = 20.sp,
            color = Color(0xFF4A4A4A),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Добавляйте понравившиеся напитки в избранное",
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = Color(0xFFB0B0B0),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .height(48.dp)
                .width(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFD17C46))
                .clickable { navController.popBackStack() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Перейти в каталог",
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Favorites Screen")
@Composable
fun FavoriteScreenPreview() {
    CoffeeShopTheme {
        FavoriteCoffeeScreen(navController = rememberNavController())
    }
}