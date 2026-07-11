package com.flowbytestudio.rencar.navigation

import kotlinx.serialization.Serializable

@Serializable
data object OnboardingRoute

@Serializable
data object LoginRoute

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
data class ReservationRoute(val vehicleId: String)

@Serializable
data class HandoverRoute(val vehicleId: String)

@Serializable
data class ActiveRentalRoute(val rentalId: String)

@Serializable
data class TripSummaryRoute(val rentalId: String)
