package com.example.fooddeliveryapp.feature.payment.momo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class MoMoPaymentCoordinator(
    private val appContext: Context
) {
    sealed class Event {
        data object Started : Event()
        data class Success(val orderId: String, val requestId: String) : Event()
        data class Failed(val message: String) : Event()
        data object Canceled : Event()
    }

    private val tag = "MoMoCoordinator"

    private val _events = MutableSharedFlow<Event>(
        replay = 0,
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<Event> = _events

    private var currentOrderId: String? = null
    private var currentRequestId: String? = null

    fun startPayment(
        activity: ComponentActivity,
        payUrl: String,
        orderId: String,
        requestId: String
    ) {
        if (payUrl.isBlank()) {
            emit(Event.Failed("PayUrl is empty."))
            return
        }

        currentOrderId = orderId
        currentRequestId = requestId
        emit(Event.Started)

        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(payUrl))
            activity.startActivity(intent)
            Log.d(tag, "startPayment() -> Opened MoMo URL: $payUrl")
        } catch (e: Exception) {
            Log.e(tag, "startPayment() -> Error: ${e.message}")
            emit(Event.Failed("Could not open MoMo app or browser."))
        }
    }

    // Hàm này sẽ được gọi từ MainActivity khi nhận được Deep Link trả về từ MoMo
    fun handleResultIntent(intent: Intent?) {
        if (intent == null || intent.data == null) return

        val uri = intent.data
        Log.d(tag, "handleResultIntent: data=$uri")

        // MoMo trả về các params như errorCode, message qua URL callback
        val errorCode = uri?.getQueryParameter("errorCode")?.toIntOrNull() ?: -1
        val message = uri?.getQueryParameter("message") ?: "Unknown error"

        if (errorCode == 0) {
            emit(Event.Success(currentOrderId ?: "", currentRequestId ?: ""))
        } else if (errorCode == 1006 || errorCode == 9000) {
            emit(Event.Canceled)
        } else {
            emit(Event.Failed(message))
        }
        
        currentOrderId = null
        currentRequestId = null
    }

    private fun emit(event: Event) {
        _events.tryEmit(event)
    }
}
