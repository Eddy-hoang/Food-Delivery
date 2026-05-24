package com.example.fooddeliveryapp.feature.home.domain

import com.example.fooddeliveryapp.feature.nav.Screens
import com.example.fooddeliveryapp.ui.theme.Resources

enum class BottomBarDestinations(
    val icon: Int,
    val title: String,
    val screen: Screens
) {
    ProductOverViewScreen(
        icon = Resources.Icon.Home,
        title = "Burgers",
        screen = Screens.ProductOverViewScreen
    ),
    CartScreen(
        icon = Resources.Icon.ShoppingCart,
        title = "Cart",
        screen = Screens.Cart
    ),
    NotificationScreen(
        icon = Resources.Icon.Bell,
        title = "Notification",
        screen = Screens.Notification
    ),
    CategoriesSceen(
        icon = Resources.Icon.Categories,
        title = "Categories",
        screen = Screens.Categories
    )
}