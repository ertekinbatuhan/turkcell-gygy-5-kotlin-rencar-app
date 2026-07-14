package com.flowbytestudio.rencar.data.network

import com.flowbytestudio.rencar.data.auth.AuthApi
import com.flowbytestudio.rencar.data.auth.AuthSession
import com.flowbytestudio.rencar.data.license.LicenseApi
import com.flowbytestudio.rencar.data.rentals.RentalApi
import com.flowbytestudio.rencar.data.vehicles.VehicleApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object NetworkModule {

    private const val BASE_URL = "https://rencarv2.halitkalayci.com/"

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    private val authInterceptor = okhttp3.Interceptor { chain ->
        val request = chain.request().newBuilder().apply {
            AuthSession.accessToken?.let { token ->
                addHeader("Authorization", "Bearer $token")
            }
        }.build()
        chain.proceed(request)
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val vehicleApi: VehicleApi by lazy { retrofit.create(VehicleApi::class.java) }
    val rentalApi: RentalApi by lazy { retrofit.create(RentalApi::class.java) }
    val licenseApi: LicenseApi by lazy { retrofit.create(LicenseApi::class.java) }
}
