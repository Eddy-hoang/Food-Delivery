package com.example.fooddeliveryapp.core.data.domain

import com.example.fooddeliveryapp.core.data.remote.CapturePayPalOrderResponse
import com.example.fooddeliveryapp.core.data.remote.CheckMoMoTransactionResponse
import com.example.fooddeliveryapp.core.data.remote.CreateMoMoPaymentResponse


interface PaymentRepository {
    // PayPal methods
    suspend fun createPayPalOrder(
        currencyCode: String,
        amount: String,
        referenceId: String?
    ): Result<String>

    suspend fun capturePayPalOrder(
        orderId: String
    ): Result<CapturePayPalOrderResponse>

    // MoMo methods
    suspend fun createMoMoPayment(
        amount: Long,
        orderId: String,
        orderInfo: String,
        requestId: String
    ): Result<CreateMoMoPaymentResponse>

    suspend fun checkMoMoTransactionStatus(
        orderId: String,
        requestId: String
    ): Result<CheckMoMoTransactionResponse>
}