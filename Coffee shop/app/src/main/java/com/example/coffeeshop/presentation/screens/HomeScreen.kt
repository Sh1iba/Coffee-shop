@file:Suppress("UNCHECKED_CAST")

package com.example.coffeeshop.presentation.screens

import android.annotation.SuppressLint
import android.content.res.Configuration
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.coffeeshop.R
import com.example.coffeeshop.data.managers.PrefsManager
import com.example.coffeeshop.data.repository.AddressRepository
import com.example.coffeeshop.data.managers.LocationManager
import com.example.coffeeshop.data.remote.response.NominatimAddress
import com.example.coffeeshop.data.managers.SearchHistoryManager
import com.example.coffeeshop.data.remote.api.ApiClient

import com.example.coffeeshop.data.remote.api.ApiClient.coffeeApi
import com.example.coffeeshop.domain.RegisterRequest
import com.example.coffeeshop.data.remote.response.CoffeeResponse
import com.example.coffeeshop.data.repository.CoffeeRepository
import com.example.coffeeshop.navigation.NavigationRoutes
import com.example.coffeeshop.presentation.theme.SoraFontFamily
import com.example.coffeeshop.presentation.theme.colorBackgroudWhite
import com.example.coffeeshop.presentation.theme.colorDarkOrange
import com.example.coffeeshop.presentation.theme.colorGrey
import com.example.coffeeshop.presentation.theme.colorGreyWhite
import com.example.coffeeshop.presentation.viewmodel.CartViewModel
import com.example.coffeeshop.presentation.viewmodel.HomeViewModel
import com.example.coffeeshop.presentation.viewmodel.LocationViewModel
import com.example.coffeeshop.presentation.viewmodel.SearchViewModel
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(navController: NavHostController = rememberNavController()) {
    val viewModel: HomeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(
                    repository = CoffeeRepository(ApiClient.coffeeApi)
                ) as T
            }
        }
    )

    val prefsManager = PrefsManager(LocalContext.current)
    val token = prefsManager.getToken()

    val cartViewModel: CartViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CartViewModel(
                    repository = CoffeeRepository(ApiClient.coffeeApi),
                    prefsManager = prefsManager
                ) as T
            }
        }
    )

    val selectedTypeId = remember { mutableStateOf<Int?>(null) }
    val showSizeDialog by viewModel.showSizeDialog.collectAsState()

    LaunchedEffect(Unit) {
        if (token != null) {
            viewModel.loadCoffeeData(token)
            cartViewModel.loadCart()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorBackgroudWhite)
    ) {
        SecondHalfOfHomeScreen(viewModel, navController, cartViewModel)

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
        FirstHalfOfHomeScreen(viewModel = viewModel)

        showSizeDialog?.let { coffee ->
            SizeSelectionDialog(
                coffee = coffee,
                onDismiss = { viewModel.hideSizeSelectionDialog() },
                onSizeSelected = { selectedSize ->
                    cartViewModel.addToCart(coffee.id, selectedSize)
                }
            )
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun SecondHalfOfHomeScreen(
    viewModel: HomeViewModel,
    navController: NavController,
    cartViewModel: CartViewModel
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val isTablet = configuration.screenWidthDp >= 600
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val contentHeight = when {
        isTablet && isLandscape -> screenHeight * 0.7f
        isTablet -> screenHeight * 0.65f
        else -> 600.dp
    }

    val contentOffset = when {
        isTablet && isLandscape -> screenHeight * 0.25f
        isTablet -> screenHeight * 0.3f
        else -> 375.dp
    }

    val horizontalPadding = if (isTablet) 48.dp else 24.dp
    val bottomSpacing = if (isTablet) 60.dp else 46.dp
    val rowBottomPadding = if (isTablet) 24.dp else 16.dp

    val homeState by viewModel.uiState.collectAsState()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(contentHeight)
                .offset(y = contentOffset)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding)
            ) {
                CoffeeCategoryRow(
                    onCoffeeTypeSelected = { typeId ->
                        val typeName = if (typeId == null) {
                            "Все кофе"
                        } else {
                            homeState.coffeeTypeMapping.entries.find { it.value == typeId }?.key ?: "Все кофе"
                        }
                        viewModel.onCoffeeTypeSelected(typeName)
                    },
                    viewModel = viewModel,
                    modifier = Modifier.padding(bottom = rowBottomPadding)
                )

                CoffeeCategoryColumn(
                    coffeeList = homeState.filteredCoffee,
                    viewModel = viewModel,
                    navController = navController,
                    cartViewModel = cartViewModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Spacer(modifier = Modifier.height(150.dp))
            }
        }
}



