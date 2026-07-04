package com.flowbytestudio.rencar.data.vehicles

import com.flowbytestudio.rencar.data.network.NetworkModule

class VehicleRepository(
    private val api: VehicleApi = NetworkModule.vehicleApi,
) {

    suspend fun getAvailableVehicles(): Result<List<VehicleDto>> = runCatching {
        api.getVehicles(limit = 100)
    }

    suspend fun getVehicle(id: String): Result<VehicleDto> = runCatching {
        api.getVehicle(id)
    }
}
