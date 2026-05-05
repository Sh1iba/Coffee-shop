package com.example.coffeeshop.data.repository

import com.example.coffeeshop.data.managers.ErrorParser
import com.example.coffeeshop.data.managers.PrefsManager
import com.example.coffeeshop.data.remote.api.ApiService
import com.example.coffeeshop.domain.LoginRequest
import com.example.coffeeshop.domain.RegisterRequest
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val prefsManager: PrefsManager,
    private val errorParser: ErrorParser
) {
    suspend fun register(email: String, password: String, name: String, role: String = "BUYER"): AuthResult {
        return try {
            val response = apiService.registerUser(RegisterRequest(email, password, name, role))
            if (response.isSuccessful) AuthResult.Success
            else {
                val msg = errorParser.parseErrorMessage(response.errorBody()?.string() ?: "") ?: "Ошибка регистрации"
                AuthResult.Error(msg)
            }
        } catch (e: Exception) {
            AuthResult.Error("Ошибка сети: ${e.message}")
        }
    }

    suspend fun login(email: String, password: String): AuthResult {
        return try {
            val response = apiService.loginUser(LoginRequest(email, password))
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    prefsManager.saveUserData(
                        token = body.token,
                        userId = body.userId,
                        email = body.email,
                        name = body.name,
                        role = body.role
                    )
                }
                AuthResult.Success
            } else {
                val msg = errorParser.parseErrorMessage(response.errorBody()?.string() ?: "") ?: "Ошибка входа: ${response.code()}"
                AuthResult.Error(msg)
            }
        } catch (e: Exception) {
            AuthResult.Error("Ошибка сети: ${e.message}")
        }
    }
}
