package com.example.fooddeliveryapp.core.data.repolmpl

import android.util.Log
import com.example.fooddeliveryapp.core.data.domain.PaymentRepository
import com.example.fooddeliveryapp.core.data.remote.CapturePayPalOrderRequest
import com.example.fooddeliveryapp.core.data.remote.CapturePayPalOrderResponse
import com.example.fooddeliveryapp.core.data.remote.CheckMoMoTransactionRequest
import com.example.fooddeliveryapp.core.data.remote.CheckMoMoTransactionResponse
import com.example.fooddeliveryapp.core.data.remote.CreateMoMoPaymentRequest
import com.example.fooddeliveryapp.core.data.remote.CreateMoMoPaymentResponse
import com.example.fooddeliveryapp.core.data.remote.CreatePayPalOrderRequest
import com.example.fooddeliveryapp.core.data.remote.PaymentApi

class PaymentRepositoryImpl(
    private val paymentApi: PaymentApi
): PaymentRepository {

    // PayPal implementations
    override suspend fun createPayPalOrder(
        currencyCode: String,
        amount: String,
        referenceId: String?
    ): Result<String>  = runCatching {
        Log.d("BurgerPayments", "createPayPalOrder -> $currencyCode $amount ref = $referenceId ")
        paymentApi.createPayPalOrder(
            CreatePayPalOrderRequest(
                currencyCode = currencyCode,
                amount = amount,
                referenceId = referenceId
            )
        ).orderId
    }.onFailure {
        Log.e("BurgerPayments", "createPayPalOrder failed-> ${it.message}", it)
    }

    override suspend fun capturePayPalOrder(orderId: String): Result<CapturePayPalOrderResponse> = runCatching {
        Log.d("BurgerPayments", "capturePayPalOrder -> $orderId")
        paymentApi.capturePayPalOrder(CapturePayPalOrderRequest(orderId))
    }.onFailure {
        Log.e("BurgerPayments", "capturePayPalOrder failed-> ${it.message}", it)
    }

    // MoMo implementations
    override suspend fun createMoMoPayment(
        amount: Long,
        orderId: String,
        orderInfo: String,
        requestId: String
    ): Result<CreateMoMoPaymentResponse> = runCatching {
        Log.d("MoMoPayments", "createMoMoPayment -> amount=$amount, orderId=$orderId")
        paymentApi.createMoMoPayment(
            CreateMoMoPaymentRequest(
                amount = amount,
                orderId = orderId,
                orderInfo = orderInfo,
                requestId = requestId
            )
        )
    }.onFailure {
        Log.e("MoMoPayments", "createMoMoPayment failed-> ${it.message}", it)
    }

    override suspend fun checkMoMoTransactionStatus(
        orderId: String,
        requestId: String
    ): Result<CheckMoMoTransactionResponse> = runCatching {
        Log.d("MoMoPayments", "checkMoMoTransactionStatus -> orderId=$orderId")
        paymentApi.checkMoMoTransactionStatus(
            CheckMoMoTransactionRequest(orderId, requestId)
        )
    }.onFailure {
        Log.e("MoMoPayments", "checkMoMoTransactionStatus failed-> ${it.message}", it)
    }
}