package com.flowbytestudio.rencar.data.geocoding

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {

    // Nominatim (OpenStreetMap) serbest metin arama ucu.
    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 5,
        // Sonuçları Türkiye ile sınırlar; harita zaten yalnız TR filosunu gösteriyor.
        @Query("countrycodes") countryCodes: String = "tr",
    ): List<GeocodingResultDto>
}

@Serializable
data class GeocodingResultDto(
    val lat: String,
    val lon: String,
    @SerialName("display_name") val displayName: String,
)
