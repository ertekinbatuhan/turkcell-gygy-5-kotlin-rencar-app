package com.flowbytestudio.rencar.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.flowbytestudio.rencar.data.auth.AuthSession
import com.flowbytestudio.rencar.ui.screens.history.HistoryScreen
import com.flowbytestudio.rencar.ui.screens.license.LicenseUploadScreen
import com.flowbytestudio.rencar.ui.screens.map.MapScreen
import com.flowbytestudio.rencar.ui.screens.profile.ProfileScreen
import com.flowbytestudio.rencar.ui.screens.reservation.ReservationScreen
import com.flowbytestudio.rencar.ui.screens.settings.SettingsScreen
import com.flowbytestudio.rencar.ui.screens.wallet.WalletScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val startDestination = remember { if (AuthSession.justRegistered) LicenseUploadRoute else MapRoute }
    LaunchedEffect(Unit) { AuthSession.consumeJustRegistered() }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable<MapRoute> {
            MapScreen(
                onNavigateToReservation = { vehicleId ->
                    navController.navigate(ReservationRoute(vehicleId))
                },
            )
        }
        composable<HistoryRoute> { HistoryScreen() }
        composable<WalletRoute> { WalletScreen() }
        composable<ProfileRoute> {
            ProfileScreen(
                onNavigateToSettings = { navController.navigate(SettingsRoute) },
                onNavigateToLicenseUpload = { navController.navigate(LicenseUploadRoute) },
            )
        }
        composable<SettingsRoute> {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable<ReservationRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ReservationRoute>()
            ReservationScreen(
                vehicleId = route.vehicleId,
                onBack = { navController.popBackStack() },
            )
        }
        composable<LicenseUploadRoute> { backStackEntry ->
            val isStartDestination = backStackEntry.destination.route ==
                navController.graph.findStartDestination().route
            LicenseUploadScreen(
                canGoBack = !isStartDestination,
                onFinished = {
                    if (!navController.popBackStack()) navController.navigate(MapRoute)
                },
                onSkip = {
                    if (!navController.popBackStack()) navController.navigate(MapRoute)
                },
            )
        }
    }
}
