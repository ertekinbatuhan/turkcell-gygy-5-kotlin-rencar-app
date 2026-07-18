package com.flowbytestudio.rencar.ui.screens.tripsummary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowbytestudio.rencar.data.cards.CardRepository
import com.flowbytestudio.rencar.data.iyzico.IyzicoRepository
import com.flowbytestudio.rencar.data.rentals.RentalRepository
import com.flowbytestudio.rencar.data.wallet.WalletRepository
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class TripSummaryViewModel(
    private val rentalId: String,
    private val rentalRepository: RentalRepository = RentalRepository(),
    private val walletRepository: WalletRepository = WalletRepository(),
    private val cardRepository: CardRepository = CardRepository(),
    private val iyzicoRepository: IyzicoRepository = IyzicoRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripSummaryUiState())
    val uiState: StateFlow<TripSummaryUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loadError = null) }
            rentalRepository.getRental(rentalId)
                .onSuccess { rental ->
                    _uiState.update { it.copy(isLoading = false, rental = rental) }
                    // Ödeme alınabilir durumdaysa cüzdan bakiyesini ve kayıtlı kartları getir.
                    if (rental.status == "COMPLETED" && rental.paymentStatus == "UNPAID") {
                        loadPaymentSources()
                    }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(isLoading = false, loadError = throwable.toLoadErrorMessage())
                    }
                }
        }
    }

    private suspend fun loadPaymentSources() {
        val wallet = walletRepository.getWallet().getOrNull()
        val cards = cardRepository.getCards().getOrNull().orEmpty()
        val defaultCard = cards.firstOrNull { it.isDefault } ?: cards.firstOrNull()
        val payable = _uiState.value.payableAmount
        val insufficient = wallet != null && payable != null && wallet.balance < payable
        // Bakiye yetersiz ama kayıtlı kart varsa, kartı öntanımlı yöntem olarak seç.
        val defaultMethod = if (insufficient && defaultCard != null) {
            PaymentMethodOption.CARD
        } else {
            PaymentMethodOption.WALLET
        }
        _uiState.update {
            it.copy(
                walletBalance = wallet?.balance,
                cards = cards,
                selectedCardId = defaultCard?.id,
                selectedMethod = defaultMethod,
            )
        }
    }

    fun onMethodSelect(method: PaymentMethodOption) {
        _uiState.update { it.copy(selectedMethod = method, payError = null) }
    }

    fun onCardSelect(cardId: String) {
        _uiState.update { it.copy(selectedCardId = cardId, payError = null) }
    }

    fun onDiscountCodeChange(code: String) {
        _uiState.update { it.copy(discountCode = code) }
    }

    fun pay() {
        val state = _uiState.value
        if (state.isPaying || !state.isPayable || !state.canPay) return
        when (state.selectedMethod) {
            PaymentMethodOption.IYZICO -> startIyzicoCheckout()
            PaymentMethodOption.WALLET, PaymentMethodOption.CARD -> payDirect()
        }
    }

    private fun payDirect() {
        val state = _uiState.value
        val method = state.selectedMethod
        val cardId = if (method == PaymentMethodOption.CARD) state.selectedCardId else null
        val discountCode = state.discountCode.takeIf { it.isNotBlank() }
        val walletInsufficient = state.walletInsufficient
        viewModelScope.launch {
            _uiState.update { it.copy(isPaying = true, payError = null) }
            rentalRepository.payRental(
                id = rentalId,
                method = method.name,
                cardId = cardId,
                discountCode = discountCode,
            )
                .onSuccess { receipt ->
                    _uiState.update { it.copy(isPaying = false, receipt = receipt) }
                }
                .onFailure { throwable ->
                    val message = throwable.toPayErrorMessage(
                        discountProvided = discountCode != null,
                        walletInsufficient = walletInsufficient,
                    )
                    _uiState.update { it.copy(isPaying = false, payError = message) }
                }
        }
    }

    // --- İyzico Checkout Form akışı --------------------------------------------

    private fun startIyzicoCheckout() {
        val amount = _uiState.value.payableAmount ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isPaying = true, payError = null) }
            iyzicoRepository.initializeCheckoutForm(rentalId, amount)
                .onSuccess { response ->
                    val url = response.paymentPageUrl
                    if (url == null) {
                        _uiState.update {
                            it.copy(isPaying = false, payError = "Ödeme sayfası açılamadı. Lütfen tekrar dene.")
                        }
                        return@onSuccess
                    }
                    _uiState.update {
                        it.copy(iyzicoCheckoutUrl = url, iyzicoToken = response.token)
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(isPaying = false, payError = "Ödeme sayfası açılamadı. Lütfen tekrar dene.")
                    }
                }
        }
    }

    // WebView, İyzico'nun barındırdığı sayfadan ayrılıp backend'in
    // (rencarv2.halitkalayci.com/iyzico/checkout-form/callback) host'una döndüğünde
    // ekran bunu çağırır: ödeme akışı tamamlanmış demektir, sonucu sorgularız.
    fun onIyzicoWebViewReturnedToBackend() {
        val token = _uiState.value.iyzicoToken ?: return
        _uiState.update { it.copy(iyzicoCheckoutUrl = null) }
        viewModelScope.launch {
            iyzicoRepository.getCheckoutFormResult(token)
                .onSuccess { result ->
                    if (result.paymentStatus == "SUCCESS" && result.paymentId != null) {
                        finalizeIyzicoPayment(result.paymentId)
                    } else {
                        _uiState.update {
                            it.copy(isPaying = false, iyzicoToken = null, payError = "Ödeme tamamlanamadı.")
                        }
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(isPaying = false, iyzicoToken = null, payError = "Ödeme sonucu doğrulanamadı.")
                    }
                }
        }
    }

    fun onIyzicoCheckoutCancelled() {
        _uiState.update { it.copy(isPaying = false, iyzicoCheckoutUrl = null, iyzicoToken = null) }
    }

    private suspend fun finalizeIyzicoPayment(iyzicoPaymentId: String) {
        rentalRepository.payRental(
            id = rentalId,
            method = PaymentMethodOption.IYZICO.name,
            iyzicoPaymentId = iyzicoPaymentId,
        )
            .onSuccess { receipt ->
                _uiState.update { it.copy(isPaying = false, iyzicoToken = null, receipt = receipt) }
            }
            .onFailure { throwable ->
                _uiState.update {
                    it.copy(
                        isPaying = false,
                        iyzicoToken = null,
                        payError = throwable.toPayErrorMessage(discountProvided = false, walletInsufficient = false),
                    )
                }
            }
    }
}

