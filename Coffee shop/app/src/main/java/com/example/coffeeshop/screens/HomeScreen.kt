package com.example.coffeeshop.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.coffeeshop.R
import com.example.coffeeshop.history_search.SearchHistoryManager
import com.example.coffeeshop.network.api.ApiClient.coffeeApi
import com.example.coffeeshop.network.model.request.RegisterRequest
import com.example.coffeeshop.network.model.response.CoffeeTypeResponse
import com.example.coffeeshop.ui.theme.SoraFontFamily
import com.example.coffeeshop.ui.theme.colorBackgroudWhite
import com.example.coffeeshop.ui.theme.colorDarkOrange
import com.example.coffeeshop.ui.theme.colorGrey
import com.example.coffeeshop.ui.theme.colorGreyWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(navController: NavHostController = rememberNavController()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorBackgroudWhite)
    ) {

        secondHalfOfHomeScreen()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF313131), Color(0xFF111111)),
                        start = Offset(0f, Float.POSITIVE_INFINITY),
                        end = Offset(Float.POSITIVE_INFINITY, 0f)
                    )
                )
        )
        Image(
            painter = painterResource(id = R.drawable.banner),
            contentDescription = "banner Image",
            modifier = Modifier
                .width(327.dp)
                .height(140.dp)
                .offset(x = 24.dp, y = 211.dp),
            contentScale = ContentScale.Crop
        )
        firstHalfOfHomeScreen()

    }
}

