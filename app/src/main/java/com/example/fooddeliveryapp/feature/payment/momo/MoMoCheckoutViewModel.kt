package com.example.fooddeliveryapp.feature.payment.momo

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddeliveryapp.core.data.domain.CartRepository
import com.example.fooddeliveryapp.core.data.domain.PaymentRepository
import com.example.fooddeliveryapp.feature.util.RequestState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class MoMoPaymentResult(
    val orderId: String,
    val resultCode: Int,
    val message: String
)

data class MoMoUiState(
    val state: RequestState<MoMoPaymentResult> = RequestState.Idle,
    val toast: String = "",
    val navigateToCart: Boolean = false
)

class MoMoCheckoutViewModel(
    private val paymentRepository: PaymentRepository,
    private val coordinator: MoMoPaymentCoordinator,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoMoUiState())
    val uiState: StateFlow<MoMoUiState> = _uiState

    init {
        viewModelScope.launch {
            coordinator.events.collect { event ->
                when (event) {
                    is MoMoPaymentCoordinator.Event.Started -> {
                        _uiState.update { it.copy(toast = "Opening MoMo...") }
                    }
                    is MoMoPaymentCoordinator.Event.Success -> {
                        checkMoMoStatus(event.orderId, event.requestId)
                    }
                    is MoMoPaymentCoordinator.Event.Canceled -> {
                        _uiState.update {
                            it.copy(
                                state = RequestState.Idle,
                                toast = "Payment cancelled."
                            )
                        }
                    }
                    is MoMoPaymentCoordinator.Event.Failed -> {
                        _uiState.update {
                            it.copy(
                                state = RequestState.Error(event.message),
                                toast = event.message
                            )
                        }
                    }
                }
            }
        }
    }

    fun startMoMoPayment(activity: ComponentActivity, totalAmount: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(state = RequestState.Loading, toast = "") }

            val amount = totalAmount.toLong()
            val orderId = "momo-order-${System.currentTimeMillis()}"
            val requestId = UUID.randomUUID().toString()
            val orderInfo = "Payment for Food Delivery Order"

            val response = paymentRepository.createMoMoPayment(
                amount = amount,
                orderId = orderId,
                orderInfo = orderInfo,
                requestId = requestId
            ).getOrElse { err ->
                Log.e("MoMoVm", "create payment failed: ${err.message}", err)
                _uiState.update {
                    it.copy(
                        state = RequestState.Error("Failed to create MoMo payment"),
                        toast = "Failed to create MoMo payment"
                    )
                }
                return@launch
            }

            if (response.resultCode == 0 && response.payUrl != null) {
                coordinator.startPayment(activity, response.payUrl, orderId, requestId)
            } else {
                _uiState.update {
                    it.copy(
                        state = RequestState.Error(response.message),
                        toast = response.message
                    )
                }
            }
        }
    }

    private fun checkMoMoStatus(orderId: String, requestId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(state = RequestState.Loading) }

            val statusResponse = paymentRepository.checkMoMoTransactionStatus(orderId, requestId).getOrElse { err ->
                Log.e("MoMoVm", "status check failed: ${err.message}", err)
                _uiState.update {
                    it.copy(
                        state = RequestState.Error("Failed to verify MoMo payment"),
                        toast = "Failed to verify MoMo payment"
                    )
                }
                return@launch
            }

            val result = MoMoPaymentResult(
                orderId = statusResponse.orderId,
                resultCode = statusResponse.resultCode,
                message = statusResponse.message
            )

            if (statusResponse.resultCode == 0) {
                // Success - Clear Cart
                when (val clearState = cartRepository.clearCart()) {
                    is RequestState.Success -> {
                        _uiState.update {
                            it.copy(
                                state = RequestState.Success(result),
                                toast = "Payment completed and cart cleared",
                                navigateToCart = true
                            )
                        }
                    }
                    else -> {
                        _uiState.update {
                            it.copy(
                                state = RequestState.Success(result),
                                toast = "Payment completed",
                                navigateToCart = true
                            )
                        }
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        state = RequestState.Error(statusResponse.message),
                        toast = "Payment failed: ${statusResponse.message}"
                    )
                }
            }
        }
    }

    fun consumeToast() {
        _uiState.update { it.copy(toast = "") }
    }

    fun consumeNavigateToCart() {
        _uiState.update { it.copy(navigateToCart = false) }
    }

    fun reset() {
        _uiState.value = MoMoUiState()
    }
}
