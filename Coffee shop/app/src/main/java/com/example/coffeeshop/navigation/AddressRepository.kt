package com.example.coffeeshop.navigation

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AddressRepository {

    private val api: NominatimService by lazy {
        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")  // Базовый URL API
            .addConverterFactory(GsonConverterFactory.create()) // Конвертер JSON в объекты
            .build()
            .create(NominatimService::class.java)
    }

    // Функция для поиска адреса
    suspend fun searchAddress(query: String): List<NominatimAddress> {
        return try {
            // Делаем запрос к API
            api.searchAddress(query)
        } catch (e: Exception) {
            // В случае ошибки возвращаем пустой список
            emptyList()
        }
    }
}