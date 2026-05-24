package com.example.fooddeliveryapp.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddeliveryapp.core.data.domain.CustomerRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(
    private val customerRepository: CustomerRepository,
) : ViewModel() {
    fun createCustomer(
        user: FirebaseUser,
        onSucess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            customerRepository.createCustomer(
                user = user,
                onSucess = onSucess,
                onError = onError
            )
        }
    }
}

