package com.example.coffeeshop.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.example.coffeeshop.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.coffeeshop.data.managers.LocationManager
import com.example.coffeeshop.data.managers.PrefsManager
import com.example.coffeeshop.data.remote.api.ApiClient
import com.example.coffeeshop.data.remote.response.CoffeeCartResponse
import com.example.coffeeshop.data.remote.response.CoffeeResponse
import com.example.coffeeshop.data.remote.response.CoffeeSizeResponse
import com.example.coffeeshop.data.remote.response.CoffeeTypeResponse
import com.example.coffeeshop.data.repository.AddressRepository
import com.example.coffeeshop.data.repository.CoffeeRepository
import com.example.coffeeshop.navigation.NavigationRoutes
import com.example.coffeeshop.presentation.theme.CoffeeShopTheme
import com.example.coffeeshop.presentation.theme.SoraFontFamily
import com.example.coffeeshop.presentation.theme.colorDarkOrange
import com.example.coffeeshop.presentation.theme.colorFoundationGrey
import com.example.coffeeshop.presentation.theme.colorLightGrey
import com.example.coffeeshop.presentation.theme.colorSelectOrange
import com.example.coffeeshop.presentation.viewmodel.CartViewModel
import com.example.coffeeshop.presentation.viewmodel.CoffeeDetailViewModel
import com.example.coffeeshop.presentation.viewmodel.LocationState
import com.example.coffeeshop.presentation.viewmodel.LocationViewModel
import com.example.coffeeshop.presentation.viewmodel.OrderItem
import com.example.coffeeshop.presentation.viewmodel.OrderViewModel


@Composable
fun OrderScreen(
    navController: NavController,
    selectedItems: List<CoffeeCartResponse> = emptyList(),
    totalPrice: Double = 0.0
) {
    val context = LocalContext.current
    val prefsManager = PrefsManager(context)

    val viewModel: OrderViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return OrderViewModel(
                    repository = CoffeeRepository(ApiClient.coffeeApi),
                    prefsManager = prefsManager
                ) as T
            }
        }
    )

    // ViewModel для локации
    val locationViewModel: LocationViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LocationViewModel(
                    locationManager = LocationManager(context),
                    addressRepository = AddressRepository()
                ) as T
            }
        }
    )

    val orderItems by viewModel.orderItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val locationState by locationViewModel.uiState.collectAsState()

    LaunchedEffect(selectedItems) {
        if (selectedItems.isNotEmpty()) {
            viewModel.loadOrderItems(selectedItems)
        }
    }

    val scrollState = rememberScrollState()

    // Показываем диалог выбора адреса если нужно
    if (locationState.showAddressDialog) {
        AddressSelectionDialog(
            currentAddress = locationState.selectedAddress,
            searchQuery = locationState.addressSearchQuery,
            onSearchQueryChange = { locationViewModel.onAddressSearchQueryChange(it) },
            searchResults = locationState.addressSearchResults,
            isLoading = locationState.isAddressLoading,
            onAddressSelected = { address ->
                // Просто передаем текст адреса как строку
                val addressText = address.toString()
                locationViewModel.onAddressSelected(addressText)
            },
            onDismiss = { locationViewModel.onShowAddressDialogChange(false) },
            isTablet = false,
            screenHeight = LocalConfiguration.current.screenHeightDp.dp
        )
    }

    Scaffold(
        topBar = {
            OrderTopBar(
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            BottomOrderPanel(
                walletBalance = "₽5.53",
                totalPrice = totalPrice,
                deliveryType = if (locationState.selectedAddress.isNotEmpty()) "Доставка" else "Забрать",
                onOrderClick = {
                    println("Создание заказа на сумму: $totalPrice")
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 24.dp)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colorDarkOrange)
                    }
                }
                error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Ошибка загрузки",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                viewModel.clearError()
                                viewModel.loadOrderItems(selectedItems)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorDarkOrange
                            )
                        ) {
                            Text("Повторить")
                        }
                    }
                }
                else -> {
                    OrderContent(
                        orderItems = orderItems,
                        totalPrice = totalPrice,
                        locationState = locationState,
                        onLocationClick = { locationViewModel.onShowAddressDialogChange(true) }
                    )
                }
            }
        }
    }
}

