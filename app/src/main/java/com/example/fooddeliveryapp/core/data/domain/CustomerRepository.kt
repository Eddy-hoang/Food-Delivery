package com.example.fooddeliveryapp.core.data.domain

import com.google.firebase.auth.FirebaseUser

interface CustomerRepository {
    fun getCurrentUserId():String?

    suspend fun CreateCustomer(user: FirebaseUser): Result<Unit>
}