@SuppressLint("SuspiciousIndentation")
@Composable
fun FirstHalfOfHomeScreen(viewModel: HomeViewModel) {
    val context = LocalContext.current

    val locationViewModel = remember {
        LocationViewModel(
            locationManager = LocationManager(context),
            addressRepository = AddressRepository()
        )
    }
    val locationState by locationViewModel.uiState.collectAsState()

    val searchViewModel = remember {
        SearchViewModel(
            searchHistoryManager = SearchHistoryManager(context)
        )
    }
    val searchState by searchViewModel.uiState.collectAsState()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val isTablet = screenWidth >= 600.dp
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val horizontalPadding = when {
        isTablet && isLandscape -> 48.dp
        isTablet -> 40.dp
        else -> 24.dp
    }

    val topPadding = when {
        isTablet && isLandscape -> 40.dp
        isTablet -> 80.dp
        else -> 68.dp
    }

    val locationWidth = when {
        isTablet -> (screenWidth * 0.3f).coerceAtMost(200.dp)
        else -> (screenWidth * 0.5f).coerceAtMost(161.dp)
    }

    val searchBarWidth = when {
        isTablet -> (screenWidth * 0.7f).coerceAtMost(400.dp)
        else -> (screenWidth - horizontalPadding * 2)
    }


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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = topPadding)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding)
        ) {
            Column(
                modifier = Modifier
                    .width(locationWidth)
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Локация",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W400,
                    fontSize = if (isTablet) 14.sp else 12.sp,
                    lineHeight = if (isTablet) 16.8.sp else 14.4.sp,
                    color = colorGrey,
                    modifier = Modifier.wrapContentSize()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isTablet) 24.dp else 21.dp)
                        .clickable {
                            locationViewModel.onShowAddressDialogChange(true)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = locationState.selectedAddress,
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = if (isTablet) 16.sp else 14.sp,
                        lineHeight = if (isTablet) 24.sp else 21.sp,
                        color = colorGreyWhite,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Image(
                        painter = painterResource(id = R.drawable.img),
                        contentDescription = "Custom Icon",
                        modifier = Modifier
                            .size(if (isTablet) 16.dp else 14.dp)
                    )
                }
            }
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = if (isTablet) 32.dp else 24.dp,
                    start = horizontalPadding,
                    end = horizontalPadding
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(if (isTablet) 20.dp else 16.dp)
            ) {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(if (isTablet) 56.dp else 52.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF2C2C2C), Color(0xFF2A2A2A))
                            ),
                            shape = RoundedCornerShape(if (isTablet) 14.dp else 12.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = if (searchState.isSearchFocused) colorDarkOrange else Color.Transparent,
                            shape = RoundedCornerShape(if (isTablet) 14.dp else 12.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = if (isTablet) 20.dp else 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.search),
                            contentDescription = "Search",
                            modifier = Modifier.size(if (isTablet) 20.dp else 18.dp)
                        )

                        BasicTextField(
                            value = searchState.searchText,
                            onValueChange = searchViewModel::onSearchTextChange,
                            textStyle = TextStyle(
                                fontSize = if (isTablet) 18.sp else 16.sp,
                                color = Color.White
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = if (isTablet) 12.dp else 8.dp)
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
                                                fontSize = if (isTablet) 18.sp else 16.sp,
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
                                            modifier = Modifier.size(if (isTablet) 28.dp else 24.dp)
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
                        .size(if (isTablet) 56.dp else 52.dp)
                        .background(colorDarkOrange, RoundedCornerShape(if (isTablet) 14.dp else 12.dp))
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
                        modifier = Modifier.size(if (isTablet) 26.dp else 24.dp),
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }
            }

            if (searchState.isSearchFocused && searchState.searchText.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = if (isTablet) 12.dp else 8.dp),
                    shape = RoundedCornerShape(if (isTablet) 14.dp else 12.dp),
                    color = Color(0xFF2C2C2C),
                    tonalElevation = 4.dp
                ) {
                    Column {
                        if (searchState.searchHistory.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { searchViewModel.clearSearchHistory() }
                                    .padding(if (isTablet) 12.dp else 8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "Clear history",
                                    color = Color.Gray,
                                    fontSize = if (isTablet) 16.sp else 14.sp
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
                                            .padding(if (isTablet) 16.dp else 12.dp),
                                        color = Color.White,
                                        fontSize = if (isTablet) 18.sp else 16.sp
                                    )

                                    if (item != searchState.searchHistory.last()) {
                                        Divider(
                                            color = Color(0xFF3A3A3A),
                                            thickness = 1.dp,
                                            modifier = Modifier.padding(horizontal = if (isTablet) 12.dp else 8.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "No search history",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(if (isTablet) 16.dp else 12.dp),
                                color = Color.Gray,
                                fontSize = if (isTablet) 18.sp else 16.sp
                            )
                        }
                    }
                }
            }
        }
    }


    if (locationState.showAddressDialog) {
        AddressSelectionDialog(
            currentAddress = locationState.selectedAddress,
            searchQuery = locationState.addressSearchQuery,
            onSearchQueryChange = locationViewModel::onAddressSearchQueryChange,
            searchResults = locationState.addressSearchResults,
            isLoading = locationState.isAddressLoading,
            onAddressSelected = locationViewModel::onAddressSelected,
            onDismiss = { locationViewModel.onShowAddressDialogChange(false) },
            isTablet = isTablet,
            screenHeight = screenHeight
        )
    }
}


