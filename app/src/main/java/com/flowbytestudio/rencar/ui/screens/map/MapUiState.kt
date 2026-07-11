package com.flowbytestudio.rencar.ui.screens.map

import com.flowbytestudio.rencar.data.rentals.RentalWithVehicle
import com.flowbytestudio.rencar.data.vehicles.VehicleDto

data class MapUiState(
    val isLoading: Boolean = true,
    val vehicles: List<VehicleDto> = emptyList(),
    val error: String? = null,
    val selectedType: String? = null,
    val focusedVehicleId: String? = null,
    val activeRental: RentalWithVehicle? = null,
) {
    val filteredVehicles: List<VehicleDto>
        get() = vehicles.filter { selectedType == null || it.type == selectedType }

    val availableTypes: List<String>
        get() = vehicles.map { it.type }.distinct()
}
