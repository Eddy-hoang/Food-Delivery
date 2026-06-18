package com.example.fooddeliveryapp.feature.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.example.fooddeliveryapp.feature.util.Alpha
import com.example.fooddeliveryapp.ui.theme.BorderError
import com.example.fooddeliveryapp.ui.theme.BorderIdle
import com.example.fooddeliveryapp.ui.theme.FontSize
import com.example.fooddeliveryapp.ui.theme.IconSecondary
import com.example.fooddeliveryapp.ui.theme.SurfaceDarker
import com.example.fooddeliveryapp.ui.theme.SurfaceLighter
import com.example.fooddeliveryapp.ui.theme.TextPrimary

@Composable
fun BurgerTextField(
    modifier: Modifier = Modifier,
    value: String = "",
    onValueChange: (String) -> Unit = {},
    text: String? = null,
    iconUrl: String? = null,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    error: Boolean = false,
    expanded: Boolean = false,
    placeholder: String? = null,
    keyBoardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Text
    )
) {
    val borderColor by animateColorAsState(
        targetValue = if (error) BorderError else BorderIdle
    )

    val isClickable = onClick != null
    val displayValue = text ?: value

    Box(modifier = modifier) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(6.dp)
                )
                .clip(RoundedCornerShape(6.dp)),
            enabled = enabled && !isClickable,
            readOnly = isClickable,
            value = displayValue,
            onValueChange = onValueChange,
            leadingIcon = if (iconUrl != null) {
                {
                    AsyncImage(
                        model = iconUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            } else null,
            placeholder = if (placeholder != null) {
                {
                    Text(
                        text = placeholder,
                        fontSize = FontSize.REGULAR
                    )
                }
            } else null,
            singleLine = !expanded,
            shape = RoundedCornerShape(6.dp),
            keyboardOptions = keyBoardOptions,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = SurfaceLighter,
                focusedContainerColor = SurfaceLighter,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                disabledTextColor = TextPrimary,
                focusedPlaceholderColor = TextPrimary.copy(Alpha.HALF),
                unfocusedPlaceholderColor = TextPrimary.copy(Alpha.HALF),
                disabledPlaceholderColor = TextPrimary.copy(Alpha.HALF),
                disabledContainerColor = SurfaceLighter,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                selectionColors = TextSelectionColors(
                    handleColor = IconSecondary,
                    backgroundColor = Color.Unspecified
                )
            )
        )
        
        // Overlay bắt click nằm trên cùng
        if (isClickable) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .zIndex(1f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onClick?.invoke()
                    }
            )
        }
    }
}