@Composable
fun AddressSelectionDialog(
    currentAddress: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    searchResults: List<NominatimAddress>,
    isLoading: Boolean,
    onAddressSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    isTablet: Boolean,
    screenHeight: Dp
) {
    val dialogWidth = when {
        isTablet -> (screenHeight * 0.6f).coerceAtMost(500.dp)
        else -> (screenHeight * 0.8f).coerceAtMost(400.dp)
    }

    val dialogHeight = when {
        isTablet -> (screenHeight * 0.7f).coerceAtMost(600.dp)
        else -> (screenHeight * 0.6f).coerceAtMost(500.dp)
    }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .width(dialogWidth)
                .height(dialogHeight),
            shape = RoundedCornerShape(if (isTablet) 16.dp else 12.dp),
            color = Color(0xFF2C2C2C)
        ) {
            Column(
                modifier = Modifier
                    .padding(if (isTablet) 20.dp else 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Выберите город",
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = if (isTablet) 20.sp else 18.sp,
                        color = Color.White
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(if (isTablet) 28.dp else 24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(if (isTablet) 20.dp else 16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isTablet) 56.dp else 52.dp)
                        .background(
                            color = Color(0xFF3A3A3A),
                            shape = RoundedCornerShape(if (isTablet) 10.dp else 8.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = if (isTablet) 20.dp else 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.Gray,
                            modifier = Modifier.size(if (isTablet) 22.dp else 20.dp)
                        )

                        Spacer(modifier = Modifier.width(if (isTablet) 14.dp else 12.dp))

                        BasicTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            textStyle = TextStyle(
                                fontSize = if (isTablet) 18.sp else 16.sp,
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
                                            fontSize = if (isTablet) 18.sp else 16.sp,
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
                                    modifier = Modifier.size(if (isTablet) 22.dp else 20.dp),
                                    strokeWidth = 2.dp,
                                    color = colorDarkOrange
                                )
                            } else {
                                IconButton(
                                    onClick = { onSearchQueryChange("") },
                                    modifier = Modifier.size(if (isTablet) 22.dp else 20.dp)
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

                Spacer(modifier = Modifier.height(if (isTablet) 20.dp else 16.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    if (searchQuery.length >= 2) {
                        if (isLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(if (isTablet) 20.dp else 16.dp),
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
                                    onClick = { onAddressSelected(address.display_name) },
                                    isTablet = isTablet
                                )
                            }
                        } else {
                            item {
                                Text(
                                    text = "Городов по запросу \"$searchQuery\" не найдено",
                                    fontFamily = SoraFontFamily,
                                    fontSize = if (isTablet) 16.sp else 14.sp,
                                    color = Color.Gray,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(if (isTablet) 20.dp else 16.dp)
                                        .wrapContentWidth()
                                )
                            }
                        }
                    } else {
                        item {
                            Text(
                                text = "Введите 2+ символа для поиска",
                                fontFamily = SoraFontFamily,
                                fontSize = if (isTablet) 16.sp else 14.sp,
                                color = Color.Gray,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(if (isTablet) 20.dp else 16.dp)
                                    .wrapContentWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun AddressItem(
    address: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isTablet: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = if (isTablet) 14.dp else 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Location",
            tint = colorDarkOrange,
            modifier = Modifier.size(if (isTablet) 22.dp else 20.dp)
        )

        Spacer(modifier = Modifier.width(if (isTablet) 14.dp else 12.dp))

        Text(
            text = address,
            fontFamily = SoraFontFamily,
            fontSize = if (isTablet) 16.sp else 14.sp,
            color = if (isSelected) colorDarkOrange else Color.White,
            modifier = Modifier.weight(1f)
        )

        if (isSelected) {
            Spacer(modifier = Modifier.width(if (isTablet) 10.dp else 8.dp))
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = colorDarkOrange,
                modifier = Modifier.size(if (isTablet) 18.dp else 16.dp)
            )
        }
    }

    Divider(
        color = Color(0xFF3A3A3A),
        thickness = 1.dp
    )
}



@Composable
fun CoffeeCategoryRow(
    onCoffeeTypeSelected: (Int?) -> Unit,
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val homeState by viewModel.uiState.collectAsState()

    val selectedItem = remember {
        mutableStateOf(viewModel.getCurrentSelectedType() ?: "Все кофе")
    }

    val isTablet = LocalConfiguration.current.screenWidthDp >= 600
    val fontSize = if (isTablet) 15.sp else 14.sp

    LaunchedEffect(viewModel.getCurrentSelectedType()) {
        viewModel.getCurrentSelectedType()?.let { type ->
            if (type != selectedItem.value) {
                selectedItem.value = type
            }
        }
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(end = 8.dp)
    ) {
        items(homeState.coffeeTypes) { item ->
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

                        if (item == "Все кофе") {
                            onCoffeeTypeSelected(null)
                        } else {
                            val typeId = homeState.coffeeTypeMapping[item]
                            onCoffeeTypeSelected(typeId)
                        }
                    }
            ) {
                Text(
                    text = item,
                    fontFamily = SoraFontFamily,
                    fontWeight = if (item == selectedItem.value) FontWeight.W600 else FontWeight.W400,
                    fontSize = fontSize,
                    lineHeight = 21.sp,
                    color = if (item == selectedItem.value) Color.White else Color(0xFF313131),
                )
            }
        }
    }
}

@Composable
fun CoffeeCategoryColumn(
    coffeeList: List<CoffeeResponse>,
    viewModel: HomeViewModel,
    navController: NavController,
    cartViewModel: CartViewModel,
    modifier: Modifier = Modifier
) {
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
                    CoffeeItem(
                        coffee = coffee,
                        viewModel = viewModel,
                        token = token,
                        navController = navController,
                        cartViewModel = cartViewModel
                    )
                }

                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun CoffeeItem(
    coffee: CoffeeResponse,
    viewModel: HomeViewModel,
    token: String,
    navController: NavController,
    cartViewModel: CartViewModel
) {
    val imageBytes by viewModel.imageCache[coffee.imageName]
        ?.let { bytes -> remember(bytes) { mutableStateOf(bytes) } }
        ?: remember { mutableStateOf<ByteArray?>(null) }

    val imagePainter = rememberAsyncImagePainter(
        model = if (imageBytes != null) {
            ImageRequest.Builder(LocalContext.current)
                .data(imageBytes)
                .build()
        } else {
            ImageRequest.Builder(LocalContext.current)
                .data("http://10.0.2.2:8080/api/coffee/image/${coffee.imageName}")
                .setHeader("Authorization", token)
                .build()
        }
    )

    val defaultPrice = remember(coffee) {
        viewModel.getDefaultPrice(coffee)
    }

    val isInCart by remember(cartViewModel.cartItems, coffee.id) {
        derivedStateOf {
            cartViewModel.cartItems.value.any { it.id == coffee.id }
        }
    }

    Card(
        modifier = Modifier
            .width(156.dp)
            .height(238.dp)
            .clickable {
                val sizesEncoded = viewModel.encodeSizesForNavigation(coffee)
                navController.navigate(
                    "${NavigationRoutes.DETAIL}/" +
                            "${coffee.id}/" +
                            "${coffee.name}/" +
                            "${coffee.type.type}/" +
                            "${coffee.description}/" +
                            "${coffee.imageName}" +
                            "?sizes=$sizesEncoded" +
                            "&favoriteSize="
                )
            },
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
                    text = "₽${"%.2f".format(defaultPrice)}",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 18.sp,
                    color = Color(0xFF2F2D2C)
                )

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = if (isInCart) Color.White else colorDarkOrange,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = if (isInCart) 2.dp else 0.dp,
                            color = if (isInCart) colorDarkOrange else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            viewModel.showSizeSelectionDialog(coffee)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isInCart) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "В корзине",
                            modifier = Modifier.size(18.dp),
                            tint = colorDarkOrange
                        )
                    } else {
                        Text(
                            text = "+",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SizeSelectionDialog(
    coffee: CoffeeResponse,
    onDismiss: () -> Unit,
    onSizeSelected: (String) -> Unit
) {
    var selectedSize by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Выберите размер",
                fontWeight = FontWeight.W600,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                Text(
                    text = coffee.name,
                    fontWeight = FontWeight.W500,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                coffee.sizes.forEach { size ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { selectedSize = size.size },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedSize == size.size,
                            onClick = { selectedSize = size.size },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = colorDarkOrange
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Размер ${size.size}",
                            fontWeight = FontWeight.W500,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "₽${"%.2f".format(size.price)}",
                            fontWeight = FontWeight.W600,
                            fontSize = 16.sp,
                            color = colorDarkOrange
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedSize?.let { onSizeSelected(it) }
                    onDismiss()
                },
                enabled = selectedSize != null
            ) {
                Text(
                    text = "Добавить в корзину",
                    fontWeight = FontWeight.W600,
                    color = if (selectedSize != null) colorDarkOrange else Color.Gray
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Отмена",
                    fontWeight = FontWeight.W500,
                    color = Color.Gray
                )
            }
        }
    )
}

data class BottomMenuItem(
    val label: String,
    val icon: Int,
    val selectedIndicator: Int,
    val route: String
)

@Composable
fun BottomMenuIcon(item: BottomMenuItem, isSelected: Boolean, onClick: () -> Unit) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    val iconSize = if (isTablet) 28.dp else 24.dp
    val indicatorSize = if (isTablet) 10.dp else 8.dp
    val horizontalPadding = if (isTablet) 12.dp else 8.dp
    val topPadding = if (isTablet) 6.dp else 4.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(horizontal = horizontalPadding)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = item.icon),
            contentDescription = item.label,
            modifier = Modifier.size(iconSize),
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
                modifier = Modifier
                    .size(indicatorSize)
                    .padding(top = topPadding)
            )
        }
    }
}

