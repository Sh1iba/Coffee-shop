package com.example.coffeeshop.navigation


data class NominatimAddress(
    val display_name: String,  // Полное название адреса
    val lat: String,           // Широта
    val lon: String            // Долгота
)