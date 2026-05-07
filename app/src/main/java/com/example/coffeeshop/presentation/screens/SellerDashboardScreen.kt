package com.example.coffeeshop.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.coffeeshop.R
import com.example.coffeeshop.data.remote.response.ProductCategoryResponse
import com.example.coffeeshop.data.remote.response.ProductResponse
import com.example.coffeeshop.data.remote.response.SellerOrderResponse
import com.example.coffeeshop.data.remote.response.SellerResponse
import com.example.coffeeshop.domain.ProductManageRequest
import com.example.coffeeshop.domain.SellerRequest
import com.example.coffeeshop.domain.VariantRequest
import com.example.coffeeshop.presentation.theme.SoraFontFamily
import com.example.coffeeshop.presentation.theme.colorDarkOrange
import com.example.coffeeshop.presentation.viewmodel.SellerViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.math.BigDecimal

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
                    2 -> OrdersTab(myOrders, viewModel)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductsTab(products: List<ProductResponse>, viewModel: SellerViewModel) {
    val categories by viewModel.categories.collectAsState()
    val isUploading by viewModel.isUploading.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductResponse?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredProducts = remember(products, searchQuery) {
        if (searchQuery.isBlank()) products
        else products.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    LaunchedEffect(Unit) { viewModel.loadCategories() }

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

        if (products.isNotEmpty()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Поиск товара...", fontFamily = SoraFontFamily, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.TwoTone.Search, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.TwoTone.Clear, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorDarkOrange,
                    focusedLabelColor = colorDarkOrange
                )
            )
        }

        if (products.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.TwoTone.Place, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Товары ещё не добавлены", fontFamily = SoraFontFamily, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else if (filteredProducts.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Ничего не найдено", fontFamily = SoraFontFamily, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 0.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredProducts) { product ->
                    ProductItemCard(
                        product = product,
                        onEdit = { editingProduct = product },
                        onDelete = { viewModel.deleteProduct(product.id) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        ProductFormDialog(
            title = "Добавить товар",
            initial = null,
            categories = categories,
            isUploading = isUploading,
            onUploadImage = { part, cb -> viewModel.uploadImage(part, cb) },
            onConfirm = { req -> viewModel.createProduct(req) {}; showAddDialog = false },
            onDismiss = { showAddDialog = false }
        )
    }

    editingProduct?.let { product ->
        ProductFormDialog(
            title = "Редактировать товар",
            initial = product,
            categories = categories,
            isUploading = isUploading,
            onUploadImage = { part, cb -> viewModel.uploadImage(part, cb) },
            onConfirm = { req -> viewModel.updateProduct(product.id, req) {}; editingProduct = null },
            onDismiss = { editingProduct = null }
        )
    }
}

@Composable
private fun ProductItemCard(
    product: ProductResponse,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(product.name, fontFamily = SoraFontFamily, fontWeight = FontWeight.W600, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(product.type.type, fontFamily = SoraFontFamily, fontSize = 12.sp, color = colorDarkOrange)
                val priceText = product.sizes.joinToString(" · ") { "${it.size}: ${it.price.toInt()}₽" }
                Text(priceText, fontFamily = SoraFontFamily, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.TwoTone.Create, "Редактировать", tint = colorDarkOrange)
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.TwoTone.Delete, "Удалить", tint = MaterialTheme.colorScheme.error)
                }
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
private fun OrdersTab(orders: List<SellerOrderResponse>, viewModel: SellerViewModel) {
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
                SellerOrderCard(order, onStatusUpdate = { newStatus ->
                    viewModel.updateOrderStatus(order.orderId, newStatus)
                })
            }
        }
    }
}

private fun nextSellerStatus(current: String): String? = when (current) {
    "PENDING"    -> "CONFIRMED"
    "CONFIRMED"  -> "PROCESSING"
    "PROCESSING" -> "READY"
    "READY"      -> "DELIVERED"
    else         -> null
}

private fun statusLabel(status: String) = when (status) {
    "PENDING"    -> "Ожидает"
    "CONFIRMED"  -> "Подтверждён"
    "PROCESSING" -> "Готовится"
    "READY"      -> "Готов"
    "DELIVERED"  -> "Доставлен"
    "CANCELLED"  -> "Отменён"
    else         -> status
}

private fun statusColor(status: String) = when (status) {
    "DELIVERED" -> Color(0xFF4CAF50)
    "CANCELLED" -> Color(0xFFE53935)
    else        -> null
}

@Composable
private fun SellerOrderCard(order: SellerOrderResponse, onStatusUpdate: (String) -> Unit) {
    val nextStatus = nextSellerStatus(order.status)
    val chipColor = statusColor(order.status)
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
                    color = (chipColor ?: colorDarkOrange).copy(alpha = 0.15f)
                ) {
                    Text(
                        statusLabel(order.status),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = 11.sp,
                        color = chipColor ?: colorDarkOrange
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Итого: ${order.itemsTotal}₽",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W700,
                    fontSize = 15.sp,
                    color = colorDarkOrange
                )
                if (nextStatus != null) {
                    Button(
                        onClick = { onStatusUpdate(nextStatus) },
                        colors = ButtonDefaults.buttonColors(containerColor = colorDarkOrange),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "→ ${statusLabel(nextStatus)}",
                            fontFamily = SoraFontFamily,
                            fontWeight = FontWeight.W600,
                            fontSize = 12.sp
                        )
                    }
                }
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

