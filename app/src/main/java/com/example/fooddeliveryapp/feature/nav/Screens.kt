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

    @Serializable
    data object ProductOverViewScreen : Screens()

    @Serializable
    data object Cart : Screens()

    @Serializable
    data object Notification : Screens()

    @Serializable
    data object Categories : Screens()
    @Serializable
    data object Profile : Screens()

    @Serializable
    data object AdminPanel: Screens()
}