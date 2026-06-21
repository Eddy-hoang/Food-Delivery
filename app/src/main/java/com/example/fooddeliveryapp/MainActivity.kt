package com.example.fooddeliveryapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.fooddeliveryapp.feature.nav.FoodNavGraph
import com.example.fooddeliveryapp.feature.payment.momo.MoMoPaymentCoordinator
import com.example.fooddeliveryapp.ui.theme.FoodDeliveryAppTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    
    private val moMoCoordinator: MoMoPaymentCoordinator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        handleIntent(intent)

        setContent {
            FoodDeliveryAppTheme {
                FoodNavGraph()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        moMoCoordinator.handleResultIntent(intent)
    }
}
