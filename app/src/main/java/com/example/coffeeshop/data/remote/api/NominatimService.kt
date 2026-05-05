package com.example.coffeeshop.data.remote.api

import com.example.coffeeshop.data.remote.response.NominatimAddress
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimService {
    @GET("search")
    suspend fun searchAddress(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 15,
        @Query("countrycodes") countryCodes: String = "ru",
        @Query("addressdetails") addressDetails: Int = 1,
        @Query("accept-language") language: String = "ru"
    ): List<NominatimAddress>
}