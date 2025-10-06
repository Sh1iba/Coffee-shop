package com.example.coffeeshop.network.repository

import android.util.Log
import com.example.coffeeshop.network.api.ApiClient
import com.example.coffeeshop.network.model.register.RegisterRequest
import com.example.coffeeshop.network.model.register.RegisterResponse

class RegistrationManager {
    private val apiService = ApiClient.coffeeApi

    suspend fun registerUser( login: String, email: String, password: String): Result<RegisterResponse> {
        return try {
            Log.d("Registration", "Пытаемся зарегистрировать пользователя: $login")
            val request = RegisterRequest(login, email, password)
            val response = apiService.registerUser(request)

            Log.d("Registration", "Получен ответ: ${response.code()}")

            when {
                response.isSuccessful -> {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Log.d("Registration", "Успешная регистрация. Токен: ${responseBody.token}")
                        Result.success(responseBody)
                    } else {
                        Log.e("Registration", "Пустой ответ от сервера")
                        Result.failure(Exception("Empty response (${response.code()})"))
                    }
                }
                else -> {
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    Log.e("Registration", "Ошибка ${response.code()}: $errorBody")
                    Result.failure(Exception("${response.code()}: $errorBody"))
                }
            }
        } catch (e: Exception) {
            Log.e("Registration", "Ошибка сети: ${e.message}", e)
            Result.failure(e)
        }
    }
}