package com.flowbytestudio.rencar.ui.screens.history

enum class RentalStatus(val label: String) {
    PREPARING("Hazırlanıyor"),
    ACTIVE("Devam ediyor"),
    COMPLETED("Tamamlandı"),
    CANCELLED("İptal edildi"),
    // Bilinmeyen durumlar için güvenli varsayılan; ham etiket ayrıca taşınır.
    OTHER(""),
}

data class RentalUiModel(
    val id: String,
    val vehicleId: String,
    // "Marka Model · Plaka" (araç yoksa vehicleId).
    val vehicleLabel: String,
    // Dakikalık / Saatlik / Günlük
    val planLabel: String,
    // startedAt biçimlenmiş; PREPARING'de yoksa "—".
    val dateLabel: String,
    // "₺X" ya da fiyat kilitlenmemişse "—".
    val priceLabel: String,
    val durationMinutes: Int,
    val distanceKm: Double,
    val status: RentalStatus,
    // Bilinen durumlar enum etiketini, bilinmeyenler ham status'u gösterir.
    val statusLabel: String,
    // COMPLETED && UNPAID ise ödenmedi rozeti gösterilir.
    val isUnpaidCompleted: Boolean,
)
