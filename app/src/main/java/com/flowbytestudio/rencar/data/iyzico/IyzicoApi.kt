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

    // Kart bilgisiyle tek adımda tahsilat; 3-D Secure YOKTUR, cevap senkron gelir.
    @POST("iyzico/payments")
    suspend fun createPayment(@Body body: CreateIyzicoPaymentRequest): IyzicoPaymentResponse

    // Banka doğrulama sayfasını (threeDSHtmlContentDecoded) döner; WebView'a yüklenir.
    @POST("iyzico/payments/threeds/initialize")
    suspend fun initializeThreeds(@Body body: CreateIyzicoPaymentRequest): ThreedsInitializeResponse
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
data class IyzicoCardRequest(
    val cardHolderName: String,
    val cardNumber: String,
    val expireMonth: String,
    val expireYear: String,
    val cvc: String,
)

@Serializable
data class CreateIyzicoPaymentRequest(
    val price: Double,
    val description: String,
    // Backend POST /rentals/:id/pay (IYZICO) doğrulamasında bu basketId'yi arar.
    val basketId: String,
    val card: IyzicoCardRequest,
)

@Serializable
data class IyzicoPaymentResponse(
    val status: String,
    val paymentId: String? = null,
    // SUCCESS | FAILURE | INIT_THREEDS | CALLBACK_THREEDS
    val paymentStatus: String? = null,
)

@Serializable
data class ThreedsInitializeResponse(
    val status: String,
    val conversationId: String? = null,
    val threeDSHtmlContentDecoded: String? = null,
)
