package com.example.fooddeliveryapp.core.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

// PayPal models (keeping for now to avoid breaking things immediately)
data class CreatePayPalOrderRequest(
    val currencyCode: String,
    val amount: String,
    val referenceId: String? = null
)

data class CreatePayPalOrderResponse(
    val orderId: String
)

data class CapturePayPalOrderRequest(
    val orderId: String
)

data class CapturePayPalOrderResponse(
    val status: String,
    val captureId: String? = null
)

// MoMo models
data class CreateMoMoPaymentRequest(
    val amount: Long,
    val orderId: String,
    val orderInfo: String,
    val requestId: String,
    val extraData: String = ""
)

data class CreateMoMoPaymentResponse(
    val partnerCode: String,
    val orderId: String,
    val requestId: String,
    val amount: Long,
    val responseTime: Long,
    val message: String,
    val resultCode: Int,
    val payUrl: String?,
    val deeplink: String?,
    val qrCodeUrl: String?
)

data class CheckMoMoTransactionRequest(
    val orderId: String,
    val requestId: String
)

data class CheckMoMoTransactionResponse(
    val partnerCode: String,
    val orderId: String,
    val requestId: String,
    val amount: Long,
    val responseTime: Long,
    val message: String,
    val resultCode: Int,
    val transId: Long?
)


interface PaymentApi {
    // PayPal endpoints
    @POST("paypal/create-order")
    suspend fun createPayPalOrder(
        @Body requestBody: CreatePayPalOrderRequest
    ): CreatePayPalOrderResponse

    @POST("paypal/capture-order")
    suspend fun capturePayPalOrder(
        @Body requestBody: CapturePayPalOrderRequest
    ): CapturePayPalOrderResponse

    // MoMo endpoints (assuming these are mirrored in your Firebase Functions)
    @POST("momo/create-payment")
    suspend fun createMoMoPayment(
        @Body request: CreateMoMoPaymentRequest
    ): CreateMoMoPaymentResponse

    @POST("momo/check-status")
    suspend fun checkMoMoTransactionStatus(
        @Body request: CheckMoMoTransactionRequest
    ): CheckMoMoTransactionResponse
}