package com.example.fooddeliveryapp.core.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val city: String? = null,
    val address: String? = null,
    val postalCode: Int? = null,
    val phoneNumber: PhoneNumber? = null,
    val country: Country? = null,
    val isAdmin: Boolean = false,
    val profilePictureUrl: String? = null
)

@Serializable
data class PhoneNumber(
    @SerialName("CountryCode")
    val dialCode: Int = 0,
    val number: String = ""
)