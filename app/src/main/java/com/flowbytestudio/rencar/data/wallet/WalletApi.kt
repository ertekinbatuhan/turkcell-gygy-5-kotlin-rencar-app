package com.flowbytestudio.rencar.data.wallet

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface WalletApi {

    @GET("wallet")
    suspend fun getWallet(): WalletResponse

    @POST("wallet/topup")
    suspend fun topup(@Body body: TopupRequest): WalletResponse
}
