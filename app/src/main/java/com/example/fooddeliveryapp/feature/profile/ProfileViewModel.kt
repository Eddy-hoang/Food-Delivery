package com.example.fooddeliveryapp.feature.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddeliveryapp.core.data.domain.CustomerRepository
import com.example.fooddeliveryapp.core.data.models.Customer
import com.example.fooddeliveryapp.core.data.models.PhoneNumber
import com.example.fooddeliveryapp.feature.util.RequestState
import kotlinx.coroutines.launch

data class ProfileScreenState(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val city: String? = null,
    val address: String? = null,
    val postalCode: Int? = null,
    val phoneNumber: PhoneNumber? = null,
    val isAdmin: Boolean = false,
    val profilePictureUrl: String? = null
)

class ProfileViewModel(
    private val customerRepository: CustomerRepository
) : ViewModel() {
    var screenReady: RequestState<Unit> by mutableStateOf(RequestState.Loading)
    var screenState: ProfileScreenState by mutableStateOf(ProfileScreenState())
        private set

    val isFormValid: Boolean
        get() = with(screenState) {
            firstName.isNotBlank() && firstName.length >= 2 &&
            lastName.isNotBlank() && lastName.length >= 2
        }

    init {
        viewModelScope.launch { observeCustomer() }
    }

    private suspend fun observeCustomer() {
        customerRepository.readCustomerFlow().collect { data ->
            when (data) {
                is RequestState.Success -> {
                    val fetched = data.getSuccessData()
                    screenState = ProfileScreenState(
                        id = fetched.id,
                        firstName = fetched.firstName,
                        lastName = fetched.lastName,
                        email = fetched.email,
                        city = fetched.city,
                        postalCode = fetched.postalCode,
                        address = fetched.address,
                        phoneNumber = fetched.phoneNumber,
                        profilePictureUrl = fetched.profilePictureUrl,
                        isAdmin = fetched.isAdmin
                    )
                    screenReady = RequestState.Success(Unit)
                }
                is RequestState.Error -> {
                    screenReady = RequestState.Error(data.getErrorMessage())
                }
                else -> Unit
            }
        }
    }

    fun updateFirstName(value: String) {
        screenState = screenState.copy(firstName = value)
    }

    fun updateLastName(value: String) {
        screenState = screenState.copy(lastName = value)
    }

    fun updateCity(value: String) {
        screenState = screenState.copy(city = value)
    }

    fun updateAddress(value: String) {
        screenState = screenState.copy(address = value)
    }

    fun updatePostalCode(value: Int?) {
        screenState = screenState.copy(postalCode = value)
    }

    fun updatePhoneNumber(value: String) {
        val currentDialCode = screenState.phoneNumber?.dialCode ?: 0
        screenState = screenState.copy(
            phoneNumber = PhoneNumber(
                dialCode = currentDialCode,
                number = value
            )
        )
    }

    fun updateCustomer(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            customerRepository.updateCustomer(
                customer = Customer(
                    id = screenState.id,
                    firstName = screenState.firstName,
                    lastName = screenState.lastName,
                    email = screenState.email,
                    city = screenState.city,
                    postalCode = screenState.postalCode,
                    address = screenState.address,
                    phoneNumber = screenState.phoneNumber,
                    profilePictureUrl = screenState.profilePictureUrl,
                    isAdmin = screenState.isAdmin
                ),
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }
}
