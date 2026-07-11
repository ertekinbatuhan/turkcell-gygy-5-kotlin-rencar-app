package com.flowbytestudio.rencar.data.rentals

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RentalApi {

    @GET("rentals")
    suspend fun getMyRentals(): List<RentalDto>

    @POST("rentals")
    suspend fun createRental(@Body body: CreateRentalRequest): RentalDto

    @GET("rentals/{id}")
    suspend fun getRental(@Path("id") id: String): RentalDto

    @POST("rentals/{id}/return")
    suspend fun returnRental(@Path("id") id: String): RentalDto
}
