package com.flowbytestudio.rencar.data.geocoding

import com.flowbytestudio.rencar.data.network.NetworkModule

data class GeocodingResult(
    val displayName: String,
    val latitude: Double,
    val longitude: Double,
)

class GeocodingRepository(
    private val api: GeocodingApi = NetworkModule.geocodingApi,
) {

    suspend fun search(query: String): Result<List<GeocodingResult>> = runCatching {
        api.search(query = query)
            .mapNotNull { result ->
                val lat = result.lat.toDoubleOrNull() ?: return@mapNotNull null
                val lon = result.lon.toDoubleOrNull() ?: return@mapNotNull null
                GeocodingResult(displayName = result.displayName, latitude = lat, longitude = lon)
            }
    }
}
