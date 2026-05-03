package com.example.coffeeshop.presentation.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.coffeeshop.data.remote.response.OrderItemResponse
import com.example.coffeeshop.data.remote.response.OrderResponse
import com.example.coffeeshop.navigation.NavigationRoutes
import com.example.coffeeshop.presentation.theme.*
import com.example.coffeeshop.presentation.viewmodel.OrderHistoryViewModel
import java.math.BigDecimal
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private val ACTIVE_STATUSES = setOf("PENDING", "CONFIRMED", "PROCESSING", "READY")
private val DONE_STATUSES   = setOf("DELIVERED", "CANCELLED")

private fun parseDate(raw: String): String = try {
    val dt = LocalDateTime.parse(raw)
    dt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm"))
} catch (_: Exception) { raw }

private fun statusLabel(s: String) = when (s) {
    "PENDING"    -> "Ожидает подтверждения"
    "CONFIRMED"  -> "Подтверждён"
    "PROCESSING" -> "Готовится"
    "READY"      -> "Готов к выдаче"
    "DELIVERED"  -> "Доставлен"
    "CANCELLED"  -> "Отменён"
    else         -> s
}

private fun statusColor(s: String) = when (s) {
    "CONFIRMED"  -> Color(0xFF1E88E5)
    "PROCESSING" -> Color(0xFF8E24AA)
    "READY"      -> Color(0xFF43A047)
    "DELIVERED"  -> Color(0xFF43A047)
    "CANCELLED"  -> Color(0xFFE53935)
    else         -> Color(0xFFF57C00) // PENDING → orange
}

// Step index for the 4-step progress bar (CONFIRMED=0 … DELIVERED=3)
private fun stepIndex(status: String) = when (status) {
    "CONFIRMED"  -> 0
    "PROCESSING" -> 1
    "READY"      -> 2
    "DELIVERED"  -> 3
    else         -> -1 // PENDING → nothing lit yet
}

// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(navController: NavController) {
    val viewModel: OrderHistoryViewModel = hiltViewModel()
    val orders    by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error     by viewModel.error.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadOrderHistory() }

    val activeOrders  = orders.filter { it.status in ACTIVE_STATUSES }
    val historyOrders = orders.filter { it.status in DONE_STATUSES }
    val currency      = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Мои заказы",
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W700,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (activeOrders.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(colorDarkOrange)
                            )
                            Text(
                                "в реальном времени",
                                fontFamily = SoraFontFamily,
                                fontSize = 11.sp,
                                color = colorDarkOrange
                            )
                        }
                    }
                }
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        error?.let { msg ->
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = { TextButton(onClick = { viewModel.clearError() }) { Text("OK", color = colorDarkOrange) } }
            ) { Text(msg) }
        }

        when {
            isLoading && orders.isEmpty() -> Box(
                Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = colorDarkOrange) }

            orders.isEmpty() -> EmptyOrdersState(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                onBrowse = { navController.navigate(NavigationRoutes.HOME) }
            )

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp, end = 20.dp,
                    top = innerPadding.calculateTopPadding() + 8.dp,
                    bottom = innerPadding.calculateBottomPadding() + 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ── Active orders ───────────────────────────────────────────
                if (activeOrders.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Активные заказы",
                            badge = activeOrders.size
                        )
                    }
                    items(activeOrders, key = { it.id }) { order ->
                        ActiveOrderCard(
                            order = order,
                            currency = currency,
                            onCancel = { viewModel.cancelOrder(order.id) }
                        )
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }

                // ── History ─────────────────────────────────────────────────
                if (historyOrders.isNotEmpty()) {
                    item { SectionHeader(title = "История") }
                    items(historyOrders, key = { it.id }) { order ->
                        HistoryOrderCard(order = order, currency = currency)
                    }
                }
            }
        }
    }
}

// ─── Section header ───────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String, badge: Int = 0) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            title,
            fontFamily = SoraFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (badge > 0) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colorDarkOrange)
                    .padding(horizontal = 7.dp, vertical = 2.dp)
            ) {
                Text(
                    badge.toString(),
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W700,
                    fontSize = 11.sp,
                    color = Color.White
                )
            }
        }
    }
}

// ─── Active order card ────────────────────────────────────────────────────────

@Composable
private fun ActiveOrderCard(
    order: OrderResponse,
    currency: NumberFormat,
    onCancel: () -> Unit
) {
    var showCancelDialog by remember { mutableStateOf(false) }
    var expanded        by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // ── Header ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Заказ #${order.id}",
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W700,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        parseDate(order.orderDate),
                        fontFamily = SoraFontFamily,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusChip(status = order.status)
            }

            // ── Step progress ──
            OrderStepProgress(status = order.status)

            // ── Address ──
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = colorLightGrey, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text(
                    order.deliveryAddress,
                    fontFamily = SoraFontFamily,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

            // ── Items ──
            val visibleItems = if (expanded) order.items else order.items.take(1)
            visibleItems.forEach { item ->
                CompactItemRow(item = item, currency = currency)
            }
            if (order.items.size > 1) {
                TextButton(
                    onClick = { expanded = !expanded },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        if (expanded) "Свернуть" else "+ ещё ${order.items.size - 1} позиции",
                        fontFamily = SoraFontFamily,
                        fontSize = 12.sp,
                        color = colorDarkOrange
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))

            // ── Footer: total + cancel ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    if (order.deliveryFee > BigDecimal.ZERO) {
                        Text(
                            "Доставка: ${currency.format(order.deliveryFee)}",
                            fontFamily = SoraFontFamily,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        "Итого: ${currency.format(order.totalAmount)}",
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W700,
                        fontSize = 15.sp,
                        color = colorDarkOrange
                    )
                }
                if (order.status == "PENDING") {
                    OutlinedButton(
                        onClick = { showCancelDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Отменить", fontFamily = SoraFontFamily, fontWeight = FontWeight.W600, fontSize = 13.sp)
                    }
                }
            }
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Отменить заказ?", fontFamily = SoraFontFamily, fontWeight = FontWeight.W600) },
            text = { Text("Заказ #${order.id} будет отменён.", fontFamily = SoraFontFamily) },
            confirmButton = {
                TextButton(onClick = { onCancel(); showCancelDialog = false }) {
                    Text("Отменить заказ", color = MaterialTheme.colorScheme.error, fontFamily = SoraFontFamily, fontWeight = FontWeight.W600)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Назад", fontFamily = SoraFontFamily)
                }
            }
        )
    }
}

