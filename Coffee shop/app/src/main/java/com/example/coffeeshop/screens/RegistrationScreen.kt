@file:Suppress("ComposePreviewMustBeTopLevelFunction")

package com.example.coffeeshop.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.blur
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
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.coffeeshop.R
import com.example.coffeeshop.navigation.NavigationRoutes
import com.example.coffeeshop.network.api.ApiClient
import com.example.coffeeshop.network.model.request.RegisterRequest
import com.example.coffeeshop.network.model.response.ErrorResponse
import com.example.coffeeshop.parser.ErrorParser
import com.example.coffeeshop.ui.theme.colorBackgroudWhite
import com.example.coffeeshop.ui.theme.colorDarkOrange
import com.example.coffeeshop.ui.theme.colorLightGrey
import com.example.coffeeshop.ui.theme.colorLightRecGrey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

@Composable
fun RegistrationScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorBackgroudWhite)
    ) {

        Text(
            text = "Создать Аккаунт",
            fontFamily = SoraFontFamily,
            fontWeight = FontWeight.W600,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            color = colorFoundationGrey,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(162.dp)
                .height(24.dp)
                .offset(x = 107.dp, y = 163.dp)
        )


        Text(
            text = "Добавьте информацию о себе.",
            fontFamily = SoraFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 16.sp,
            lineHeight = 19.2.sp,
            color = colorLightGrey,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(300.dp)
                .height(19.dp)
                .offset(x = 38.dp, y = 203.dp)
        )
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }

        Text(
            text = "Имя",
            fontFamily = SoraFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            color = colorFoundationGrey,
            modifier = Modifier
                .width(49.dp)
                .height(24.dp)
                .offset(x = 24.dp, y = 267.dp)
        )

        Box(
            modifier = Modifier
                .padding(start = 22.dp, top = 291.dp, end = 22.dp)
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colorLightRecGrey),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = name,
                onValueChange = { name = it },
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
            text = "Почта",
            fontFamily = SoraFontFamily,
            fontWeight = FontWeight.W400,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            color = colorFoundationGrey,
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .offset(x = 24.dp, y = 336.dp)
        )

        Box(
            modifier = Modifier
                .padding(start = 22.dp, top = 360.dp, end = 22.dp)
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colorLightRecGrey),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = email,
                onValueChange = { email = it },
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
            color = colorFoundationGrey,
            modifier = Modifier
                .width(62.dp)
                .height(24.dp)
                .offset(x = 24.dp, y = 403.dp)
        )

        Box(
            modifier = Modifier
                .padding(start = 22.dp, top = 427.dp, end = 22.dp)
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



        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 39.dp, top = 507.dp, end = 39.dp),
            thickness = 1.dp,
            color = Color(0xFFE3E3E3)
        )


        val errorParser = remember { ErrorParser() }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(false) }
        val context = LocalContext.current
        var showSuccessDialog by remember { mutableStateOf(false) }

        LaunchedEffect(errorMessage) {
            errorMessage?.let { message ->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                errorMessage = null
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, top = 523.dp, end = 24.dp)
        ) {
            Button(
                onClick = {
                    if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                        errorMessage = "Все поля должны быть заполнены"
                        Log.d("Registration", "Все поля должны быть заполнены")
                        return@Button
                    }

                    errorMessage = null
                    isLoading = true

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            Log.d("Registration", "Начало регистрации...")

                            val request = RegisterRequest(email, password, name)
                            val response = ApiClient.coffeeApi.registerUser(request)

                            withContext(Dispatchers.Main) {
                                isLoading = false

                                when {
                                    response.isSuccessful -> {
                                        val responseBody = response.body()
                                        if (responseBody != null) {
                                            Log.d("Registration", "Успешная регистрация. UserID: ${responseBody.userID}, " +
                                                    "Email: ${responseBody.email}, " +
                                                    "Name: ${responseBody.name}")
                                            showSuccessDialog = true
                                        } else {
                                            errorMessage = "Пустой ответ от сервера"
                                            Log.e("Registration", "Пустой ответ от сервера (${response.code()})")
                                        }
                                    }
                                    else -> {
                                        val errorBody = response.errorBody()?.string() ?: "No error body"
                                        Log.e("Registration", "Ошибка ${response.code()}: $errorBody")
                                        errorMessage = errorParser.parseErrorMessage(errorBody) ?: "Неизвестная ошибка"
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
                    text = "Зарегистрироваться",
                    fontFamily = SoraFontFamily,
                    fontWeight = FontWeight.W600,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = Color.White
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
                            color = colorFoundationGrey
                        )
                    ) {
                        append("У вас уже есть учетная запись? ")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = colorDarkOrange,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("Войти")
                    }
                },
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.W400,
                fontSize = 16.sp,
                lineHeight = 19.2.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable {
                    navController.navigate(NavigationRoutes.SIGN_IN) {
                        popUpTo(NavigationRoutes.REGISTRATION) { inclusive = true }
                    }
                }


            )


        }
        if(showSuccessDialog){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 1f))

            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(start = 24.dp, end = 24.dp, top = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.success),
                        contentDescription = "Success",
                        modifier = Modifier.size(100.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Поздравляю!",
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W600,
                        fontSize = 20.sp,
                        color = Color.Black
                    )

                    Text(
                        text = "Регистрация прошла успешно.",
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.W400,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            navController.navigate(NavigationRoutes.SIGN_IN) {
                                popUpTo(NavigationRoutes.REGISTRATION) { inclusive = true }
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
                            text = "OK",
                            color = Color.White,
                            fontFamily = SoraFontFamily,
                            fontWeight = FontWeight.W600,
                            fontSize = 16.sp
                        )
                    }
                } }
        } }


}

@Preview(showBackground = true, showSystemUi = true, name = "pre")
@Composable
fun RegistrationScreenPreview() {
    RegistrationScreen(navController = rememberNavController())
}