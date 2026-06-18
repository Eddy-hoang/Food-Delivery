package com.example.fooddeliveryapp.feature.util

import android.os.Message
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlin.time.Duration

object MessageUtils {
    @Composable
    fun ShowToast(message: String, duration: Int = Toast.LENGTH_SHORT){
        val context = LocalContext.current
        LaunchedEffect(message) {
            if (message.isNotEmpty()) {
                Toast.makeText(context, message, duration).show()
            }
        }
    }
}