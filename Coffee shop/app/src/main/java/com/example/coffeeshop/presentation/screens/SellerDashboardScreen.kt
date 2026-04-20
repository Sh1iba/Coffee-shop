package com.example.coffeeshop.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.coffeeshop.R
import com.example.coffeeshop.data.remote.response.ProductResponse
import com.example.coffeeshop.data.remote.response.SellerOrderResponse
import com.example.coffeeshop.data.remote.response.SellerResponse
import com.example.coffeeshop.domain.SellerRequest
import com.example.coffeeshop.presentation.theme.SoraFontFamily
import com.example.coffeeshop.presentation.theme.colorDarkOrange
import com.example.coffeeshop.presentation.viewmodel.SellerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboardScreen(navController: NavController) {
    val viewModel: SellerViewModel = hiltViewModel()

    val myShop by viewModel.myShop.collectAsState()
    val myProducts by viewModel.myProducts.collectAsState()
    val myOrders by viewModel.myOrders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Магазин", "Товары", "Заказы")

    LaunchedEffect(selectedTab) {
        when (selectedTab) {
            1 -> viewModel.loadMyProducts()
            2 -> viewModel.loadMyOrders()
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 56.dp, start = 24.dp, end = 24.dp, bottom = 8.dp)
                        .height(44.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable { navController.navigateUp() },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.leftarrow),
                            contentDescription = "Назад",
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outline)
                        )
                    }
                    Text(
                        text = "Панель продавца",
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Box(modifier = Modifier.size(44.dp))
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = colorDarkOrange
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    title,
                                    fontFamily = SoraFontFamily,
                                    fontWeight = if (selectedTab == index) FontWeight.W600 else FontWeight.W400,
                                    fontSize = 14.sp
                                )
                            }
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = colorDarkOrange
                )
            } else {
                when (selectedTab) {
                    0 -> ShopTab(myShop, viewModel)
                    1 -> ProductsTab(myProducts, viewModel)
                    2 -> OrdersTab(myOrders)
                }
            }

            error?.let { msg ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("OK", color = colorDarkOrange)
                        }
                    }
                ) { Text(msg) }
            }
        }
    }
}

@Composable
private fun ShopTab(shop: SellerResponse?, viewModel: SellerViewModel) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    if (shop == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(32.dp)
            ) {
                Icon(
                    imageVector = Icons.TwoTone.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "У вас ещё нет магазина",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "Создайте магазин, чтобы начать продавать кофе",
                    fontFamily = SoraFontFamily,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = colorDarkOrange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Создать магазин", fontFamily = SoraFontFamily, fontWeight = FontWeight.W600)
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ShopInfoCard(shop, onEdit = { showEditDialog = true })
            }
            item {
                ShopStatsCard(shop)
            }
        }
    }

    if (showCreateDialog) {
        ShopFormDialog(
            title = "Создать магазин",
            initial = null,
            onConfirm = { req ->
                viewModel.createShop(req) {}
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false }
        )
    }

    if (showEditDialog && shop != null) {
        ShopFormDialog(
            title = "Редактировать магазин",
            initial = shop,
            onConfirm = { req ->
                viewModel.updateShop(req) {}
                showEditDialog = false
            },
            onDismiss = { showEditDialog = false }
        )
    }
}

@Composable
private fun ShopInfoCard(shop: SellerResponse, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        shop.name,
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W700,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        shop.category,
                        fontFamily = SoraFontFamily,
                        fontSize = 13.sp,
                        color = colorDarkOrange
                    )
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.TwoTone.Create, "Редактировать", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(
                shop.description,
                fontFamily = SoraFontFamily,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.TwoTone.Star, null, tint = colorDarkOrange, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    "%.1f".format(shop.rating),
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(16.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (shop.isActive) colorDarkOrange.copy(alpha = 0.15f) else MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        if (shop.isActive) "Активен" else "Неактивен",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = 12.sp,
                        color = if (shop.isActive) colorDarkOrange else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun ShopStatsCard(shop: SellerResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatItem(icon = Icons.TwoTone.Person, label = "Владелец", value = shop.ownerName)
            StatItem(icon = Icons.TwoTone.Star, label = "Рейтинг", value = "%.1f".format(shop.rating))
        }
    }
}

