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
    val status: String,
    val latitude: Double,
    val longitude: Double,
    // Backend bu alanları henüz döndürmüyor; detay tasarımı için önden eklendi.
    // Nullable + default sayesinde mevcut cevaplarla uyumlu kalır.
    val imageUrl: String? = null,
    val fuelPercent: Int? = null,
    val rangeKm: Int? = null,
    val transmission: String? = null,
    val seatCount: Int? = null,
    val pricePerMinute: Double? = null,
    val pricePerHour: Double? = null,
)
