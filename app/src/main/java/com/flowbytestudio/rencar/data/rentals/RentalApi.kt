package com.flowbytestudio.rencar.data.rentals

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RentalApi {

    @GET("rentals")
    suspend fun getMyRentals(): List<RentalDto>

    @POST("rentals")
    suspend fun createRental(@Body body: CreateRentalRequest): RentalDto
}
