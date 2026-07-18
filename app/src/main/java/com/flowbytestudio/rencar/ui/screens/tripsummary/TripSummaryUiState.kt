package com.flowbytestudio.rencar.ui.screens.tripsummary

import com.flowbytestudio.rencar.data.cards.CardDto
import com.flowbytestudio.rencar.data.rentals.PayRentalResponse
import com.flowbytestudio.rencar.data.rentals.RentalDto

/** Fatura ekranındaki ödeme yöntemi seçimi. API'ye WALLET/CARD/IYZICO olarak gider. */
enum class PaymentMethodOption { WALLET, CARD, IYZICO }

data class TripSummaryUiState(
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val rental: RentalDto? = null,

    // Ödeme bölümü (yalnız COMPLETED + UNPAID iken doldurulur)
    val walletBalance: Double? = null,
    val cards: List<CardDto> = emptyList(),
    val selectedMethod: PaymentMethodOption = PaymentMethodOption.WALLET,
    val selectedCardId: String? = null,
    val discountCode: String = "",
    val isPaying: Boolean = false,
    val payError: String? = null,

    // İyzico Checkout Form akışı: WebView açılırken dolar, sonuç gelene kadar sürer.
    val iyzicoCheckoutUrl: String? = null,
    val iyzicoToken: String? = null,

    // Bu oturumda ödeme alındığında dolan makbuz (paidAmount, kalan bakiye / kart).
    val receipt: PayRentalResponse? = null,
) {
    /** Kilitli toplam (indirim öncesi). PER_MINUTE/HOURLY finish sonrası dolar. */
    val totalAmount: Double?
        get() = rental?.totalPrice

    /**
     * Kullanım ücreti kalemi: finish cevabında usageFee doluysa onu, değilse
     * toplamdan açılış ve hizmet bedelini düşerek türetir.
     */
    val usageFee: Double?
        get() {
            val r = rental ?: return null
            r.usageFee?.let { return it }
            val total = r.totalPrice ?: return null
            return total - r.startFee - (r.serviceFee ?: 0.0)
        }

    /** Ödeme yapıldı mı? (bu oturumda ödendiyse ya da yüklenişte zaten PAID ise) */
    val isPaid: Boolean
        get() = receipt != null || rental?.paymentStatus == "PAID"

    /** Ödeme bölümü gösterilsin mi? */
    val isPayable: Boolean
        get() = receipt == null &&
            rental?.status == "COMPLETED" &&
            rental.paymentStatus == "UNPAID"

    /** Ödenecek tutar (kilitli toplam; indirim sunucuda uygulanır). */
    val payableAmount: Double?
        get() = rental?.totalPrice

    /** Cüzdan bakiyesi ödenecek tutarı karşılamıyor mu? */
    val walletInsufficient: Boolean
        get() {
            val balance = walletBalance ?: return false
            val payable = payableAmount ?: return false
            return balance < payable
        }

    /** Ödeme tuşu aktif mi? */
    val canPay: Boolean
        get() {
            if (payableAmount == null) return false
            return when (selectedMethod) {
                PaymentMethodOption.WALLET -> walletBalance != null && !walletInsufficient
                PaymentMethodOption.CARD -> selectedCardId != null
                PaymentMethodOption.IYZICO -> true
            }
        }
}
