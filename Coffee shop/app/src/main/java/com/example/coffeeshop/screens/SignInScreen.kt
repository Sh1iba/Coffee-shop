package com.example.coffeeshop.screens


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffeeshop.ui.theme.SoraFontFamily
import com.example.coffeeshop.ui.theme.colorFoundationGrey
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.coffeeshop.R
import com.example.coffeeshop.navigation.NavigationRoutes
import com.example.coffeeshop.network.repository.LoginManager
import com.example.coffeeshop.network.repository.RegistrationManager
import com.example.coffeeshop.ui.theme.CoffeeShopTheme
import com.example.coffeeshop.ui.theme.colorBackgroudWhite
import com.example.coffeeshop.ui.theme.colorDarkOrange
import com.example.coffeeshop.ui.theme.colorLightGrey
import com.example.coffeeshop.ui.theme.colorLightRecGrey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SignInScreen(navController: NavController) {
    var darkTheme by remember { mutableStateOf(false) }

    CoffeeShopTheme(darkTheme = darkTheme) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dark theme",
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(end = 8.dp)
            )
            Switch(
                checked = darkTheme,
                onCheckedChange = { darkTheme = it }
            )
        }

        Column(
            modifier = Modifier
                .padding(top = 196.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Войти в Аккаунт",
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.W600,
                fontSize = 20.sp,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .wrapContentSize()

            )

            Text(
                text = "С возвращением, мы скучали по тебе.",
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 16.sp,
                lineHeight = 19.2.sp,
                color = colorLightGrey,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.1f.dp)

            )
        }

        var login by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }

        Text(
            text = "Логин",
            fontFamily = SoraFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .offset(x = 24.dp, y = 305.dp)
        )

        Box(
            modifier = Modifier
                .padding(start = 22.dp, top = 330.dp, end = 22.dp)
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colorLightRecGrey),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = login,
                onValueChange = { login = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 16.sp,
                    lineHeight = 16.sp
                ),
                singleLine = true
            )
        }

        Text(
            text = "Пароль",
            fontFamily = SoraFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .width(62.dp)
                .height(24.dp)
                .offset(x = 24.dp, y = 373.dp)
        )

        Box(
            modifier = Modifier
                .padding(start = 22.dp, top = 399.dp, end = 22.dp)
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colorLightRecGrey),
            contentAlignment = Alignment.CenterStart

        ) {
            BasicTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 16.sp,
                    lineHeight = 16.sp
                ),
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                singleLine = true
            )

            IconButton(
                onClick = { passwordVisible = !passwordVisible },
                modifier = Modifier
                    .size(48.dp)
                    .align(alignment = Alignment.CenterEnd)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (passwordVisible)
                            R.drawable.eye_on_foreground
                        else
                            R.drawable.eye_off_foreground
                    ),
                    contentDescription = if (passwordVisible)
                        "Скрыть пароль"
                    else
                        "Показать пароль",
                    tint = colorFoundationGrey
                )
            }


        }

        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "забыли Пароль?",
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                color = colorDarkOrange,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 22.dp,top = 441.dp)
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 39.dp, top = 473.dp, end = 39.dp),
            thickness = 1.dp,
            color = Color(0xFFE3E3E3)
        )
        val loginManager = remember { LoginManager() }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(false) }
        val context = LocalContext.current

        LaunchedEffect(errorMessage) {
            errorMessage?.let { message ->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                errorMessage = null
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 489.dp, end = 24.dp)

        ) {
            Button(
                onClick = {
                    if (login.isEmpty() || password.isEmpty()) {
                        errorMessage = "Все поля должны быть заполнены"
                        Log.d("Login", "Все поля должны быть заполнены")
                        return@Button
                    }
                    errorMessage = null
                    isLoading = true

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            Log.d("Login", "Выполняется вход...")
                            val result = loginManager.loginUser(login, password)

                            withContext(Dispatchers.Main) {
                                isLoading = false

                                when {
                                    result.isSuccess -> {
                                        navController.navigate(NavigationRoutes.HOME) {
                                            popUpTo(NavigationRoutes.SIGN_IN) { inclusive = true }
                                        }
                                        Log.d("Registration", "Успещный вход!")
                                    }

                                    else -> {
                                        val error = result.exceptionOrNull()
                                        errorMessage = when {
                                            error?.message?.contains("400") == true -> "Пользователь не найден"
                                            error?.message?.contains("409") == true -> "Неверный пароль"
                                            error?.message?.contains("500") == true -> "Ошибка сервера"
                                            else -> "Ошибка: ${error?.message ?: "Неизвестная ошибка"}"
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                isLoading = false
                                errorMessage = "Ошибка сети: ${e.message}"
                                Log.e("Registration", "Ошибка в корутине", e)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorDarkOrange
                )
            ) {
                Text(
                    text = "Войти",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = Color.White,
                    modifier = Modifier
                        .width(57.dp)
                        .height(24.dp)
                )
            }
        }

        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 710.dp),
            thickness = 4.dp,
            color = Color(0xFFF9F2ED)
        )

        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary
                    )
                ) {
                    append("У вас нет учетной записи? ")
                }
                withStyle(
                    style = SpanStyle(
                        color = colorDarkOrange,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append("Создать")
                }
            },
            fontFamily = SoraFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 16.sp,
            lineHeight = 19.2.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(19.dp)
                .clickable {
                navController.navigate(NavigationRoutes.REGISTRATION) {
                    popUpTo(NavigationRoutes.SIGN_IN) { inclusive = true }
                }
            }
        )
        }
    }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "pre")
@Composable
fun SignInScreenPreview() {
    val navController = rememberNavController()
    SignInScreen(navController = navController)
}