@Composable
fun OrderContent(
    orderItems: List<OrderItem>,
    totalPrice: Double,
    locationState: LocationState,
    onLocationClick: () -> Unit
) {
    var selectedButton by remember { mutableStateOf("Доставка") }

    // Автоматически выбираем тип доставки в зависимости от наличия адреса
    LaunchedEffect(locationState.selectedAddress) {
        selectedButton = if (locationState.selectedAddress.isNotEmpty()) "Доставка" else "Забрать"
    }

    // Разбиваем адрес на основную часть и детали
    val (mainAddress, addressDetails) = remember(locationState.selectedAddress) {
        parseAddress(locationState.selectedAddress)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Row(
            modifier = Modifier
                .padding(top = 24.dp)
                .padding(horizontal = 24.dp)
                .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(12))
                .fillMaxWidth()
                .height(43.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .padding(all = 4.dp)
                    .background(
                        color = if (selectedButton == "Доставка") colorDarkOrange else Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { selectedButton = "Доставка" }
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                Text(
                    text = "Доставка",
                    fontFamily = SoraFontFamily,
                    fontWeight = if (selectedButton == "Доставка") FontWeight.W600 else FontWeight.W400,
                    fontSize = 16.sp,
                    lineHeight = 21.sp,
                    color = if (selectedButton == "Доставка") Color.White else Color(0xFF313131),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }

            Box(
                modifier = Modifier
                    .padding(all = 4.dp)
                    .background(
                        color = if (selectedButton == "Забрать") colorDarkOrange else Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { selectedButton = "Забрать" }
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                Text(
                    text = "Забрать",
                    fontFamily = SoraFontFamily,
                    fontWeight = if (selectedButton == "Забрать") FontWeight.W600 else FontWeight.W400,
                    fontSize = 16.sp,
                    lineHeight = 21.sp,
                    color = if (selectedButton == "Забрать") Color.White else Color(0xFF313131),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 24.dp)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // БЛОК АДРЕСА ДОСТАВКИ ПОКАЗЫВАЕТСЯ ТОЛЬКО ПРИ ВЫБОРЕ "ДОСТАВКА"
            if (selectedButton == "Доставка") {
                Text(
                    text = "Адрес Доставки",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Блок адреса с новым форматированием
                Column {
                    // Основная часть адреса - черная жирная
                    Text(
                        text = if (mainAddress.isNotEmpty()) mainAddress else "Выберите адрес",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Детали адреса - серые (если есть)
                    if (addressDetails.isNotEmpty()) {
                        Text(
                            text = addressDetails,
                            color = Color.Gray,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Row(
                    modifier = Modifier.wrapContentWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    OutlinedButton(
                        onClick = onLocationClick,
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, colorLightGrey),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.edit),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Изменить Адрес",
                            fontFamily = SoraFontFamily,
                            fontWeight = FontWeight.W400,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    OutlinedButton(
                        onClick = { /* Примечание */ },
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, colorLightGrey),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.note),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Примечание",
                            fontFamily = SoraFontFamily,
                            fontWeight = FontWeight.W400,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Divider(
                    modifier = Modifier.padding(horizontal = 17.dp),
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp
                )
            }

            // Динамический список выбранных товаров
            Text(
                text = "Ваш заказ (${orderItems.size} товаров)",
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            orderItems.forEach { orderItem ->
                OrderCoffeeCard(
                    coffeeName = orderItem.cartItem.name,
                    coffeeType = orderItem.coffeeData?.type?.type ?: "Кофе",
                    coffeeDescription = orderItem.cartItem.selectedSize,
                    coffeeImage = if (orderItem.imageBytes != null) {
                        rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(orderItem.imageBytes)
                                .build()
                        )
                    } else {
                        painterResource(id = R.drawable.banner)
                    },
                    quantity = orderItem.cartItem.quantity
                )
            }
        }

        Divider(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
            color = colorSelectOrange,
            thickness = 4.dp
        )

        Image(
            painter = painterResource(id = R.drawable.banner2),
            contentDescription = "banner Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 24.dp),
            contentScale = ContentScale.Crop
        )

        // Расчет стоимости с учетом типа доставки
        val deliveryFee = if (selectedButton == "Доставка") 50.00 else 0.00
        val finalTotalPrice = totalPrice + deliveryFee

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp)
        ) {
            Text(
                text = "Платежное Резюме",
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Сумма заказа",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = Color.Gray
                )
                Text(
                    text = "₽${"%.2f".format(totalPrice)}",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Плата за доставку показывается только для доставки
            if (selectedButton == "Доставка") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Плата за доставку",
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W400,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "₽${"%.2f".format(deliveryFee)}",
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Итого",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "₽${"%.2f".format(finalTotalPrice)}",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                    color = colorDarkOrange
                )
            }
        }
    }
}

// Функция для парсинга адреса на основную часть и детали
private fun parseAddress(fullAddress: String): Pair<String, String> {
    if (fullAddress.isEmpty()) return "" to ""

    return try {
        val parts = fullAddress.split(",")
        if (parts.size >= 2) {
            // Первая часть - город/район (основная)
            // Остальные части - детали адреса
            parts[0].trim() to parts.subList(1, parts.size).joinToString(", ").trim()
        } else {
            fullAddress to ""
        }
    } catch (e: Exception) {
        fullAddress to ""
    }
}

// Остальные компоненты без изменений
@Composable
fun OrderTopBar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp)
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
                    painter = painterResource(id = R.drawable.leftarrow),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = "Оформление заказа",
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )

            Box(modifier = Modifier.size(44.dp))
        }
    }
}