private fun Throwable.toLoadErrorMessage(): String = when {
    this is HttpException && code() == 404 -> "Kiralama bulunamadı."
    this is HttpException && code() == 401 -> "Oturumun sona ermiş. Lütfen tekrar giriş yap."
    else -> "Kiralama özeti yüklenemedi. Lütfen tekrar dene."
}

private fun Throwable.toPayErrorMessage(
    discountProvided: Boolean,
    walletInsufficient: Boolean,
): String = when {
    this is HttpException && code() == 409 -> {
        // 409 üç anlama gelebilir: yetersiz bakiye / zaten ödendi / indirim kodu limiti.
        // Önce sunucu mesajından, olmazsa yerel bağlamdan en olası nedeni seçeriz.
        val body = runCatching { response()?.errorBody()?.string() }
            .getOrNull().orEmpty().lowercase(Locale("tr", "TR"))
        when {
            body.contains("bakiye") -> "Cüzdan bakiyen yetersiz. Bakiye yükle ya da kartla öde."
            body.contains("indirim") -> "İndirim kodu geçersiz ya da kullanım limiti dolmuş."
            body.contains("zaten") -> "Bu yolculuğun ödemesi zaten alınmış."
            walletInsufficient -> "Cüzdan bakiyen yetersiz. Bakiye yükle ya da kartla öde."
            discountProvided -> "İndirim kodu geçersiz ya da kullanım limiti dolmuş."
            else -> "Ödeme alınamadı. Bu yolculuk zaten ödenmiş olabilir."
        }
    }
    this is HttpException && code() == 404 -> "İndirim kodu ya da kart bulunamadı."
    this is HttpException && code() == 400 -> "Geçersiz ödeme isteği. Bilgileri kontrol et."
    this is HttpException && code() == 401 -> "Oturumun sona ermiş. Lütfen tekrar giriş yap."
    else -> "Ödeme alınamadı. Lütfen tekrar dene."
}
