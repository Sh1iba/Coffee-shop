package com.example.coffeeshop.data.remote.interceptor

import com.example.coffeeshop.data.managers.PrefsManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val prefsManager: PrefsManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = prefsManager.getToken()
        val request = if (token != null) {
            val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"
            chain.request().newBuilder()
                .addHeader("Authorization", authHeader)
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
