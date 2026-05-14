package com.example.coffeeshop

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CoffeeShopApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("669d523d-1916-4697-86f8-bc46f40c51eb")
        MapKitFactory.initialize(this)
    }
}
