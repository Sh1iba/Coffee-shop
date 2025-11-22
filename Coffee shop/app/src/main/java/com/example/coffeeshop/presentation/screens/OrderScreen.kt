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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
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
import com.example.coffeeshop.data.managers.PrefsManager
import com.example.coffeeshop.data.remote.api.ApiClient
import com.example.coffeeshop.data.remote.response.CoffeeResponse
import com.example.coffeeshop.data.remote.response.CoffeeSizeResponse
import com.example.coffeeshop.data.remote.response.CoffeeTypeResponse
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


@Composable
fun OrderScreen() {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = { OrderTopBar() },
        bottomBar = { BottomOrderPanel(walletBalance = "$5.53") }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 24.dp)
        ) {
            OrderContent()
        }
    }
}

@Composable
fun OrderContent() {

    val selectedButton = remember { mutableStateOf("Доставка") }

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
                        color = if (selectedButton.value == "Доставка") colorDarkOrange else Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { selectedButton.value = "Доставка" }
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                Text(
                    text = "Доставка",
                    fontFamily = SoraFontFamily,
                    fontWeight = if (selectedButton.value == "Доставка") FontWeight.W600 else FontWeight.W400,
                    fontSize = 16.sp,
                    lineHeight = 21.sp,
                    color = if (selectedButton.value == "Доставка") Color.White else Color(0xFF313131),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }

            Box(
                modifier = Modifier
                    .padding(all = 4.dp)
                    .background(
                        color = if (selectedButton.value == "Забрать") colorDarkOrange else Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { selectedButton.value = "Забрать" }
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                Text(
                    text = "Забрать",
                    fontFamily = SoraFontFamily,
                    fontWeight = if (selectedButton.value == "Забрать") FontWeight.W600 else FontWeight.W400,
                    fontSize = 16.sp,
                    lineHeight = 21.sp,
                    color = if (selectedButton.value == "Забрать") Color.White else Color(0xFF313131),
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

            Box(
                modifier = Modifier.wrapContentSize()
            ) {
                Text(
                    text = "Адрес Доставки",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }

            Box {
                Text(
                    text = "Жуковский",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }

            Row(
                modifier = Modifier.wrapContentWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OutlinedButton(
                    onClick = { },
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
                    onClick = { },
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

            CoffeeCard(
                coffeeName = "Caffe Mocha",
                coffeeType = "Deep Foam",
                coffeeDescription = "L",
                coffeeImage = painterResource(id = R.drawable.banner),
                quantity = 5
            )
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

        val price = 4.53
        val deliveryFee = 2.0
        val totalPrice = price + deliveryFee
        val walletBalance = 5.53

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
                    text = "Цена",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W400,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = Color.Gray
                )
                Text(
                    text = "$${price}",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                    text = "$${deliveryFee}",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}


@Composable
fun OrderTopBar() {
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
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.leftarrow),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = "Заказ",
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
    onOrderClick: () -> Unit = {}
) {
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
                        painter = painterResource(id = R.drawable.heart),
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
                    text = "Заказать",
                    fontWeight = FontWeight.W600,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun CoffeeCard(
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
                modifier = Modifier .size(54.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Column() {

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

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Размер: $coffeeDescription",
                    fontSize = 14.sp, color = Color.Gray )
            }

            Spacer(modifier = Modifier.width(70.dp))

            Text(
                text = "$quantity",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(end = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "OrderScreenPreview")
@Composable
fun OrderScreenPreview(){
    OrderScreen()
}