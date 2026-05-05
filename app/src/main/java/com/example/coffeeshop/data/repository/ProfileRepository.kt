package com.example.coffeeshop.data.repository

import com.example.coffeeshop.data.managers.PrefsManager
import com.example.coffeeshop.data.remote.api.ApiService
import com.example.coffeeshop.data.remote.response.LoginResponse
import com.example.coffeeshop.domain.UpdateProfileRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val apiService: ApiService,
    private val prefsManager: PrefsManager
) {
    suspend fun getProfile(): LoginResponse? = try {
        val r = apiService.getProfile()
        if (r.isSuccessful) {
            r.body()?.also { profile ->
                prefsManager.saveUserData(profile.token, profile.userId, profile.email, profile.name)
            }
        } else null
    } catch (e: Exception) { null }

    suspend fun updateProfile(request: UpdateProfileRequest): LoginResponse? = try {
        val r = apiService.updateProfile(request)
        if (r.isSuccessful) {
            r.body()?.also { profile ->
                prefsManager.saveUserData(profile.token, profile.userId, profile.email, profile.name)
            }
        } else null
    } catch (e: Exception) { null }
}
