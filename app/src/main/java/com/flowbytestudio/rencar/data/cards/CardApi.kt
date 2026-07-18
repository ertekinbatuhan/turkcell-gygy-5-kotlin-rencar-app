package com.flowbytestudio.rencar.data.cards

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CardApi {

    // Öntanımlı kart en üstte, gerisi yeniden eskiye.
    @GET("cards")
    suspend fun getCards(): List<CardDto>

    @POST("cards")
    suspend fun createCard(@Body body: CreateCardRequest): CardDto

    @PATCH("cards/{id}/default")
    suspend fun setDefault(@Path("id") id: String): CardDto

    @DELETE("cards/{id}")
    suspend fun deleteCard(@Path("id") id: String)
}
