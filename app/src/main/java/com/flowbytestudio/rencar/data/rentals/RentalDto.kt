package com.flowbytestudio.rencar.data.rentals

import kotlinx.serialization.Serializable

// RentalResponseDto — /rentals uçlarının ortak cevabı.
// elapsedSeconds/currentCost yalnız GET /rentals/active, usageFee yalnız
// POST /rentals/{id}/finish cevabında dolu gelir; tek DTO üçünü de karşılar.
@Serializable
data class RentalDto(
    val id: String,
    val userId: String,
    val vehicleId: String,
    val vehicle: RentalVehicleSummary? = null,
    // PER_MINUTE / HOURLY / DAILY
    val plan: String = "DAILY",
    // "Kilidi aç" anı; PREPARING aşamasında henüz atılmamış olabilir.
    val startedAt: String? = null,
    val endedAt: String? = null,
    // Yalnız DAILY planda dolu.
    val endDate: String? = null,
    // DAILY: oluştururken kilitlenir; PER_MINUTE/HOURLY: finish'e kadar null.
    val totalPrice: Double? = null,
    val startFee: Double = 0.0,
    val serviceFee: Double? = null,
    val distanceKm: Double = 0.0,
    val durationMinutes: Int = 0,
    // PREPARING / ACTIVE / COMPLETED / CANCELLED
    val status: String,
    // UNPAID / PAID
    val paymentStatus: String = "UNPAID",
    // WALLET / CARD — yalnız ödeme alındıysa dolu.
    val paymentMethod: String? = null,
    val discountAmount: Double = 0.0,
    val createdAt: String,
    // GET /rentals/active ekleri
    val elapsedSeconds: Long? = null,
    val currentCost: Double? = null,
    // POST /rentals/{id}/finish eki
    val usageFee: Double? = null,
)

@Serializable
data class RentalVehicleSummary(
    val id: String,
    val plate: String,
    val brand: String,
    val model: String,
    val type: String,
)

@Serializable
data class CreateRentalRequest(
    val vehicleId: String,
    // Verilmezse DAILY varsayılır (geriye uyum).
    val plan: String? = null,
    // YALNIZ DAILY planda zorunlu; dk/sa planında gönderilmez.
    val endDate: String? = null,
)

@Serializable
data class RentalPhotoDto(
    val side: String,
    val imageUrl: String,
    val createdAt: String,
)

// "2/4 çekildi" sayacı ve start önkoşulu bu cevaptan beslenir.
@Serializable
data class RentalPhotosState(
    val rentalId: String,
    val photos: List<RentalPhotoDto> = emptyList(),
    val uploadedCount: Int,
    val remainingSides: List<String> = emptyList(),
    val photosComplete: Boolean,
)

@Serializable
data class RentalStatsResponse(
    val month: String,
    val tripCount: Int,
    val totalSpent: Double,
    val totalMinutes: Int,
    val totalKm: Double,
)

@Serializable
data class PayRentalRequest(
    // WALLET / CARD
    val method: String,
    // YALNIZ CARD yönteminde zorunlu; WALLET'ta gönderilirse 400.
    val cardId: String? = null,
    val discountCode: String? = null,
)

@Serializable
data class PaidCardSummary(
    val brand: String,
    val last4: String,
)

@Serializable
data class PayRentalResponse(
    val rentalId: String,
    val paymentStatus: String,
    val method: String,
    val totalPrice: Double,
    val discountAmount: Double,
    val paidAmount: Double,
    // Yalnız WALLET yönteminde dolu.
    val walletBalance: Double? = null,
    // Yalnız CARD yönteminde dolu.
    val card: PaidCardSummary? = null,
)
