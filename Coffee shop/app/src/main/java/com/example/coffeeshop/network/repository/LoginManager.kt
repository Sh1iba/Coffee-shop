package com.example.coffeeshop.network.repository

import android.util.Log
import com.example.coffeeshop.network.api.ApiClient
import com.example.coffeeshop.network.model.login.LoginRequest
import com.example.coffeeshop.network.model.login.LoginResponse


class LoginManager {
        private val apiService = ApiClient.coffeeApi

        suspend fun loginUser( login: String, password: String): Result<LoginResponse> {
            return try {
                Log.d("Login", "Пытаемся войти в аккаунт: $login")

                val request = LoginRequest(login, password)
                val response = apiService.loginUser(request)

                Log.d("Login", "Получен ответ: ${response.code()}")

                when {
                    response.isSuccessful -> {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            Log.d("Login", "Успешный вход. Токен: ${responseBody.token}")
                            Result.success(responseBody)
                        } else {
                            Log.e("Login", "Пустой ответ от сервера")
                            Result.failure(Exception("Empty response (${response.code()})"))
                        }
                    }
                    else -> {
                        val errorBody = response.errorBody()?.string() ?: "No error body"
                        Log.e("Login", "Ошибка ${response.code()}: $errorBody")
                        Result.failure(Exception("${response.code()}: $errorBody"))
                    }
                }
            } catch (e: Exception) {
                Log.e("Login", "Ошибка сети: ${e.message}", e)
                Result.failure(e)
            }
        }
}
