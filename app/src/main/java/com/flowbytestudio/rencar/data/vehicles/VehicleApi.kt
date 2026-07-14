package com.flowbytestudio.rencar.data.vehicles

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface VehicleApi {

    @GET("vehicles")
    suspend fun getVehicles(
        @Query("type") type: String? = null,
        @Query("segment") segment: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        // true ise RENTED + RESERVED araçlar da döner (harita gri marker'ları);
        // MAINTENANCE hiçbir zaman dönmez.
        @Query("includeBusy") includeBusy: Boolean? = null,
    ): List<VehicleDto>

    @GET("vehicles/{id}")
    suspend fun getVehicle(@Path("id") id: String): VehicleDto

    @GET("vehicles/{id}/quote")
    suspend fun getQuote(
        @Path("id") id: String,
        @Query("plan") plan: String,
        @Query("minutes") minutes: Int,
    ): QuoteResponse
}