@Composable
fun secondHalfOfHomeScreen() {
    Scaffold(
        bottomBar = {
            BottomMenu()
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .width(327.dp)
                .height(545.dp)
                .offset(x = 24.dp, y = 375.dp)
                .padding(innerPadding)
        ) {
            Column {
                CoffeeCategoryRow(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                )
                CoffeeCategoryColumn()
            }
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun firstHalfOfHomeScreen() {
    Box(
        modifier = Modifier
            .width(161.dp)
            .height(43.dp)
            .offset(x = 24.dp, y = 68.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Location",
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 12.sp,
                lineHeight = 14.4.sp,
                color = colorGrey,
                modifier = Modifier
                    .width(55.dp)
                    .height(14.dp)
            )

            Row(
                modifier = Modifier
                    .width(176.dp)
                    .height(21.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bilzen, Tanjungbalai",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = colorGreyWhite,
                    modifier = Modifier.weight(1f)
                )

                Image(
                    painter = painterResource(id = R.drawable.img),
                    contentDescription = "Custom Icon",
                    modifier = Modifier
                        .width(14.dp)
                        .height(14.dp)
                )
            }
        }
    }
    val context = LocalContext.current
    val searchHistoryManager = remember { SearchHistoryManager(context) }
    var searchText by rememberSaveable { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    var searchHistory by remember { mutableStateOf(searchHistoryManager.getSearchHistory()) }
    val focusManager = LocalFocusManager.current

    val lastActionTime = remember { mutableStateOf(System.currentTimeMillis()) }
    fun updateHistory() {
        searchHistory = searchHistoryManager.getSearchHistory()
    }
    // Timer that triggers after 2 seconds of inactivity
    LaunchedEffect(searchText) {
        val currentTime = System.currentTimeMillis()
        lastActionTime.value = currentTime
        delay(2000) // Wait for 2 seconds

        if (System.currentTimeMillis() - lastActionTime.value >= 2000 && searchText.isNotBlank()) {
            // After 2 seconds of inactivity, initiate search if text is not empty
            searchHistoryManager.addSearchQuery(searchText)
            updateHistory() // Update search history

        }
    }



    Column(
        modifier = Modifier
            .width(327.dp)
            .offset(x = 24.dp, y = 135.dp)
    ) {
        // Search Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Field
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF2C2C2C), Color(0xFF2A2A2A))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isFocused) colorDarkOrange else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = "Search",
                        modifier = Modifier.size(18.dp)
                    )

                    BasicTextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            lastActionTime.value = System.currentTimeMillis() // Update the last action time
                            if (it.isEmpty()) updateHistory()
                        },
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused
                                if (focusState.isFocused && searchText.isEmpty()) {
                                    updateHistory()
                                }
                            },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (searchText.isNotBlank()) {
                                    searchHistoryManager.addSearchQuery(searchText)
                                    updateHistory() // Явное обновление после добавления
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                    isFocused = false
                                }
                            }
                        ),
                        decorationBox = { innerTextField ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    if (searchText.isEmpty()) {
                                        Text(
                                            text = "Search coffee",
                                            fontSize = 16.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    innerTextField()
                                }

                                if (searchText.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            searchText = ""
                                            keyboardController?.hide()
                                            updateHistory()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Clear",
                                            tint = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }


            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(colorDarkOrange, RoundedCornerShape(12.dp))
                    .clickable {
                        if (searchText.isNotBlank()) {
                            searchHistoryManager.addSearchQuery(searchText)
                            updateHistory() // Явное обновление после добавления

                            keyboardController?.hide()
                            focusManager.clearFocus()
                            isFocused = false
                        }
                    },
                contentAlignment = Alignment.Center
            ) {


            }
        }

        if (isFocused && searchText.isEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF2C2C2C),
                tonalElevation = 4.dp
            ) {
                Column {
                    if (searchHistory.isNotEmpty()) {
                        // Clear History Button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    searchHistoryManager.clearSearchHistory()
                                    updateHistory() // Явное обновление после очистки
                                }
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Clear history",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }

                        // History Items
                        LazyColumn {
                            items(searchHistory) { item ->
                                Text(
                                    text = item,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            searchText = item
                                            searchHistoryManager.addSearchQuery(item) // Добавляем при выборе
                                            updateHistory() // Обновляем историю
                                            keyboardController?.hide()
                                            focusManager.clearFocus()
                                            isFocused = false
                                        }
                                        .padding(12.dp),
                                    color = Color.White,
                                    fontSize = 16.sp
                                )

                                if (item != searchHistory.last()) {
                                    Divider(
                                        color = Color(0xFF3A3A3A),
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                }
                            }
                        }
                    } else {

                        Text(
                            text = "No search history",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}


data class BottomMenuItem(
    val label: String,
    val icon: Int,
    val selectedIndicator: Int
)

@Composable
fun CoffeeCategoryColumn(viewModel: HomeViewModel = viewModel()) {
    val productImages = listOf(
        R.drawable.item_1,
        R.drawable.item_2,
        R.drawable.item_2,
        R.drawable.item_2
    )

    LazyColumn(
        modifier = Modifier
            .width(327.dp)
            .height(240.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            when {
                viewModel.isLoading.value -> LoadingState()
                viewModel.errorMessage.value != null -> ErrorState(viewModel)

                else -> EmptyState(viewModel)
            }
        }

        items(productImages.chunked(2)) { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                pair.forEach { imageRes ->
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = null,
                        modifier = Modifier
                            .width(156.dp)
                            .height(238.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorState(viewModel: HomeViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Error",
            tint = Color.Red,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = viewModel.errorMessage.value ?: "Произошла ошибка",
            color = Color.Red,
            modifier = Modifier.padding(8.dp)
        )
        Button(
            onClick = {  },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorDarkOrange
            )
        ) {
            Text("Обновить")
        }
    }
}

@Composable
fun EmptyState(viewModel: HomeViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Нет результатов поиска",
            color = Color.Gray
        )
        Button(
            onClick = {  },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorDarkOrange
            ),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Попробовать снова")
        }
    }
}

class HomeViewModel : ViewModel() {

    val coffeeTypes = mutableStateOf<List<CoffeeTypeUI>>(emptyList())
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    fun getCoffeeTypes() {
        isLoading.value = true
        errorMessage.value = null

        viewModelScope.launch {
            try {

                val mockTypes = listOf(
                    CoffeeTypeResponse(1, "На молоке"),

                )

                coffeeTypes.value = mockTypes.map {
                    CoffeeTypeUI(
                        id = it.id,
                        name = it.type,
                        isSelected = it.id == 1
                    )
                }

                val response = coffeeApi.getAllCoffeeTypes()
                if (response.isSuccessful) {
                    val typesFromApi = response.body() ?: emptyList()
                    coffeeTypes.value = typesFromApi.map {
                        CoffeeTypeUI(
                            id = it.id,
                            name = it.type,
                            isSelected = it.id == 1
                        )
                    }
                } else {
                    errorMessage.value = "Ошибка загрузки: ${response.code()}"
                }


            } catch (e: Exception) {
                errorMessage.value = "Ошибка сети: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    // Выбор типа кофе
    fun selectCoffeeType(typeId: Int) {
        coffeeTypes.value = coffeeTypes.value.map {
            it.copy(isSelected = it.id == typeId)
        }

        // Здесь можно добавить загрузку кофе по выбранному типу
        // getCoffeeByType(typeId)
    }

    init {
        getCoffeeTypes()
    }
}

data class CoffeeTypeUI(
    val id: Int,
    val name: String,
    val isSelected: Boolean = false
)
@Preview(showBackground = true, showSystemUi = true, name = "pre")
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}

@Composable
fun BottomMenuIcon(item: BottomMenuItem, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = item.icon),
            contentDescription = item.label,
            modifier = Modifier.size(24.dp),
            colorFilter = if (isSelected) {
                ColorFilter.tint(Color(0xFFC67C4E))
            } else {
                ColorFilter.tint(Color(0xFFA2A2A2))
            }
        )

        if (isSelected) {
            Image(
                painter = painterResource(id = item.selectedIndicator),
                contentDescription = "Selected indicator",
                modifier = Modifier.size(8.dp)
                    .padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun CoffeeCategoryRow(modifier: Modifier = Modifier) {
    val coffeeItems = listOf(
        "All Coffee",
        "Machiato",
        "Latte",
        "Americano"
    )

    val selectedItem = remember { mutableStateOf("All Coffee") }

    LazyRow(
        modifier = modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(end = 8.dp)
    ) {
        items(coffeeItems) { item ->
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .background(
                        color = if (item == selectedItem.value) colorDarkOrange else Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(
                        top = 4.dp,
                        bottom = 4.dp,
                        start = 8.dp,
                        end = 8.dp
                    )
                    .clickable {
                        selectedItem.value = item
                    }
            ) {
                Text(
                    text = item,
                    fontFamily = SoraFontFamily,
                    fontWeight = if (item == selectedItem.value) FontWeight.W600 else FontWeight.W400,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = if (item == selectedItem.value) Color.White else Color(0xFF313131),
                )
            }
        }
    }
}

@Composable
fun BottomMenu() {
    val menuItems = listOf(
        BottomMenuItem("Home", R.drawable.menu_home, R.drawable.selected_dot),
        BottomMenuItem("Favorite", R.drawable.heart, R.drawable.selected_dot),
        BottomMenuItem("Cart", R.drawable.cart, R.drawable.selected_dot),
        BottomMenuItem("Profile", R.drawable.notification, R.drawable.selected_dot)
    )

    val selectedItem = remember { mutableStateOf(menuItems[0].label) }

    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(99.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(
                    topStart = 24.dp,
                    topEnd = 24.dp
                )
            ),
        containerColor = Color.White,
        contentPadding = PaddingValues(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            menuItems.forEach { item ->
                BottomMenuIcon(
                    item = item,
                    isSelected = item.label == selectedItem.value,
                    onClick = { selectedItem.value = item.label }
                )
            }
        }
    }
}