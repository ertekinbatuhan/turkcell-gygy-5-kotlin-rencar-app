package com.flowbytestudio.rencar.ui.screens.map

import androidx.compose.ui.graphics.Color

enum class VehicleType(val apiValue: String, val label: String, val color: Color) {
    SEDAN("SEDAN", "Sedan", Color(0xFFF59E0B)),
    SUV("SUV", "SUV", Color(0xFF8B5CF6)),
    HATCHBACK("HATCHBACK", "Hatchback", Color(0xFF14B8A6)),
    STATION("STATION", "Station", Color(0xFFEAB308)),
    MINIVAN("MINIVAN", "Minivan", Color(0xFFEF4444));

    companion object {
        fun fromApiValue(value: String): VehicleType? = entries.find { it.apiValue == value }

        fun colorFor(apiValue: String): Color = fromApiValue(apiValue)?.color ?: Color(0xFF6B7280)

        fun labelFor(apiValue: String): String = fromApiValue(apiValue)?.label ?: apiValue
    }
}

// Fiyat segmenti (ECONOMY/COMFORT/SUV) — karoseri tipinden (VehicleType) BAĞIMSIZDIR.
// Haritadaki Tümü/Ekonomik/Konfor/SUV sekmeleri (sunucu ?segment paramı) ve araç
// kartındaki segment rozeti bunu kullanır.
enum class VehicleSegment(val apiValue: String, val label: String) {
    ECONOMY("ECONOMY", "Ekonomik"),
    COMFORT("COMFORT", "Konfor"),
    SUV("SUV", "SUV");

    companion object {
        fun labelFor(apiValue: String?): String? =
            apiValue?.let { value -> entries.find { it.apiValue == value }?.label }
    }
}
