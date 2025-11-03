package com.example.coffeeshop.presentation.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.coffeeshop.R
import com.example.coffeeshop.data.managers.PrefsManager
import com.example.coffeeshop.data.repository.AddressRepository
import com.example.coffeeshop.data.managers.LocationManager
import com.example.coffeeshop.data.remote.response.NominatimAddress
import com.example.coffeeshop.data.managers.SearchHistoryManager

import com.example.coffeeshop.data.remote.api.ApiClient.coffeeApi
import com.example.coffeeshop.domain.RegisterRequest
import com.example.coffeeshop.data.remote.response.CoffeeResponse
import com.example.coffeeshop.presentation.theme.SoraFontFamily
import com.example.coffeeshop.presentation.theme.colorBackgroudWhite
import com.example.coffeeshop.presentation.theme.colorDarkOrange
import com.example.coffeeshop.presentation.theme.colorGrey
import com.example.coffeeshop.presentation.theme.colorGreyWhite
import com.example.coffeeshop.presentation.viewmodel.LocationViewModel
import com.example.coffeeshop.presentation.viewmodel.SearchViewModel
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(navController: NavHostController = rememberNavController()) {
    val viewModel: HomeViewModel = viewModel()
    val prefsManager = PrefsManager(LocalContext.current)
    val token = prefsManager.getToken()

    val selectedTypeId = remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        if (token != null) {
            viewModel.loadAllCoffee(token)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorBackgroudWhite)
    ) {
        secondHalfOfHomeScreen(selectedTypeId, viewModel)

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
fun secondHalfOfHomeScreen(
    selectedTypeId: MutableState<Int?>,
    viewModel: HomeViewModel
) {
    Scaffold(
        bottomBar = {
            BottomMenu()
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(600.dp)
                .offset(y = 375.dp)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                CoffeeCategoryRow(
                    onCoffeeTypeSelected = { typeId ->
                        selectedTypeId.value = typeId
                    },
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                CoffeeCategoryColumn(
                    coffeeList = viewModel.getFilteredCoffee(selectedTypeId.value),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                Spacer(modifier = Modifier.height(46.dp))
            }
        }
    }
}


@SuppressLint("SuspiciousIndentation")
@Composable
fun firstHalfOfHomeScreen(viewModel: HomeViewModel = viewModel()) {
    val context = LocalContext.current


    val locationViewModel = remember {
        LocationViewModel(
            locationManager = LocationManager(context),
            addressRepository = AddressRepository()
        )
    }
    val locationState by locationViewModel.uiState.collectAsState()

    // ViewModel для поиска
    val searchViewModel = remember {
        SearchViewModel(
            searchHistoryManager = SearchHistoryManager(context)
        )
    }
    val searchState by searchViewModel.uiState.collectAsState()

    LaunchedEffect(locationState.error) {
        locationState.error?.let { error ->
            println("Location Error: $error")
            locationViewModel.clearError()
        }
    }

    LaunchedEffect(searchState.searchText) {
        viewModel.searchCoffee(searchState.searchText)
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .width(161.dp)
            .height(43.dp)
            .offset(x = 24.dp, y = 68.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Локация",
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 12.sp,
                lineHeight = 14.4.sp,
                color = colorGrey,
                modifier = Modifier
                    .width(75.dp)
                    .height(14.dp)
            )

            Row(
                modifier = Modifier
                    .width(176.dp)
                    .height(21.dp)
                    .clickable {
                        locationViewModel.onShowAddressDialogChange(true)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = locationState.selectedAddress,
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = colorGreyWhite,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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

    // БЛОК ПОИСКОВОЙ СТРОКИ
    Column(
        modifier = Modifier
            .width(327.dp)
            .offset(x = 24.dp, y = 135.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Поле поиска
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
                        color = if (searchState.isSearchFocused) colorDarkOrange else Color.Transparent,
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
                        value = searchState.searchText,
                        onValueChange = searchViewModel::onSearchTextChange,
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                            .onFocusChanged { focusState ->
                                searchViewModel.onSearchFocusChange(focusState.isFocused)
                            },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                searchViewModel.performSearch(viewModel::searchCoffee)
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        ),
                        decorationBox = { innerTextField ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    if (searchState.searchText.isEmpty()) {
                                        Text(
                                            text = "Search coffee",
                                            fontSize = 16.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    innerTextField()
                                }

                                if (searchState.searchText.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            searchViewModel.clearSearch()
                                            viewModel.searchCoffee("")
                                            keyboardController?.hide()
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

            // Кнопка поиска
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(colorDarkOrange, RoundedCornerShape(12.dp))
                    .clickable {
                        searchViewModel.performSearch(viewModel::searchCoffee)
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.search),
                    contentDescription = "Search",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }

        // История поиска
        if (searchState.isSearchFocused && searchState.searchText.isEmpty()) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF2C2C2C),
                tonalElevation = 4.dp
            ) {
                Column {
                    if (searchState.searchHistory.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { searchViewModel.clearSearchHistory() }
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Clear history",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }

                        LazyColumn {
                            items(searchState.searchHistory) { item ->
                                Text(
                                    text = item,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            searchViewModel.selectSearchHistoryItem(item, viewModel::searchCoffee)
                                            keyboardController?.hide()
                                            focusManager.clearFocus()
                                        }
                                        .padding(12.dp),
                                    color = Color.White,
                                    fontSize = 16.sp
                                )

                                if (item != searchState.searchHistory.last()) {
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

    // ДИАЛОГ ВЫБОРА АДРЕСА (ВЫНЕСЕН В ОТДЕЛЬНЫЙ COMPOSABLE)
    if (locationState.showAddressDialog) {
        AddressSelectionDialog(
            currentAddress = locationState.selectedAddress,
            searchQuery = locationState.addressSearchQuery,
            onSearchQueryChange = locationViewModel::onAddressSearchQueryChange,
            searchResults = locationState.addressSearchResults,
            isLoading = locationState.isAddressLoading,
            onAddressSelected = locationViewModel::onAddressSelected,
            onDismiss = { locationViewModel.onShowAddressDialogChange(false) }
        )
    }
}

// ОТДЕЛЬНЫЙ COMPOSABLE ДЛЯ ДИАЛОГА ВЫБОРА АДРЕСА
@Composable
fun AddressSelectionDialog(
    currentAddress: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    searchResults: List<NominatimAddress>,
    isLoading: Boolean,
    onAddressSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF2C2C2C)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                // Заголовок
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Выберите город",
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = 18.sp,
                        color = Color.White
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Поле поиска адреса
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .background(
                            color = Color(0xFF3A3A3A),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        BasicTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                color = Color.White,
                                fontFamily = SoraFontFamily
                            ),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = "Поиск города...",
                                            fontSize = 16.sp,
                                            color = Color.Gray,
                                            fontFamily = SoraFontFamily
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )

                        if (searchQuery.isNotEmpty()) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = colorDarkOrange
                                )
                            } else {
                                IconButton(
                                    onClick = { onSearchQueryChange("") },
                                    modifier = Modifier.size(20.dp)
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
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Список адресов
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    if (searchQuery.length >= 2) {
                        if (isLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = colorDarkOrange)
                                }
                            }
                        } else if (searchResults.isNotEmpty()) {
                            items(searchResults) { address ->
                                AddressItem(
                                    address = address.display_name,
                                    isSelected = address.display_name == currentAddress,
                                    onClick = { onAddressSelected(address.display_name) }
                                )
                            }
                        } else {
                            item {
                                Text(
                                    text = "Городов по запросу \"$searchQuery\" не найдено",
                                    fontFamily = SoraFontFamily,
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .wrapContentWidth()
                                )
                            }
                        }
                    } else {
                        item {
                            Text(
                                text = "Введите 2+ символа для поиска",
                                fontFamily = SoraFontFamily,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .wrapContentWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

// ОТДЕЛЬНЫЙ COMPOSABLE ДЛЯ ЭЛЕМЕНТА АДРЕСА
@Composable
fun AddressItem(
    address: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Location",
            tint = colorDarkOrange,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = address,
            fontFamily = SoraFontFamily,
            fontSize = 14.sp,
            color = if (isSelected) colorDarkOrange else Color.White,
            modifier = Modifier.weight(1f)
        )

        if (isSelected) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = colorDarkOrange,
                modifier = Modifier.size(16.dp)
            )
        }
    }

    Divider(
        color = Color(0xFF3A3A3A),
        thickness = 1.dp
    )
}


data class BottomMenuItem(
    val label: String,
    val icon: Int,
    val selectedIndicator: Int
)

@Composable
fun CoffeeCategoryColumn(
    coffeeList: List<CoffeeResponse>,
    modifier: Modifier = Modifier
) {
    val baseUrl = "http://10.0.2.2:8080"
    val prefsManager = PrefsManager(LocalContext.current)
    val token = prefsManager.getToken() ?: ""

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 160.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(coffeeList.chunked(2)) { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                pair.forEach { coffee ->
                    CoffeeItem(coffee, baseUrl, token)
                }

                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun CoffeeItem(coffee: CoffeeResponse, baseUrl: String, token: String) {
    val imageUrl = "$baseUrl/api/coffee/image/${coffee.imageName}"

    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .setHeader("Authorization", token)
            .build()
    )

    Card(
        modifier = Modifier
            .width(156.dp)
            .height(238.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(125.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF2F2F2))
            ) {
                Image(
                    painter = imagePainter,
                    contentDescription = coffee.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = coffee.name,
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 16.sp,
                color = Color(0xFF2F2D2C),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = coffee.type.type,
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 12.sp,
                color = Color(0xFF9B9B9B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₽ ${coffee.price}",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 18.sp,
                    color = Color(0xFF2F2D2C)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = colorDarkOrange,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            // TODO: Добавить логику добавления в корзину
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}


@Composable
fun CoffeeCategoryRow(
    onCoffeeTypeSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: HomeViewModel = viewModel()
    val prefsManager = PrefsManager(LocalContext.current)
    val token = prefsManager.getToken()

    val coffeeTypes = remember { mutableStateOf(listOf("Все кофе")) }
    val selectedItem = remember { mutableStateOf("Все кофе") }
    val selectedTypeId = remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(token) {
        if (token != null && coffeeTypes.value.size == 1) {
            try {
                val response = coffeeApi.getAllCoffeeTypes(token)
                if (response.isSuccessful) {
                    val typesFromApi = response.body() ?: emptyList()
                    val typeNames = typesFromApi.map { it.type }
                    coffeeTypes.value = listOf("Все кофе") + typeNames


                    viewModel.coffeeTypeMapping = typesFromApi.associate { it.type to it.id }
                }
            } catch (e: Exception) {
            }
        }
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(end = 8.dp)
    ) {
        items(coffeeTypes.value) { item ->
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

                        if (item == "All Coffee") {
                            selectedTypeId.value = null
                            onCoffeeTypeSelected(null)
                        } else {
                            val typeId = viewModel.coffeeTypeMapping[item]
                            selectedTypeId.value = typeId
                            onCoffeeTypeSelected(typeId)
                        }
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

class HomeViewModel : ViewModel() {
    var lastQuery = ""
    val coffeeImage = mutableStateOf<RegisterRequest?>(null)
    var coffeeTypeMapping = mapOf<String, Int>()
    val allCoffee = mutableStateOf<List<CoffeeResponse>>(emptyList())
    val searchResults = mutableStateOf<List<CoffeeResponse>>(emptyList())
    val isSearching = mutableStateOf(false)

    fun loadAllCoffee(token: String) {
        viewModelScope.launch {
            try {
                val response = coffeeApi.getAllCoffee(token)
                if (response.isSuccessful) {
                    allCoffee.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {

            }
        }
    }


    fun searchCoffee(query: String) {
        isSearching.value = query.isNotBlank()

        if (query.isBlank()) {
            searchResults.value = emptyList()
            return
        }

        val filtered = allCoffee.value.filter { coffee ->
            coffee.name.contains(query, ignoreCase = true) ||
                    coffee.type.type.contains(query, ignoreCase = true) ||
                    coffee.description.contains(query, ignoreCase = true)
        }
        searchResults.value = filtered
    }

    fun getFilteredCoffee(typeId: Int?): List<CoffeeResponse> {
        return if (isSearching.value) {
            searchResults.value
        } else if (typeId == null) {
            allCoffee.value
        } else {
            allCoffee.value.filter { it.type.id == typeId }
        }
    }


    fun logCoffeeTypes(token: String) {
        viewModelScope.launch {
            try {
                Log.d("COFFEE_TYPES", "Загружаем типы кофе...")
                val response = coffeeApi.getAllCoffeeTypes(token)
                if (response.isSuccessful) {
                    val typesFromApi = response.body() ?: emptyList()
                    Log.d("COFFEE_TYPES", "Получено типов: ${typesFromApi.size}")
                    typesFromApi.forEach { type ->
                        Log.d("COFFEE_TYPES", "ID: ${type.id}, Type: ${type.type}")
                    }
                } else {
                    Log.e("COFFEE_TYPES", "Ошибка: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("COFFEE_TYPES", "Ошибка сети: ${e.message}")
            }
        }
    }
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

@Preview(showBackground = true, showSystemUi = true, name = "pre")
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = rememberNavController())
}