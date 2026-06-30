package com.flowbytestudio.rencar.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.flowbytestudio.rencar.ui.screens.history.HistoryScreen
import com.flowbytestudio.rencar.ui.screens.map.MapScreen
import com.flowbytestudio.rencar.ui.screens.profile.ProfileScreen
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
        composable<MapRoute> { MapScreen() }
        composable<HistoryRoute> { HistoryScreen() }
        composable<WalletRoute> { WalletScreen() }
        composable<ProfileRoute> { ProfileScreen() }
    }
}
