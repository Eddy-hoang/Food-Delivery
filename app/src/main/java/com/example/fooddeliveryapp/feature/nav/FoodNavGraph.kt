package com.example.fooddeliveryapp.feature.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fooddeliveryapp.feature.admin_panel.AdminPanelScreen
import com.example.fooddeliveryapp.feature.splash.SplashScreen
import com.example.fooddeliveryapp.feature.auth.AuthScreen
import com.example.fooddeliveryapp.feature.home.HomeScreen
import com.example.fooddeliveryapp.feature.profile.ProfileScreen

@Composable
fun FoodNavGraph(startDeprecated: Screens = Screens.SplashScreen) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDeprecated
    ) {
        composable<Screens.SplashScreen> {
            SplashScreen(
                navigaToAuth = {
                    navController.navigate(Screens.AuthScreen) {
                        popUpTo<Screens.SplashScreen> { inclusive = true }
                    }
                },
                navigaToHome = {
                    navController.navigate(Screens.HomeGraph) {
                        popUpTo<Screens.SplashScreen> { inclusive = true }
                    }
                }
            )
        }
        composable<Screens.AuthScreen> {
            AuthScreen(
                navigateToHome = {
                    navController.navigate(Screens.HomeGraph) {
                        popUpTo<Screens.AuthScreen> { inclusive = true }
                    }
                }
            )
        }

        composable<Screens.HomeGraph> {
            HomeScreen(
                navigateToAuth = {
                    navController.navigate(Screens.AuthScreen) {
                        popUpTo<Screens.HomeGraph> { inclusive = true }
                    }
                },
                navigateToProfile = {
                    navController.navigate(Screens.Profile)
                },
                navigateToAdminPanel = {
                    navController.navigate(Screens.AdminPanel)
                }
            )
        }

        composable<Screens.Profile> {
            ProfileScreen(
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }

        composable<Screens.AdminPanel> {
            AdminPanelScreen(
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}
