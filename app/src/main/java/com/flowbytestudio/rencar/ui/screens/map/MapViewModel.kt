package com.flowbytestudio.rencar.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowbytestudio.rencar.data.vehicles.VehicleDto
import com.flowbytestudio.rencar.data.vehicles.VehicleRepository
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel(
    private val repository: VehicleRepository = VehicleRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadVehicles()
    }

    fun loadVehicles() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getAvailableVehicles()
                .onSuccess { vehicles ->
                    _uiState.update { it.copy(isLoading = false, vehicles = vehicles) }
                }
                .onFailure { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Araçlar yüklenemedi. ${throwable.message.orEmpty()}".trim(),
                        )
                    }
                }
        }
    }

    fun onTypeSelected(type: String?) {
        _uiState.update { it.copy(selectedType = type, focusedVehicleId = null) }
    }

    fun onVehicleFocused(vehicleId: String?) {
        _uiState.update { it.copy(focusedVehicleId = vehicleId) }
    }

    fun findNearestVehicle(userLat: Double, userLon: Double): VehicleDto? {
        val nearest = _uiState.value.filteredVehicles.minByOrNull { vehicle ->
            haversineMeters(userLat, userLon, vehicle.latitude, vehicle.longitude)
        }
        _uiState.update { it.copy(focusedVehicleId = nearest?.id) }
        return nearest
    }
}

private fun haversineMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadiusMeters = 6371000.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
        cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadiusMeters * c
}
