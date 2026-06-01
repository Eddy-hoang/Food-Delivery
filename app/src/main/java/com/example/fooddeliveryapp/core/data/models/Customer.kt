package com.example.fooddeliveryapp.core.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val id: String = "", // Đã thêm = ""
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val city: String? = null,
    val address: String? = null,
    val postalCode: Int? = null,
    val phoneNumber: PhoneNumber? = null,
    val isAdmin: Boolean = false,
    val profilePictureUrl: String? = null // Đã thêm = null
)

@Serializable
data class PhoneNumber(
    val dialCode: Int = 0, // Đã thêm = 0
    val number: String = "" // Đã thêm = ""
)