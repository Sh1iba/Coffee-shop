package com.example.coffeeshop.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.coffeeshop.navigation.NavigationRoutes
import com.example.coffeeshop.presentation.theme.*
import com.example.coffeeshop.presentation.viewmodel.BecomeSellerViewModel

private val CATEGORIES = listOf("Coffee", "Food", "Bakery", "Drinks", "Desserts", "Other")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BecomeSellerScreen(navController: NavController) {
    val viewModel: BecomeSellerViewModel = hiltViewModel()
    val isLoading by viewModel.isLoading.collectAsState()
    val error     by viewModel.error.collectAsState()
    val success   by viewModel.success.collectAsState()

    var name        by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category    by remember { mutableStateOf("") }
    var expanded    by remember { mutableStateOf(false) }

    LaunchedEffect(success) {
        if (success) {
            navController.navigate(NavigationRoutes.SELLER_DASHBOARD) {
                popUpTo(NavigationRoutes.SETTINGS) { inclusive = false }
            }
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(start = 8.dp, end = 24.dp, top = 16.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Text(
                        "Стать продавцом",
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W700,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Откройте свой магазин",
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.W700,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "Заполните информацию о вашем магазине. После регистрации вы получите доступ к кабинету продавца.",
                fontFamily = SoraFontFamily,
                fontSize = 13.sp,
                color = colorLightGrey,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(4.dp))

            // Название магазина
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Название магазина", fontFamily = SoraFontFamily) },
                placeholder = { Text("например, Urban Brew", fontFamily = SoraFontFamily) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorDarkOrange,
                    focusedLabelColor = colorDarkOrange
                )
            )

            // Описание
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание", fontFamily = SoraFontFamily) },
                placeholder = { Text("Расскажите о вашем магазине", fontFamily = SoraFontFamily) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorDarkOrange,
                    focusedLabelColor = colorDarkOrange
                )
            )

            // Категория — дропдаун
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Категория", fontFamily = SoraFontFamily) },
                    placeholder = { Text("Выберите категорию", fontFamily = SoraFontFamily) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorDarkOrange,
                        focusedLabelColor = colorDarkOrange
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    CATEGORIES.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat, fontFamily = SoraFontFamily) },
                            onClick = { category = cat; expanded = false }
                        )
                    }
                }
            }

            error?.let { msg ->
                Text(
                    msg,
                    fontFamily = SoraFontFamily,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { viewModel.register(name, description, category) },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorDarkOrange)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(
                        "Зарегистрироваться как продавец",
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
