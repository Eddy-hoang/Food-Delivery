package com.example.fooddeliveryapp.feature.home.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun BurgerTextField (
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    error: Boolean = false,
    expanded: Boolean = false,
    keyBoardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text
    )
){
}