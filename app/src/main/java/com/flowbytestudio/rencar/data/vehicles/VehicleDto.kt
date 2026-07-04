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
)
