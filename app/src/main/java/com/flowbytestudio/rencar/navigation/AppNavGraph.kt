package com.flowbytestudio.rencar.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.flowbytestudio.rencar.ui.screens.history.HistoryScreen
import com.flowbytestudio.rencar.ui.screens.map.MapScreen
import com.flowbytestudio.rencar.ui.screens.profile.ProfileScreen
import com.flowbytestudio.rencar.ui.screens.reservation.ReservationScreen
import com.flowbytestudio.rencar.ui.screens.wallet.WalletScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = MapRoute,
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
        composable<ProfileRoute> { ProfileScreen() }
        composable<ReservationRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ReservationRoute>()
            ReservationScreen(
                vehicleId = route.vehicleId,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