@Composable
fun BottomMenu(navController: NavController) {
    val menuItems = listOf(
        BottomMenuItem("Home", R.drawable.menu_home, R.drawable.selected_dot, NavigationRoutes.HOME),
        BottomMenuItem("Favorite", R.drawable.heart, R.drawable.selected_dot, NavigationRoutes.FAVORITE),
        BottomMenuItem("Cart", R.drawable.cart, R.drawable.selected_dot, NavigationRoutes.CART),
        BottomMenuItem("Profile", R.drawable.notification, R.drawable.selected_dot, NavigationRoutes.PROFILE)
    )

    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentRoute = currentDestination?.route

    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    val bottomBarHeight = if (isTablet) 110.dp else 99.dp
    val horizontalPadding = if (isTablet) 48.dp else 24.dp
    val verticalPadding = if (isTablet) 20.dp else 24.dp
    val cornerRadius = if (isTablet) 28.dp else 24.dp

    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(bottomBarHeight)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(
                    topStart = cornerRadius,
                    topEnd = cornerRadius
                )
            ),
        containerColor = Color.White,
        contentPadding = PaddingValues(
            horizontal = horizontalPadding,
            vertical = verticalPadding
        )
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
                    isSelected = currentRoute == item.route,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                // Очищаем back stack до корня
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Предотвращаем множественные копии экрана
                                launchSingleTop = true
                                // Восстанавливаем состояние
                                restoreState = true
                            }
                        }
                    }
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