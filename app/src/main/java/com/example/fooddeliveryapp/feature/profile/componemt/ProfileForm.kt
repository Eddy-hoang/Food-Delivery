package com.example.fooddeliveryapp.feature.profile.componemt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fooddeliveryapp.feature.home.component.BurgerTextField

@Composable
fun ProfileFrom(
    modifier: Modifier = Modifier,
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    email: String,
    city: String,
    onCityChange: (String) -> Unit,
    postalCode: Int?,
    onpostalCodeChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    phoneNumber: String?,
    onPhoneNumberChange: (String) -> Unit,
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),

    ) {
        BurgerTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            placeholder = "First Name",
            error = firstName.length !in 3..10
        )
        BurgerTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            placeholder = "Last Name",
            error = lastName.length !in 3..10
        )
        BurgerTextField(
            value = email,
            onValueChange = {},
            placeholder = "Email",
            enabled = false
        )
        BurgerTextField(
            value = city ?: "",
            onValueChange = onCityChange,
            placeholder = "City",
            error = city.length !in 3..10
        )
        BurgerTextField(
            value = "${postalCode ?: ""}",
            onValueChange = onpostalCodeChange,
            placeholder = "Postal Code",
            error = postalCode == null || firstName.length !in 3..10,
            keyBoardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            )

        )
        BurgerTextField(
            value = address,
            onValueChange = onAddressChange,
            placeholder = "Address",
            error = address.length !in 3..10,
            keyBoardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            )
        )
        BurgerTextField(
            value = phoneNumber ?: "",
            onValueChange = onPhoneNumberChange,
            placeholder = "Phone Number",
            error = phoneNumber.toString().length !in 3..30,
            keyBoardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )
    }
}