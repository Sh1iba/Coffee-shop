package com.example.coffeeshop.data.managers

import android.content.Context
import android.content.SharedPreferences

class LocationManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    private val locationKey = "selected_location"

    fun saveLocation(location: String) {
        sharedPreferences.edit()
            .putString(locationKey, location)
            .apply()
    }

    fun getSavedLocation(): String {
        return sharedPreferences.getString(locationKey, "Москва, Россия") ?: "Москва, Россия"
    }
    fun clearLocation() {
        sharedPreferences.edit()
            .remove(locationKey)
            .apply()
    }
}