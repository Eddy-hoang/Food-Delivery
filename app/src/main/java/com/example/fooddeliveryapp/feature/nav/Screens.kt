package com.example.fooddeliveryapp.feature.nav

import kotlinx.serialization.Serializable

@Serializable
sealed class Screens {
    @Serializable
    data object SplashScreen : Screens()

    @Serializable
    data object AuthScreen : Screens()

    @Serializable
    data object HomeGraph : Screens()
}