package com.flowbytestudio.rencar.data.rentals

import retrofit2.http.GET

interface RentalApi {

    @GET("rentals")
    suspend fun getMyRentals(): List<RentalDto>
}
