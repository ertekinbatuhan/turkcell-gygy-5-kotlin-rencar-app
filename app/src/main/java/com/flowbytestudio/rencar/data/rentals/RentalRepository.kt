package com.flowbytestudio.rencar.data.rentals

import com.flowbytestudio.rencar.data.network.NetworkModule
import com.flowbytestudio.rencar.data.vehicles.VehicleRepository

class RentalRepository(
    private val api: RentalApi = NetworkModule.rentalApi,
    private val vehicleRepository: VehicleRepository = VehicleRepository(),
) {

    suspend fun getMyRentals(): Result<List<RentalWithVehicle>> = runCatching {
        val rentals = api.getMyRentals()
        val vehiclesById = vehicleRepository.getAvailableVehicles().getOrDefault(emptyList())
            .associateBy { it.id }
        rentals.map { rental ->
            RentalWithVehicle(rental = rental, vehicle = vehiclesById[rental.vehicleId])
        }
    }

    suspend fun createRental(vehicleId: String, endDate: String): Result<RentalDto> = runCatching {
        api.createRental(CreateRentalRequest(vehicleId = vehicleId, endDate = endDate))
    }
}
