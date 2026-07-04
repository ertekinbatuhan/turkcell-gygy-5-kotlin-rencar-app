package com.flowbytestudio.rencar.data.vehicles

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface VehicleApi {

    @GET("vehicles")
    suspend fun getVehicles(
        @Query("type") type: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
    ): List<VehicleDto>

    @GET("vehicles/{id}")
    suspend fun getVehicle(@Path("id") id: String): VehicleDto
}
