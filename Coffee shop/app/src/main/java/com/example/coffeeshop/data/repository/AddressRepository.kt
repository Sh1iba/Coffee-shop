package com.example.coffeeshop.data.repository

import com.example.coffeeshop.data.remote.response.NominatimAddress
import com.example.coffeeshop.data.remote.api.NominatimService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class AddressRepository {
    private val api: NominatimService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("User-Agent", "CoffeeShopApp/1.0")
                    .header("Accept", "application/json")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NominatimService::class.java)
    }

    suspend fun searchAddress(query: String): List<NominatimAddress> {
        return try {
            println("Searching for: '$query'")
            val results = api.searchAddress(query)
            println("Found ${results.size} results")
            results.forEach {
                println("   - ${it.display_name}")
            }
            results
        } catch (e: Exception) {
            println("API Error: ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }
}