package com.flowbytestudio.rencar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.flowbytestudio.rencar.data.auth.AuthSession
import com.flowbytestudio.rencar.navigation.AppNavGraph
import com.flowbytestudio.rencar.navigation.LoginRoute
import com.flowbytestudio.rencar.navigation.OnboardingRoute
import com.flowbytestudio.rencar.navigation.RencarNavBar
import com.flowbytestudio.rencar.navigation.ReservationRoute
import com.flowbytestudio.rencar.ui.screens.login.LoginScreen
import com.flowbytestudio.rencar.ui.screens.onboarding.OnboardingScreen
import com.flowbytestudio.rencar.ui.theme.RencarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RencarTheme {
                RencarApp()
            }
        }
    }
}

@Composable
private fun RencarApp() {
    val isLoggedIn by AuthSession.isLoggedIn.collectAsState()

    if (!isLoggedIn) {
        val authNavController = rememberNavController()

        NavHost(
            navController = authNavController,
            startDestination = OnboardingRoute,
        ) {
            composable<OnboardingRoute> {
                OnboardingScreen(
                    onStartClick = { authNavController.navigate(LoginRoute) },
                    onLoginClick = { authNavController.navigate(LoginRoute) },
                )
            }

            composable<LoginRoute> {
                LoginScreen(
                    onLoggedIn = { /* AuthSession handles state */ },
                    onBack = { authNavController.popBackStack() },
                )
            }
        }

        return
    }

    val navController = rememberNavController()
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val showBottomBar = currentDestination?.hasRoute(ReservationRoute::class) != true

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                RencarNavBar(navController = navController)
            }
        },
    ) { innerPadding ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}
