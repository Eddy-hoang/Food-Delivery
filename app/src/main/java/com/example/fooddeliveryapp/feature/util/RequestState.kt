package com.example.fooddeliveryapp.feature.util

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

sealed class RequestState<out T> {
    object Idle : RequestState<Nothing>()
    object Loading : RequestState<Nothing>()
    data class Success<out T>(val data: T) : RequestState<T>()
    data class Error(val message: String) : RequestState<Nothing>()

    fun isIdle(): Boolean = this is Idle
    fun isLoading(): Boolean = this is Loading
    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error

    fun getSuccessData() = (this as Success).data
    fun getSuccessDataOrNull() = if (this.isSuccess()) this.getSuccessData() else null
    fun getErrorMessage() = (this as Error).message
}

@Composable
fun <T> RequestState<T>.DisplayResult(
    modifier: Modifier = Modifier,
    onIdle: (@Composable () -> Unit)? = null,
    onLoading: @Composable () -> Unit,
    onSuccess: @Composable (T) -> Unit,
    onError: @Composable (String) -> Unit,
) {
    Box(modifier = modifier) {
        when (this@DisplayResult) {
            is RequestState.Idle -> {
                onIdle?.invoke()
            }
            is RequestState.Loading -> {
                onLoading()
            }
            is RequestState.Success -> {
                onSuccess(data)
            }
            is RequestState.Error -> {
                onError(message)
            }
        }
    }
}
