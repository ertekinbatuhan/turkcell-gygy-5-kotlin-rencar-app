package com.flowbytestudio.rencar.data.iyzico

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface IyzicoApi {

    @POST("iyzico/checkout-form/initialize")
    suspend fun initializeCheckoutForm(@Body body: InitializeCheckoutFormRequest): CheckoutFormInitializeResponse

    @GET("iyzico/checkout-form/result/{token}")
    suspend fun getCheckoutFormResult(@Path("token") token: String): IyzicoPaymentResponse
}

@Serializable
data class InitializeCheckoutFormRequest(
    val price: Double,
    val description: String,
    // Backend POST /rentals/:id/pay (IYZICO) doğrulamasında bu basketId'yi arar.
    val basketId: String,
)

@Serializable
data class CheckoutFormInitializeResponse(
    val status: String,
    val token: String,
    val paymentPageUrl: String? = null,
)

@Serializable
data class IyzicoPaymentResponse(
    val status: String,
    val paymentId: String? = null,
    // SUCCESS | FAILURE | INIT_THREEDS | CALLBACK_THREEDS
    val paymentStatus: String? = null,
)
