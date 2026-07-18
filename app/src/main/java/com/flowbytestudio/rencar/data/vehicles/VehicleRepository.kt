package com.flowbytestudio.rencar.data.vehicles

import com.flowbytestudio.rencar.data.network.NetworkModule

class VehicleRepository(
    private val api: VehicleApi = NetworkModule.vehicleApi,
) {

    suspend fun getVehicles(
        segment: String? = null,
        includeBusy: Boolean = false,
    ): Result<List<VehicleDto>> = runCatching {
        api.getVehicles(
            segment = segment,
            limit = 100,
            includeBusy = if (includeBusy) true else null,
        )
    }

    suspend fun getAvailableVehicles(): Result<List<VehicleDto>> = getVehicles()

    suspend fun getVehicle(id: String): Result<VehicleDto> = runCatching {
        api.getVehicle(id)
    }

    suspend fun getQuote(id: String, plan: String, minutes: Int): Result<QuoteResponse> =
        runCatching {
            api.getQuote(id = id, plan = plan, minutes = minutes)
        }
}
