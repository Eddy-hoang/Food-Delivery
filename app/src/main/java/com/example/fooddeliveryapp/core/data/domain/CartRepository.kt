package com.example.fooddeliveryapp.core.data.domain

import com.example.fooddeliveryapp.core.data.models.CartItemUi
import com.example.fooddeliveryapp.feature.util.RequestState
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun observerCartItems(): Flow<RequestState<List<CartItemUi>>>
    suspend fun increment(productId: String, productTitle: String? = null): RequestState<Unit>
    suspend fun decrement(productId: String): RequestState<Unit>
    suspend fun delete(productId: String): RequestState<Unit>
    suspend fun setQuantity(productId: String, quantity: Int): RequestState<Unit>

    suspend fun clearCart(): RequestState<Unit>
}