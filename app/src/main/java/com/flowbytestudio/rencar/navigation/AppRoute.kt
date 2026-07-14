package com.flowbytestudio.rencar.navigation

import kotlinx.serialization.Serializable

@Serializable
data object OnboardingRoute

@Serializable
data object LoginRoute

@Serializable
data object RegisterRoute

@Serializable
data object LicenseUploadRoute

@Serializable
data object MapRoute

@Serializable
data object HistoryRoute

@Serializable
data object WalletRoute

@Serializable
data object ProfileRoute

@Serializable
data object SettingsRoute

@Serializable
data object ReferralRoute

@Serializable
data class ReservationRoute(val vehicleId: String)

// v2'de foto akışı PREPARING durumundaki kiralamaya bağlıdır; ekran rentalId alır.
@Serializable
data class HandoverRoute(val rentalId: String)

@Serializable
data class ActiveRentalRoute(val rentalId: String)

@Serializable
data class TripSummaryRoute(val rentalId: String)
