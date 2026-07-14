package com.flowbytestudio.rencar.data.reservations

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReservationApi {

    // Aracı 15 dk ücretsiz tutar (RESERVATION_TTL_MIN sunucu konfigürasyonu).
    @POST("reservations")
    suspend fun createReservation(@Body body: CreateReservationRequest): ReservationResponse

    @GET("reservations/active")
    suspend fun getActiveReservation(): ReservationResponse

    @DELETE("reservations/{id}")
    suspend fun cancelReservation(@Path("id") id: String)
}
