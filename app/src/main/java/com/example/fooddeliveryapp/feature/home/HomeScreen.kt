package com.example.fooddeliveryapp.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fooddeliveryapp.feature.component.BurgersBottomBar
import com.example.fooddeliveryapp.feature.home.domain.BottomBarDestinations
import com.example.fooddeliveryapp.feature.nav.Screens

@Composable
fun HomeScreen(){
    val navController = rememberNavController()
    val currentRouter = navController.currentBackStackEntryAsState()

    val selectedDestinaion by remember {
        derivedStateOf {
        val route = currentRouter.value?.destination?.route.toString()
        when{
            route.contains(BottomBarDestinations.ProductOverViewScreen.screen.toString()) -> BottomBarDestinations.ProductOverViewScreen
            route.contains(BottomBarDestinations.CartScreen.screen.toString()) -> BottomBarDestinations.CartScreen
            route.contains(BottomBarDestinations.NotificationScreen.screen.toString()) -> BottomBarDestinations.NotificationScreen
            route.contains(BottomBarDestinations.CategoriesSceen.screen.toString()) -> BottomBarDestinations.CategoriesSceen
            else -> BottomBarDestinations.ProductOverViewScreen
        }}
    }
    Scaffold() { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                modifier = Modifier.weight(1f),
                navController = navController,
                startDestination = Screens.ProductOverViewScreen
            ){
                composable<Screens.ProductOverViewScreen> {  }
                composable<Screens.Cart> {  }
                composable<Screens.Notification> {  }
                composable<Screens.Categories> {  }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier.padding(12.dp)
            ){
                BurgersBottomBar(
                    selected = selectedDestinaion,
                    onSelect = { destination ->
                        navController.navigate(destination.screen){
                            launchSingleTop = true
                            popUpTo<Screens.ProductOverViewScreen>{
                                saveState = true
                                inclusive = false
                            }
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}