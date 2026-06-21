package com.example.fooddeliveryapp.feature.nav

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.fooddeliveryapp.feature.admin_panel.AdminPanelScreen
import com.example.fooddeliveryapp.feature.admin_panel.manage_product.ManageProductScreen
import com.example.fooddeliveryapp.feature.splash.SplashScreen
import com.example.fooddeliveryapp.feature.auth.AuthScreen
import com.example.fooddeliveryapp.feature.home.HomeScreen
import com.example.fooddeliveryapp.feature.product_details.ProductDetailsScreen
import com.example.fooddeliveryapp.feature.profile.ProfileScreen

const val HOME_TAB_KEY = "HOME_TAB_KEY"

private fun NavController.setHomeTab(tab: HomeTab) {
    try {
        val homeEntry = getBackStackEntry<Screens.HomeGraph>()
        homeEntry.savedStateHandle[HOME_TAB_KEY] = tab
    } catch (e: IllegalArgumentException) {
    }
}

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
                    navController.navigate(Screens.HomeGraph()) {
                        popUpTo<Screens.SplashScreen> { inclusive = true }
                    }
                }
            )
        }
        composable<Screens.AuthScreen> {
            AuthScreen(
                navigateToHome = {
                    navController.navigate(Screens.HomeGraph()) {
                        popUpTo<Screens.AuthScreen> { inclusive = true }
                    }
                }
            )
        }

        composable<Screens.HomeGraph> { entry ->
            val args = entry.toRoute<Screens.HomeGraph>()

            // Listen for incoming tab switch requests
            val requestedTabFlow = entry.savedStateHandle.getStateFlow(HOME_TAB_KEY, args.start)
            val requestStateTab by requestedTabFlow.collectAsState()
            HomeScreen(
                startTab = requestStateTab,
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
                },
                navigateToDetails = { productId ->
                    navController.navigate(Screens.DetailsScreen(id = productId))
                },
                navigateToCheckout = { amount ->
                    navController.navigate(Screens.Checkout(amount = amount))
                },
                navigateToMenu = {
                    navController.setHomeTab(HomeTab.Categories)
                },
                navigateToProductCategory = { categoryTitle ->
                    navController.navigate(Screens.ProductCategoryScreen(category = categoryTitle))
                },
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
                },
                navigateToManageProduct = { id ->
                    navController.navigate(Screens.ManageProduct(id = id))
                }
            )
        }

        composable<Screens.ManageProduct> {
            val id = it.toRoute<Screens.ManageProduct>().id
            ManageProductScreen(
                id = id,
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }

        composable<Screens.DetailsScreen> {
            ProductDetailsScreen(
                navigateBack = {
                    navController.navigateUp()
                },
                navigateToCart = {
                    navController.setHomeTab(HomeTab.Cart)
                    navController.navigateUp()
                },
                navigateToCheckout = { amount ->
                    navController.navigate(Screens.Checkout(amount = amount))
                },
                navigateToMenu = {
                    navController.setHomeTab(HomeTab.Categories)
                    navController.popBackStack()
                }
            )
        }
    }
}