// ─── Step progress indicator ──────────────────────────────────────────────────

private val STEPS = listOf("Принят", "Готовится", "Готов", "Доставлен")

@Composable
private fun OrderStepProgress(status: String) {
    val current = stepIndex(status)

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            STEPS.forEachIndexed { i, _ ->
                val filled = i <= current
                val dotSize by animateDpAsState(if (i == current) 14.dp else 10.dp, label = "dot$i")
                val dotColor by animateColorAsState(
                    if (filled) colorDarkOrange else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                    label = "dotC$i"
                )

                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .clip(CircleShape)
                        .background(dotColor)
                        .then(if (i == current) Modifier.border(2.dp, colorDarkOrange.copy(alpha = 0.4f), CircleShape) else Modifier)
                )

                if (i < STEPS.lastIndex) {
                    val lineColor by animateColorAsState(
                        if (i < current) colorDarkOrange else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        label = "line$i"
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(lineColor)
                    )
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            STEPS.forEachIndexed { i, label ->
                val filled = i <= current
                Text(
                    label,
                    modifier = Modifier.weight(1f),
                    textAlign = when (i) {
                        0              -> TextAlign.Start
                        STEPS.lastIndex -> TextAlign.End
                        else           -> TextAlign.Center
                    },
                    fontFamily = SoraFontFamily,
                    fontSize = 10.sp,
                    fontWeight = if (i == current) FontWeight.W700 else FontWeight.W400,
                    color = if (filled) colorDarkOrange else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }

        // Status hint for PENDING (not yet on step 0)
        if (status == "PENDING") {
            Text(
                "⏳  Ожидаем подтверждения продавца...",
                fontFamily = SoraFontFamily,
                fontSize = 11.sp,
                color = Color(0xFFF57C00)
            )
        }
    }
}

// ─── Status chip ──────────────────────────────────────────────────────────────

@Composable
private fun StatusChip(status: String) {
    val color = statusColor(status)
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            statusLabel(status),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontFamily = SoraFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = 11.sp,
            color = color
        )
    }
}

// ─── Compact item row ─────────────────────────────────────────────────────────

@Composable
private fun CompactItemRow(item: OrderItemResponse, currency: NumberFormat) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "${item.coffeeName} · ${item.selectedSize}",
            fontFamily = SoraFontFamily,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "× ${item.quantity}",
                fontFamily = SoraFontFamily,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                currency.format(item.totalPrice),
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ─── History order card ───────────────────────────────────────────────────────

@Composable
private fun HistoryOrderCard(order: OrderResponse, currency: NumberFormat) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Заказ #${order.id}",
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        parseDate(order.orderDate),
                        fontFamily = SoraFontFamily,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusChip(status = order.status)
            }

            if (expanded) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                order.items.forEach { item -> CompactItemRow(item = item, currency = currency) }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Итого: ${currency.format(order.totalAmount)}",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(
                    onClick = { expanded = !expanded },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        if (expanded) "Свернуть" else "Детали",
                        fontFamily = SoraFontFamily,
                        fontSize = 12.sp,
                        color = colorDarkOrange
                    )
                }
            }
        }
    }
}

// ─── Empty state ──────────────────────────────────────────────────────────────

@Composable
private fun EmptyOrdersState(modifier: Modifier = Modifier, onBrowse: () -> Unit) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(colorDarkOrange.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(44.dp), tint = colorDarkOrange)
        }
        Spacer(Modifier.height(24.dp))
        Text(
            "Заказов пока нет",
            fontFamily = SoraFontFamily,
            fontWeight = FontWeight.W700,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Здесь появятся ваши заказы — активные и завершённые",
            fontFamily = SoraFontFamily,
            fontSize = 14.sp,
            color = colorLightGrey,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onBrowse,
            modifier = Modifier.height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorDarkOrange)
        ) {
            Text("Перейти в каталог", fontFamily = SoraFontFamily, fontWeight = FontWeight.W600, fontSize = 15.sp, color = Color.White)
        }
    }
}

// ─── Keep old public composables used in older screens ───────────────────────

@Composable
fun OrderCard(order: OrderResponse) {
    val currency = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    HistoryOrderCard(order = order, currency = currency)
}

@Composable
fun OrderItemRow(item: OrderItemResponse) {
    val currency = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    CompactItemRow(item = item, currency = currency)
}

@Composable
fun EmptyOrderHistoryState(navController: NavController, onNavigateToCatalog: () -> Unit = { navController.popBackStack() }) {
    EmptyOrdersState(onBrowse = onNavigateToCatalog)
}
