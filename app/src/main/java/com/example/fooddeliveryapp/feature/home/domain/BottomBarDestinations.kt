package com.example.fooddeliveryapp.feature.home.domain

import com.example.fooddeliveryapp.feature.nav.Screens
import com.example.fooddeliveryapp.ui.theme.Resources

enum class BottomBarDestinations(
    val icon: Int,
    val title: String,
    val screen: Screens
) {
    ProductOverviewScreen(
        icon = Resources.Icon.Home,
        title = "Burgers",
        screen = Screens.ProductOverviewScreen
    ),
    CartScreen(
        icon = Resources.Icon.ShoppingCart,
        title = "Cart",
        screen = Screens.Cart
    ),
    NotificationsScreen(
        icon = Resources.Icon.Bell,
        title = "Notification",
        screen = Screens.Notifications
    ),
    CategoriesScreen(
        icon = Resources.Icon.Categories,
        title = "Categories",
        screen = Screens.Categories
    )
}