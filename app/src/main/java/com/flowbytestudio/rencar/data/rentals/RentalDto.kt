package com.flowbytestudio.rencar.data.rentals

import com.flowbytestudio.rencar.data.vehicles.VehicleDto
import kotlinx.serialization.Serializable

@Serializable
data class RentalDto(
    val id: String,
    val userId: String,
    val vehicleId: String,
    val startDate: String,
    val endDate: String,
    val totalPrice: Double,
    val status: String,
    val createdAt: String,
)

@Serializable
data class CreateRentalRequest(
    val vehicleId: String,
    val endDate: String,
)

data class RentalWithVehicle(
    val rental: RentalDto,
    val vehicle: VehicleDto?,
)
