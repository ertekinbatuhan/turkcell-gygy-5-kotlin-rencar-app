package com.flowbytestudio.rencar.data.iyzico

import com.flowbytestudio.rencar.data.network.NetworkModule

class IyzicoRepository(
    private val api: IyzicoApi = NetworkModule.iyzicoApi,
) {

    // Kiralama ödemesi için basketId sabit desendedir: backend /rentals/:id/pay (IYZICO)
    // doğrulaması bu ID ile ödemeyi arar.
    suspend fun initializeCheckoutForm(rentalId: String, amount: Double): Result<CheckoutFormInitializeResponse> =
        runCatching {
            api.initializeCheckoutForm(
                InitializeCheckoutFormRequest(
                    price = amount,
                    description = "RenCar yolculuk ödemesi",
                    basketId = "rental-$rentalId",
                ),
            )
        }

    suspend fun getCheckoutFormResult(token: String): Result<IyzicoPaymentResponse> = runCatching {
        api.getCheckoutFormResult(token)
    }

    // 3-D Secure YOK: kart bilgisi burada toplanır, cevap senkron gelir.
    suspend fun createPayment(
        rentalId: String,
        amount: Double,
        card: IyzicoCardRequest,
    ): Result<IyzicoPaymentResponse> = runCatching {
        api.createPayment(
            CreateIyzicoPaymentRequest(
                price = amount,
                description = "RenCar yolculuk ödemesi",
                basketId = "rental-$rentalId",
                card = card,
            ),
        )
    }

    suspend fun initializeThreeds(
        rentalId: String,
        amount: Double,
        card: IyzicoCardRequest,
    ): Result<ThreedsInitializeResponse> = runCatching {
        api.initializeThreeds(
            CreateIyzicoPaymentRequest(
                price = amount,
                description = "RenCar yolculuk ödemesi",
                basketId = "rental-$rentalId",
                card = card,
            ),
        )
    }
}
