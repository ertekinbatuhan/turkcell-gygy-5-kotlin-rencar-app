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
}
