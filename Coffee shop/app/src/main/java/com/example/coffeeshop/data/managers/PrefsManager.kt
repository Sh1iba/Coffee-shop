package com.example.coffeeshop.data.managers

import android.content.Context
import android.util.Log

class PrefsManager(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_TOKEN = "token"
        const val KEY_USER_ID = "userId"
        const val KEY_EMAIL = "email"
        const val KEY_NAME = "name"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_FIRST_LAUNCH = "is_first_launch"
        const val KEY_SAVED_ADDRESS = "saved_address" // Добавляем ключ для адреса
    }
    // Методы для работы с адресом
    fun saveAddress(address: String) {
        sharedPreferences.edit().putString(KEY_SAVED_ADDRESS, address).apply()
        Log.d("PrefsManager", "Адрес сохранен: $address")
    }

    fun getSavedAddress(): String {
        return sharedPreferences.getString(KEY_SAVED_ADDRESS, "") ?: ""
    }
    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun setFirstLaunchCompleted() {
        sharedPreferences.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
        Log.d("PrefsManager", "Первый запуск завершен")
    }

    fun saveUserData(token: String, userId: Long, email: String, name: String) {
        sharedPreferences.edit().apply {
            putString(KEY_TOKEN, token)
            putLong(KEY_USER_ID, userId)
            putString(KEY_EMAIL, email)
            putString(KEY_NAME, name)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
        Log.d("PrefsManager", "Данные пользователя сохранены: userId=$userId, name=$name")
    }

    fun getToken(): String? = sharedPreferences.getString(KEY_TOKEN, null)

    fun getUserId(): Long = sharedPreferences.getLong(KEY_USER_ID, -1L)

    fun getEmail(): String? = sharedPreferences.getString(KEY_EMAIL, null)

    fun getName(): String? = sharedPreferences.getString(KEY_NAME, null)

    fun isLoggedIn(): Boolean = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false) && getToken() != null

    fun logout() {
        sharedPreferences.edit().apply {
            remove(KEY_TOKEN)
            remove(KEY_USER_ID)
            remove(KEY_EMAIL)
            remove(KEY_NAME)
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
        Log.d("PrefsManager", "Пользователь вышел из системы")
    }

    fun getUserData(): UserData? {
        return if (isLoggedIn()) {
            UserData(
                token = getToken()!!,
                userId = getUserId(),
                email = getEmail()!!,
                name = getName() ?: ""
            )
        } else {
            null
        }
    }
}

data class UserData(
    val token: String,
    val userId: Long,
    val email: String,
    val name: String
)