private data class VariantState(val size: String, val price: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductFormDialog(
    title: String,
    initial: ProductResponse?,
    categories: List<ProductCategoryResponse>,
    isUploading: Boolean,
    onUploadImage: (MultipartBody.Part, (String?) -> Unit) -> Unit,
    onConfirm: (ProductManageRequest) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf(initial?.name ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var selectedCategoryId by remember { mutableStateOf(initial?.type?.id ?: 1) }
    var imageUrl by remember { mutableStateOf(initial?.imageUrl ?: "") }
    var previewUri by remember { mutableStateOf<Uri?>(null) }
    var variants by remember {
        mutableStateOf(
            initial?.sizes?.map { VariantState(it.size, it.price.toInt().toString()) }
                ?: listOf(VariantState("S", ""), VariantState("M", ""), VariantState("L", ""))
        )
    }
    var categoryExpanded by remember { mutableStateOf(false) }

    val imageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            previewUri = it
            scope.launch {
                try {
                    val mimeType = context.contentResolver.getType(it) ?: "image/jpeg"
                    val ext = when (mimeType) {
                        "image/png"  -> "png"
                        "image/gif"  -> "gif"
                        "image/webp" -> "webp"
                        else         -> "jpg"
                    }
                    val bytes = context.contentResolver.openInputStream(it)?.readBytes() ?: return@launch
                    val body = bytes.toRequestBody(mimeType.toMediaType())
                    val part = MultipartBody.Part.createFormData("file", "upload.$ext", body)
                    onUploadImage(part) { uploaded -> if (uploaded != null) imageUrl = uploaded }
                } catch (_: Exception) {}
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.92f),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, fontFamily = SoraFontFamily, fontWeight = FontWeight.W600, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.TwoTone.Clear, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                HorizontalDivider()

                // Scrollable form
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedTextField(
                        value = name, onValueChange = { name = it },
                        label = { Text("Название", fontFamily = SoraFontFamily) },
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = description, onValueChange = { description = it },
                        label = { Text("Описание", fontFamily = SoraFontFamily) },
                        modifier = Modifier.fillMaxWidth(), minLines = 2, maxLines = 4,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Category dropdown
                    if (categories.isNotEmpty()) {
                        ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = it }) {
                            OutlinedTextField(
                                value = categories.find { it.id == selectedCategoryId }?.type ?: "",
                                onValueChange = {}, readOnly = true,
                                label = { Text("Категория", fontFamily = SoraFontFamily) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat.type, fontFamily = SoraFontFamily) },
                                        onClick = { selectedCategoryId = cat.id; categoryExpanded = false }
                                    )
                                }
                            }
                        }
                    }

                    // Image upload
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Изображение", fontFamily = SoraFontFamily, fontWeight = FontWeight.W600, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)

                        // Preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { imageLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (previewUri != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        ImageRequest.Builder(context).data(previewUri).build()
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                if (isUploading) {
                                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                                    }
                                }
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.TwoTone.AddCircle, null, modifier = Modifier.size(36.dp), tint = colorDarkOrange)
                                    Text("Нажмите чтобы выбрать фото", fontFamily = SoraFontFamily, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        if (imageUrl.isNotEmpty()) {
                            Text("✓ Фото загружено", fontFamily = SoraFontFamily, fontSize = 12.sp, color = colorDarkOrange)
                        }
                    }

                    // Variants
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Варианты", fontFamily = SoraFontFamily, fontWeight = FontWeight.W600, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                            TextButton(onClick = { variants = variants + VariantState("", "") }) {
                                Icon(Icons.TwoTone.AddCircle, null, modifier = Modifier.size(16.dp), tint = colorDarkOrange)
                                Spacer(Modifier.width(4.dp))
                                Text("Добавить", fontFamily = SoraFontFamily, fontSize = 13.sp, color = colorDarkOrange)
                            }
                        }

                        variants.forEachIndexed { index, variant ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = variant.size,
                                    onValueChange = { v -> variants = variants.toMutableList().also { it[index] = variant.copy(size = v) } },
                                    label = { Text("Размер", fontFamily = SoraFontFamily, fontSize = 11.sp) },
                                    singleLine = true, modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                OutlinedTextField(
                                    value = variant.price,
                                    onValueChange = { v ->
                                        if (v.isEmpty() || v.matches(Regex("\\d{0,6}(\\.\\d{0,2})?")))
                                            variants = variants.toMutableList().also { it[index] = variant.copy(price = v) }
                                    },
                                    label = { Text("Цена ₽", fontFamily = SoraFontFamily, fontSize = 11.sp) },
                                    singleLine = true, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)
                                )
                                if (variants.size > 1) {
                                    IconButton(onClick = { variants = variants.toMutableList().also { it.removeAt(index) } }) {
                                        Icon(Icons.TwoTone.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                    }
                                } else {
                                    Spacer(Modifier.size(48.dp))
                                }
                            }
                        }
                    }
                }

                HorizontalDivider()

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss, modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Отмена", fontFamily = SoraFontFamily) }

                    Button(
                        onClick = {
                            val validVariants = variants.filter { it.size.isNotBlank() && it.price.isNotBlank() }
                            if (name.isNotBlank() && description.isNotBlank() && imageUrl.isNotBlank() && validVariants.isNotEmpty()) {
                                onConfirm(
                                    ProductManageRequest(
                                        name = name.trim(),
                                        description = description.trim(),
                                        categoryId = selectedCategoryId,
                                        imageUrl = imageUrl.trim(),
                                        variants = validVariants.map {
                                            VariantRequest(it.size.trim(), it.price.toBigDecimalOrNull() ?: BigDecimal.ZERO)
                                        }
                                    )
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = colorDarkOrange),
                        shape = RoundedCornerShape(12.dp)
                    ) { Text("Сохранить", fontFamily = SoraFontFamily, fontWeight = FontWeight.W600) }
                }
            }
        }
    }
}
