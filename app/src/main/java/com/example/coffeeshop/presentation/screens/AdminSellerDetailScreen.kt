package com.example.coffeeshop.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material.icons.twotone.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.coffeeshop.data.remote.response.BranchResponse
import com.example.coffeeshop.data.remote.response.SellerResponse
import com.example.coffeeshop.presentation.theme.SoraFontFamily
import com.example.coffeeshop.presentation.theme.colorDarkOrange
import com.example.coffeeshop.presentation.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSellerDetailScreen(navController: NavController, sellerId: Long) {
    val viewModel: AdminViewModel = hiltViewModel()
    val allSellers by viewModel.allSellers.collectAsState()
    val sellerProducts by viewModel.sellerProducts.collectAsState()
    val sellerBranches by viewModel.sellerBranches.collectAsState()
    val error by viewModel.error.collectAsState()

    val seller = allSellers.find { it.id == sellerId }
    val products = sellerProducts[sellerId]
    val branches = sellerBranches[sellerId]

    var selectedTab by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadAllSellers()
        viewModel.loadSellerProducts(sellerId)
        viewModel.loadSellerBranches(sellerId)
    }

    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        seller?.name ?: "Магазин",
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.TwoTone.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        if (seller == null) {
            Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colorDarkOrange)
            }
            return@Scaffold
        }

        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            SellerHeaderCard(
                seller = seller,
                onToggleActive = { viewModel.toggleSellerActive(seller) }
            )

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = colorDarkOrange
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            if (products != null) "Товары (${products.size})" else "Товары",
                            fontFamily = SoraFontFamily,
                            fontWeight = if (selectedTab == 0) FontWeight.W600 else FontWeight.W400,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            if (branches != null) "Филиалы (${branches.size})" else "Филиалы",
                            fontFamily = SoraFontFamily,
                            fontWeight = if (selectedTab == 1) FontWeight.W600 else FontWeight.W400,
                            fontSize = 13.sp
                        )
                    }
                )
            }

            when (selectedTab) {
                0 -> {
                    if (products == null) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = colorDarkOrange)
                        }
                    } else if (products.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Товары ещё не добавлены", fontFamily = SoraFontFamily, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(products, key = { it.id }) { product ->
                                ProductModerationCard(
                                    product = product,
                                    onApprove = { viewModel.approveProduct(sellerId, product.id) },
                                    onDelete = { viewModel.deleteProduct(sellerId, product.id) },
                                    onRejectWithReason = { reason -> viewModel.rejectProduct(sellerId, product.id, reason) }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    if (branches == null) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = colorDarkOrange)
                        }
                    } else if (branches.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Филиалы ещё не добавлены", fontFamily = SoraFontFamily, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(branches, key = { it.id }) { branch ->
                                BranchDetailCard(
                                    branch = branch,
                                    onApprove = { viewModel.approveBranch(branch.id) },
                                    onRejectWithReason = { reason -> viewModel.rejectBranch(branch.id, reason) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SellerHeaderCard(
    seller: SellerResponse,
    onToggleActive: () -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!seller.logoImage.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context).data(seller.logoImage).crossfade(true).build(),
                        contentDescription = seller.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = colorDarkOrange.copy(alpha = 0.12f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.TwoTone.Store,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                                tint = colorDarkOrange
                            )
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            seller.name,
                            fontFamily = SoraFontFamily,
                            fontWeight = FontWeight.W700,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        StatusBadge(seller.status)
                    }
                    Text(
                        seller.category,
                        fontFamily = SoraFontFamily,
                        fontSize = 13.sp,
                        color = colorDarkOrange,
                        fontWeight = FontWeight.W600
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.TwoTone.Star, null, modifier = Modifier.size(14.dp), tint = Color(0xFFF59E0B))
                        Text(
                            "${seller.rating}",
                            fontFamily = SoraFontFamily,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (seller.status == "APPROVED") {
                    Switch(
                        checked = seller.isActive,
                        onCheckedChange = { onToggleActive() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colorDarkOrange,
                            checkedTrackColor = colorDarkOrange.copy(alpha = 0.3f),
                            uncheckedThumbColor = MaterialTheme.colorScheme.outlineVariant,
                            uncheckedTrackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                AdminInfoRow(Icons.TwoTone.Person, "Владелец: ${seller.ownerName}")
                if (!seller.phone.isNullOrBlank()) {
                    AdminInfoRow(Icons.TwoTone.Phone, seller.phone)
                }
                if (!seller.website.isNullOrBlank()) {
                    AdminInfoRow(Icons.TwoTone.Info, seller.website)
                }
                if (seller.description.isNotBlank()) {
                    AdminInfoRow(Icons.TwoTone.Info, seller.description)
                }
                if (!seller.rejectionReason.isNullOrBlank()) {
                    Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.errorContainer) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.TwoTone.Warning, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.error)
                            Text(seller.rejectionReason, fontFamily = SoraFontFamily, fontSize = 12.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BranchDetailCard(
    branch: BranchResponse,
    onApprove: () -> Unit,
    onRejectWithReason: (String) -> Unit
) {
    var showRejectDialog by remember { mutableStateOf(false) }
    var rejectReason by remember { mutableStateOf("") }

    val borderColor = when (branch.status) {
        "APPROVED" -> Color(0xFF22C55E)
        "REJECTED" -> MaterialTheme.colorScheme.error
        else -> Color(0xFFF59E0B)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, borderColor.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.TwoTone.Place, null, modifier = Modifier.size(28.dp), tint = colorDarkOrange)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(branch.name, fontFamily = SoraFontFamily, fontWeight = FontWeight.W700, fontSize = 15.sp)
                    Text(
                        "${branch.city}, ${branch.address}",
                        fontFamily = SoraFontFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusBadge(branch.status)
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (!branch.workingHours.isNullOrBlank()) {
                    AdminInfoRow(Icons.TwoTone.Info, branch.workingHours)
                }
                if (!branch.managerEmail.isNullOrBlank()) {
                    AdminInfoRow(Icons.TwoTone.Person, branch.managerEmail)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Доставка: ${branch.deliveryFee.toInt()}₽",
                        fontFamily = SoraFontFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Мин. заказ: ${branch.minOrderAmount.toInt()}₽",
                        fontFamily = SoraFontFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (branch.latitude != null && branch.longitude != null) {
                    Text(
                        "Координаты: ${String.format("%.5f", branch.latitude)}, ${String.format("%.5f", branch.longitude)}",
                        fontFamily = SoraFontFamily,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            if (!branch.rejectionReason.isNullOrBlank()) {
                Surface(shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.errorContainer) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.TwoTone.Warning, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.error)
                        Text(branch.rejectionReason, fontFamily = SoraFontFamily, fontSize = 12.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }

            if (branch.status == "PENDING") {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Icon(Icons.TwoTone.CheckCircle, null, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Одобрить", fontFamily = SoraFontFamily, fontWeight = FontWeight.W600, fontSize = 13.sp)
                    }
                    OutlinedButton(
                        onClick = { showRejectDialog = true },
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Icon(Icons.TwoTone.Clear, null, modifier = Modifier.size(15.dp), tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.width(4.dp))
                        Text("Отклонить", fontFamily = SoraFontFamily, fontWeight = FontWeight.W600, fontSize = 13.sp, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }

    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false; rejectReason = "" },
            title = {
                Text(
                    "Отклонить «${branch.name}»",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Укажите причину отклонения:", fontFamily = SoraFontFamily, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        placeholder = { Text("Например: неверный адрес или координаты", fontFamily = SoraFontFamily) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { onRejectWithReason(rejectReason.trim()); showRejectDialog = false; rejectReason = "" },
                    enabled = rejectReason.isNotBlank()
                ) {
                    Text("Отклонить", fontFamily = SoraFontFamily, fontWeight = FontWeight.W600, color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false; rejectReason = "" }) {
                    Text("Отмена", fontFamily = SoraFontFamily, color = MaterialTheme.colorScheme.primary)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}
