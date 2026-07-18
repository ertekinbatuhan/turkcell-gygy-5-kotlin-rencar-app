package com.flowbytestudio.rencar.data.vehicles

import kotlinx.serialization.Serializable

@Serializable
data class VehicleDto(
    val id: String,
    val plate: String,
    val brand: String,
    val model: String,
    val type: String,
    val pricePerDay: Double,
    // AVAILABLE / RESERVED / RENTED / MAINTENANCE
    val status: String,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String? = null,
    // v2 şemasında zorunlu; eski kayıtlara karşı nullable+default ile korunuyor.
    val fuelPercent: Int? = null,
    val rangeKm: Int? = null,
    // MANUAL / AUTOMATIC
    val transmission: String? = null,
    val seats: Int? = null,
    val pricePerMinute: Double? = null,
    val pricePerHour: Double? = null,
    // ECONOMY / COMFORT / SUV — fiyat segmenti (karoseri tipi değil)
    val segment: String? = null,
)

// GET /vehicles/{id}/quote — kayıt oluşturmayan salt fiyat önizlemesi.
@Serializable
data class QuoteResponse(
    val vehicleId: String,
    val plan: String,
    val minutes: Int,
    val usageFee: Double,
    val startFee: Double,
    val serviceFee: Double,
    val estimatedTotal: Double,
)
