package com.flowbytestudio.rencar.data.rentals

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface RentalApi {

    @GET("rentals")
    suspend fun getMyRentals(): List<RentalDto>

    @POST("rentals")
    suspend fun createRental(@Body body: CreateRentalRequest): RentalDto

    @GET("rentals/stats")
    suspend fun getStats(@Query("month") month: String? = null): RentalStatsResponse

    @GET("rentals/active")
    suspend fun getActiveRental(): RentalDto

    @GET("rentals/{id}")
    suspend fun getRental(@Path("id") id: String): RentalDto

    @Multipart
    @POST("rentals/{id}/photos")
    suspend fun uploadPhoto(
        @Path("id") id: String,
        @Part("side") side: RequestBody,
        @Part file: MultipartBody.Part,
    ): RentalPhotosState

    @GET("rentals/{id}/photos")
    suspend fun getPhotos(@Path("id") id: String): RentalPhotosState

    @POST("rentals/{id}/start")
    suspend fun startRental(@Path("id") id: String): RentalDto

    // Yalnız PREPARING aşamasındaki yolculuğu iptal eder.
    @DELETE("rentals/{id}")
    suspend fun cancelRental(@Path("id") id: String)

    @POST("rentals/{id}/finish")
    suspend fun finishRental(@Path("id") id: String): RentalDto

    // ESKİ uç — yalnız DAILY planda çalışır; dk/sa planında 409 döner.
    @POST("rentals/{id}/return")
    suspend fun returnRental(@Path("id") id: String): RentalDto

    @POST("rentals/{id}/pay")
    suspend fun payRental(@Path("id") id: String, @Body body: PayRentalRequest): PayRentalResponse
}
