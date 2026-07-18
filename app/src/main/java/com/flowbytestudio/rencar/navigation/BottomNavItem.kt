package com.flowbytestudio.rencar.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute

data class BottomNavItem(
    val route: Any,
    val matchesDestination: (NavDestination?) -> Boolean,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = MapRoute,
        matchesDestination = { it?.hasRoute(MapRoute::class) == true },
        label = "Harita",
        selectedIcon = Icons.Filled.LocationOn,
        unselectedIcon = Icons.Outlined.LocationOn,
    ),
    BottomNavItem(
        route = HistoryRoute,
        matchesDestination = { it?.hasRoute(HistoryRoute::class) == true },
        label = "Geçmiş",
        selectedIcon = Icons.Filled.History,
        unselectedIcon = Icons.Outlined.History,
    ),
    BottomNavItem(
        route = WalletRoute,
        matchesDestination = { it?.hasRoute(WalletRoute::class) == true },
        label = "Cüzdan",
        selectedIcon = Icons.Filled.CreditCard,
        unselectedIcon = Icons.Outlined.CreditCard,
    ),
    BottomNavItem(
        route = ProfileRoute,
        matchesDestination = { it?.hasRoute(ProfileRoute::class) == true },
        label = "Profil",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
    ),
)