@Composable
private fun StatItem(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, null, tint = colorDarkOrange, modifier = Modifier.size(24.dp))
        Text(value, fontFamily = SoraFontFamily, fontWeight = FontWeight.W600, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
        Text(label, fontFamily = SoraFontFamily, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ProductsTab(products: List<ProductResponse>, viewModel: SellerViewModel) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Мои товары (${products.size})",
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = colorDarkOrange),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(Icons.TwoTone.AddCircle, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Добавить", fontFamily = SoraFontFamily, fontWeight = FontWeight.W600, fontSize = 13.sp)
            }
        }

        if (products.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.TwoTone.Place, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Товары ещё не добавлены", fontFamily = SoraFontFamily, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products) { product ->
                    ProductCard(product, onDelete = { viewModel.deleteProduct(product.id) })
                }
            }
        }
    }

    if (showAddDialog) {
        AddProductDialog(
            onConfirm = { name, description, categoryId, imageName ->
                viewModel.createProduct(
                    com.example.coffeeshop.domain.ProductManageRequest(
                        name = name, description = description,
                        categoryId = categoryId, imageName = imageName,
                        variants = listOf(
                            com.example.coffeeshop.domain.VariantRequest("S", java.math.BigDecimal("250")),
                            com.example.coffeeshop.domain.VariantRequest("M", java.math.BigDecimal("350")),
                            com.example.coffeeshop.domain.VariantRequest("L", java.math.BigDecimal("450"))
                        )
                    )
                ) {}
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
}

@Composable
private fun ProductCard(product: ProductResponse, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(product.name, fontFamily = SoraFontFamily, fontWeight = FontWeight.W600, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(product.type.type, fontFamily = SoraFontFamily, fontSize = 12.sp, color = colorDarkOrange)
                val priceText = product.sizes.joinToString(" / ") { "${it.size}: ${it.price.toInt()}₽" }
                Text(priceText, fontFamily = SoraFontFamily, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.TwoTone.Delete, "Удалить", tint = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить товар?", fontFamily = SoraFontFamily, fontWeight = FontWeight.W600) },
            text = { Text("«${product.name}» будет удалён навсегда.", fontFamily = SoraFontFamily) },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error, fontFamily = SoraFontFamily, fontWeight = FontWeight.W600)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Отмена", fontFamily = SoraFontFamily)
                }
            }
        )
    }
}

@Composable
private fun OrdersTab(orders: List<SellerOrderResponse>) {
    if (orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.TwoTone.Create, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Заказов пока нет", fontFamily = SoraFontFamily, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(orders) { order ->
                SellerOrderCard(order)
            }
        }
    }
}

@Composable
private fun SellerOrderCard(order: SellerOrderResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    "Заказ #${order.orderId}",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = colorDarkOrange.copy(alpha = 0.15f)
                ) {
                    Text(
                        order.status,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = 11.sp,
                        color = colorDarkOrange
                    )
                }
            }
            Text(
                order.deliveryAddress,
                fontFamily = SoraFontFamily,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            order.items.forEach { item ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        "${item.coffeeName} (${item.selectedSize}) × ${item.quantity}",
                        fontFamily = SoraFontFamily,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "${item.totalPrice}₽",
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    "Итого: ${order.itemsTotal}₽",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W700,
                    fontSize = 15.sp,
                    color = colorDarkOrange
                )
            }
        }
    }
}

@Composable
private fun ShopFormDialog(
    title: String,
    initial: SellerResponse?,
    onConfirm: (SellerRequest) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var category by remember { mutableStateOf(initial?.category ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontFamily = SoraFontFamily, fontWeight = FontWeight.W600, fontSize = 18.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название магазина", fontFamily = SoraFontFamily) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание", fontFamily = SoraFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Категория", fontFamily = SoraFontFamily) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank() && description.isNotBlank() && category.isNotBlank()) onConfirm(SellerRequest(name, description, category)) },
                colors = ButtonDefaults.buttonColors(containerColor = colorDarkOrange),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Сохранить", fontFamily = SoraFontFamily, fontWeight = FontWeight.W600)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", fontFamily = SoraFontFamily, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Composable
private fun AddProductDialog(
    onConfirm: (name: String, description: String, categoryId: Int, imageName: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf("1") }
    var imageName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить товар", fontFamily = SoraFontFamily, fontWeight = FontWeight.W600, fontSize = 18.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название", fontFamily = SoraFontFamily) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание", fontFamily = SoraFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = imageName,
                    onValueChange = { imageName = it },
                    label = { Text("Имя файла изображения", fontFamily = SoraFontFamily) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Text(
                    "Цены устанавливаются автоматически (S: 250₽, M: 350₽, L: 450₽)",
                    fontFamily = SoraFontFamily,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && description.isNotBlank())
                        onConfirm(name, description, categoryId.toIntOrNull() ?: 1, imageName)
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorDarkOrange),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Добавить", fontFamily = SoraFontFamily, fontWeight = FontWeight.W600)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", fontFamily = SoraFontFamily, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}