@Composable
fun BottomOrderPanel(
    walletBalance: String,
    totalPrice: Double,
    deliveryType: String,
    onOrderClick: () -> Unit = {}
) {
    val deliveryFee = if (deliveryType == "Доставка") 50.00 else 0.00
    val finalTotalPrice = totalPrice + deliveryFee

    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 12.dp
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.wallet),
                        contentDescription = null,
                        tint = colorDarkOrange,
                        modifier = Modifier.size(22.dp)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Наличные/Кошелек",
                            fontWeight = FontWeight.W600,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Text(
                            text = walletBalance,
                            fontWeight = FontWeight.W600,
                            fontSize = 12.sp,
                            color = colorDarkOrange
                        )
                    }
                }

                Icon(
                    painter = painterResource(id = R.drawable.img),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(colorDarkOrange)
                    .clickable { onOrderClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Заказать за ₽${"%.2f".format(finalTotalPrice)}",
                    fontWeight = FontWeight.W600,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun OrderCoffeeCard(
    coffeeName: String,
    coffeeType: String,
    coffeeDescription: String,
    coffeeImage: Painter,
    quantity: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(16.dp)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = coffeeImage,
                contentDescription = coffeeName,
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = coffeeName,
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = coffeeType,
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Text(
                    text = "Размер: $coffeeDescription",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Box(
                modifier = Modifier
                    .width(40.dp)
                    .wrapContentHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$quantity",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "OrderScreenPreview")
@Composable
fun OrderScreenPreview(){
    val mockItems = listOf(
        CoffeeCartResponse(
            id = 1,
            name = "Cappuccino с очень длинным названием которое может сдвигать элементы",
            selectedSize = "M",
            price = 3.00f,
            quantity = 2,
            totalPrice = 6.00f,
            imageName = "cappuccino.jpg"
        ),
        CoffeeCartResponse(
            id = 2,
            name = "Latte",
            selectedSize = "L",
            price = 4.50f,
            quantity = 1,
            totalPrice = 4.50f,
            imageName = "latte.jpg"
        )
    )

    OrderScreen(
        navController = rememberNavController(),
        totalPrice = 10.50
    )
}