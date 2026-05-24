package com.example.fooddeliveryapp.feature.util

sealed class RequestState<out T> {
    object Idle : RequestState<Nothing>()
    object Loading : RequestState<Nothing>()
    data class Success<out T>(val theData: T) : RequestState<T>()
    data class Error(val message: String) : RequestState<Nothing>()
}