package com.example.fooddeliveryapp.feature.component.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.fooddeliveryapp.core.data.models.Country
import com.example.fooddeliveryapp.feature.component.BurgerTextField
import com.example.fooddeliveryapp.feature.component.ErrorCard
import com.example.fooddeliveryapp.feature.util.Alpha
import com.example.fooddeliveryapp.ui.theme.FontSize
import com.example.fooddeliveryapp.ui.theme.IconWhite
import com.example.fooddeliveryapp.ui.theme.Resources
import com.example.fooddeliveryapp.ui.theme.SurfaceBrand
import com.example.fooddeliveryapp.ui.theme.SurfaceDarker
import com.example.fooddeliveryapp.ui.theme.SurfaceLighter
import com.example.fooddeliveryapp.ui.theme.TextBrand
import com.example.fooddeliveryapp.ui.theme.TextPrimary

@Composable
fun CountryPickerDialog(
    countries: List<Country>,
    selectedCountry: Country?,
    onDismiss: () -> Unit,
    onConfirmClick: (Country) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var localSelection by remember(selectedCountry) { mutableStateOf(selectedCountry) }

    val filteredCountries = remember(searchQuery, countries) {
        val query = searchQuery.trim().lowercase()
        if (query.isEmpty()) countries
        else countries.filter { country ->
            country.name.lowercase().contains(query) ||
                    "${country.dialCode}".contains(query) ||
                    country.code.lowercase().contains(query)

        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select Country",
                fontSize = FontSize.EXTRA_MEDIUM,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .height(360.dp)
                    .fillMaxWidth()
            ) {
                BurgerTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Dial code or country"
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (filteredCountries.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(
                            items = filteredCountries,
                            key = { it.code }
                        ) { country ->
                            CountryPicker(
                                country = country,
                                isSelected = localSelection?.code == country.code,
                                onClick = {
                                    localSelection = country
                                }
                            )
                        }
                    }

                } else {
                    ErrorCard(
                        modifier = Modifier.weight(1f),
                        message = "Dial code not found"
                    )
                }
            }

        },
        confirmButton = {
            TextButton(
                onClick = { localSelection?.let(onConfirmClick) },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = TextBrand
                )
            ) {
                Text(
                    text = "Confirm",
                    fontSize = FontSize.REGULAR,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = TextPrimary.copy(alpha = Alpha.HALF)
                )
            ) {
                Text(
                    text = "Cancel",
                    fontSize = FontSize.REGULAR,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        containerColor = SurfaceLighter
    )
}

@Composable
fun CountryPicker(
    modifier: Modifier = Modifier,
    country: Country,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        val saturation = remember { Animatable(if (isSelected) 1f else 0f) }
        LaunchedEffect(isSelected) {
            saturation.animateTo(if (isSelected) 1f else 0f)
        }
        val colorMatrix = remember(saturation.value) {
            ColorMatrix().apply {
                setToSaturation(saturation.value)
            }
        }
        AsyncImage(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape),
            model = country.flagUrl,
            contentDescription = "${country.name} flag",
            colorFilter = ColorFilter.colorMatrix(colorMatrix)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "+${country.dialCode} (${country.name})",
            fontSize = FontSize.REGULAR,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Selector(isSelected = isSelected)
    }
}

@Composable
fun Selector(
    modifier: Modifier = Modifier,
    isSelected: Boolean
) {
    val animateBackground by animateColorAsState(
        targetValue = if (isSelected) SurfaceBrand else SurfaceDarker,
    )
    Box(
        modifier = modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(animateBackground),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isSelected
        ) {
            Icon(
                modifier = modifier.size(14.dp),
                painter = painterResource(Resources.Icon.Checkmark),
                contentDescription = "Checkmark icon",
                tint = IconWhite
            )
        }
    }
}
