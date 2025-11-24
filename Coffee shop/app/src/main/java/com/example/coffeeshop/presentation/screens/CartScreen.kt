
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.example.coffeeshop.R
import com.example.coffeeshop.data.managers.PrefsManager
import com.example.coffeeshop.data.remote.api.ApiClient
import com.example.coffeeshop.data.remote.response.CoffeeCartResponse
import com.example.coffeeshop.data.remote.response.CoffeeResponse
import com.example.coffeeshop.data.repository.CoffeeRepository
import com.example.coffeeshop.navigation.NavigationRoutes
import com.example.coffeeshop.presentation.theme.CoffeeShopTheme
import com.example.coffeeshop.presentation.theme.SoraFontFamily
import com.example.coffeeshop.presentation.theme.colorDarkOrange
import com.example.coffeeshop.presentation.theme.colorLightGrey
import com.example.coffeeshop.presentation.viewmodel.CartViewModel
import com.example.coffeeshop.presentation.viewmodel.FavoriteCoffeeViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val prefsManager = PrefsManager(context)

    val viewModel: CartViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CartViewModel(
                    repository = CoffeeRepository(ApiClient.coffeeApi),
                    prefsManager = prefsManager
                ) as T
            }
        }
    )

    val cartItems by viewModel.cartItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var selectedKeys by rememberSaveable { mutableStateOf<Set<String>>(emptySet()) }

    val selectedCartItems = remember(cartItems, selectedKeys) {
        cartItems.filter { item ->
            val key = "${item.id}_${item.selectedSize}"
            selectedKeys.contains(key)
        }
    }

    val selectedTotalPrice = remember(selectedCartItems) {
        selectedCartItems.sumOf { it.totalPrice.toDouble() }
    }

    val selectedTotalItems = remember(selectedCartItems) {
        selectedCartItems.sumOf { it.quantity }
    }

    val allSelected = remember(cartItems, selectedKeys) {
        cartItems.isNotEmpty() && selectedKeys.size == cartItems.size
    }

    LaunchedEffect(Unit) {
        viewModel.loadCart()
    }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Корзина",
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

                if (cartItems.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .background(colorScheme.background),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                if (allSelected) {
                                    selectedKeys = emptySet()
                                } else {
                                    selectedKeys = cartItems.map { "${it.id}_${it.selectedSize}" }.toSet()
                                }
                            }
                        ) {
                            Checkbox(
                                checked = allSelected,
                                onCheckedChange = { isChecked ->
                                    selectedKeys = if (isChecked) {
                                        cartItems.map { "${it.id}_${it.selectedSize}" }.toSet()
                                    } else {
                                        emptySet()
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = colorDarkOrange,
                                    uncheckedColor = Color.Gray
                                )
                            )
                            Text(
                                text = "Выбрать все",
                                fontWeight = FontWeight.W500,
                                fontSize = 16.sp,
                                color = colorScheme.onBackground,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        if (selectedKeys.isNotEmpty()) {
                            TextButton(
                                onClick = {
                                    selectedKeys.forEach { key ->
                                        val parts = key.split("_")
                                        if (parts.size == 2) {
                                            val coffeeId = parts[0].toIntOrNull()
                                            val selectedSize = parts[1]
                                            if (coffeeId != null) {
                                                viewModel.removeFromCart(coffeeId, selectedSize)
                                            }
                                        }
                                    }
                                    selectedKeys = emptySet()
                                }
                            ) {
                                Text(
                                    text = "Удалить",
                                    color = colorDarkOrange,
                                    fontWeight = FontWeight.W600
                                )
                            }
                        }
                    }
                }
            }
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
                            viewModel.loadCart()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorDarkOrange
                        )
                    ) {
                        Text("Повторить")
                    }
                }
            }
            cartItems.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    EmptyCartState(navController)
                }
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        items(cartItems) { item ->
                            CartItemCard(
                                item = item,
                                viewModel = viewModel,
                                navController = navController,
                                isSelected = selectedKeys.contains("${item.id}_${item.selectedSize}"),
                                onSelectionChange = { isSelected ->
                                    val key = "${item.id}_${item.selectedSize}"
                                    selectedKeys = if (isSelected) {
                                        selectedKeys + key
                                    } else {
                                        selectedKeys - key
                                    }
                                },
                                onQuantityChange = { newQuantity ->
                                    if (newQuantity > 0) {
                                        viewModel.updateQuantity(item.id, item.selectedSize, newQuantity)
                                    } else {
                                        viewModel.removeFromCart(item.id, item.selectedSize)
                                    }
                                },
                                onRemove = {
                                    viewModel.removeFromCart(item.id, item.selectedSize)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OrderButton(
                        totalPrice = selectedTotalPrice,
                        totalItems = selectedTotalItems,
                        onOrderClick = {
                            if (selectedCartItems.isNotEmpty()) {
                                val gson = Gson()
                                val selectedItemsJson = gson.toJson(selectedCartItems)
                                val totalPrice = selectedTotalPrice

                                navController.navigate(
                                    "${NavigationRoutes.ORDER}?selectedItems=${URLEncoder.encode(selectedItemsJson, "UTF-8")}&totalPrice=$totalPrice"
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CoffeeCartResponse,
    viewModel: CartViewModel,
    navController: NavController,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    val imageBytes by remember(item.id) {
        derivedStateOf { viewModel.getImageForCoffee(item.id) }
    }

    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable {
                coroutineScope.launch {
                    val fullCoffeeData = viewModel.getFullCoffeeData(item.id)
                    fullCoffeeData?.let { coffee ->
                        val sizesEncoded = URLEncoder.encode(
                            coffee.sizes.joinToString(",") { size ->
                                "${size.size}:${size.price}"
                            },
                            "UTF-8"
                        )
                        navController.navigate(
                            "${NavigationRoutes.DETAIL}/" +
                                    "${coffee.id}/" +
                                    "${coffee.name}/" +
                                    "${coffee.type.type}/" +
                                    "${coffee.description}/" +
                                    "${coffee.imageName}" +
                                    "?sizes=$sizesEncoded" +
                                    "&favoriteSize=${item.selectedSize}"
                        )
                    }
                }
            },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(60.dp)
            ) {
                if (imageBytes != null) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageBytes)
                                .build()
                        ),
                        contentDescription = item.name,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEDE0D4)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = colorDarkOrange
                        )
                    }
                }

                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { isChecked ->
                        onSelectionChange(isChecked)
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = colorDarkOrange,
                        uncheckedColor = Color.White
                    ),
                    modifier = Modifier
                        .size(20.dp)
                        .offset(x = (-4).dp, y = (-4).dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp
                )

                Text(
                    text = "Размер: ${item.selectedSize}",
                    fontWeight = FontWeight.W500,
                    fontSize = 13.sp,
                    color = Color(0xFF2F2D2C),
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                Text(
                    text = "₽${"%.2f".format(item.totalPrice)}",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                    color = colorDarkOrange
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onQuantityChange(item.quantity - 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.minus_button),
                            contentDescription = "Уменьшить",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = item.quantity.toString(),
                        fontWeight = FontWeight.W500,
                        modifier = Modifier.width(24.dp),
                        textAlign = TextAlign.Center
                    )

                    IconButton(
                        onClick = { onQuantityChange(item.quantity + 1) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.plus_button),
                            contentDescription = "Увеличить",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = colorDarkOrange
                    )
                }
            }
        }
    }
}

@Composable
fun OrderButton(totalPrice: Double, totalItems: Int, onOrderClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(48.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colorDarkOrange)
            .clickable(
                enabled = totalItems > 0,
                onClick = onOrderClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (totalItems > 0) {
                "К оформлению $totalItems шт., ₽${"%.2f".format(totalPrice)}"
            } else {
                "Выберите товары"
            },
            fontSize = 16.sp,
            fontWeight = FontWeight.W600,
            color = Color.White
        )
    }
}

@Composable
fun EmptyCartState(navController: NavController) {
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
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Cart",
                modifier = Modifier.size(48.dp),
                tint = colorDarkOrange
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Корзина пуста",
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.W600,
            fontSize = 20.sp,
            color = Color(0xFF4A4A4A),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Добавляйте понравившиеся напитки в корзину",
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CartScreenPreview() {
    CoffeeShopTheme {
        CartScreen(navController = rememberNavController())
    }
}