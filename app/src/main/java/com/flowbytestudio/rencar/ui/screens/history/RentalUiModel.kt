package com.flowbytestudio.rencar.ui.screens.history

import androidx.annotation.StringRes
import com.flowbytestudio.rencar.R

enum class RentalStatus(@StringRes val labelRes: Int?) {
    PREPARING(R.string.history_status_preparing),
    ACTIVE(R.string.history_status_active),
    COMPLETED(R.string.history_status_completed),
    CANCELLED(R.string.history_status_cancelled),
    // Bilinmeyen durumlar için güvenli varsayılan; ham etiket ayrıca taşınır.
    OTHER(null),
}

data class RentalUiModel(
    val id: String,
    val vehicleId: String,
    // "Marka Model · Plaka" (araç yoksa vehicleId).
    val vehicleLabel: String,
    // Dakikalık / Saatlik / Günlük; bilinmeyen planlarda null.
    @StringRes val planLabelRes: Int?,
    // Backend'den gelen ham plan; planLabelRes null ise gösterilir.
    val planLabelRaw: String,
    // startedAt biçimlenmiş; PREPARING'de yoksa "—".
    val dateLabel: String,
    // "₺X" ya da fiyat kilitlenmemişse "—".
    val priceLabel: String,
    val durationMinutes: Int,
    val distanceKm: Double,
    val status: RentalStatus,
    // Bilinen durumlar enum etiket kaynağını taşır, bilinmeyenlerde null.
    @StringRes val statusLabelRes: Int?,
    // Backend'den gelen ham status; statusLabelRes null ise gösterilir.
    val statusLabelRaw: String,
    // COMPLETED && UNPAID ise ödenmedi rozeti gösterilir.
    val isUnpaidCompleted: Boolean,
)
