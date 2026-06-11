package com.example.fooddeliveryapp.core.data.models

import com.example.fooddeliveryapp.ui.theme.Resources

data class Product(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val allergyAdvice: String,
    val energyValue: Int?,
    val ingredients: String,
    val price: Double,
)

enum class ProductCategory(
    val title: String,
    val icon: Int,
) {
    Burgers(
        title = "Burgers",
        icon = Resources.Icon.Burgers
    ),
    Nuggets(
        title = "Nuggets",
        icon = Resources.Icon.Nuggets
    ),
    Wraps(
        title = "Wraps",
        icon = Resources.Icon.Wraps
    ),
    Desserts(
        title = "Desserts",
        icon = Resources.Icon.Desserts
    ),
    Sacuces(
        title = "Sacuces",
        icon = Resources.Icon.Sacuces
    ),
    Fries(
        title = "Fries",
        icon = Resources.Icon.Fries
    ),
    Drink(
        title = "Drink",
        icon = Resources.Icon.Drink
    )
}