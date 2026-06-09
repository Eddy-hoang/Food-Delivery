package com.example.fooddeliveryapp.feature.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fooddeliveryapp.core.data.domain.CustomerRepository
import com.example.fooddeliveryapp.core.data.models.Customer
import com.example.fooddeliveryapp.feature.util.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val customerRepository: CustomerRepository
): ViewModel() {

    var customerState by mutableStateOf<Customer?>(null)
        private set

    init {
        observeCustomer()
    }

    private fun observeCustomer() {
        viewModelScope.launch {
            customerRepository.readCustomerFlow().collectLatest { result ->
                if (result is RequestState.Success) {
                    customerState = result.getSuccessData()
                }
            }
        }
    }

    fun signOut(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ){
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO){
                customerRepository.signOut()
            }
            if (result.isSuccess()){
                onSuccess()
            }else if (result.isError()){
                onError(result.getErrorMessage())
            }
        }
    }
}