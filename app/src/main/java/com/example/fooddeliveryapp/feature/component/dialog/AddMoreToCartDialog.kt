package com.example.fooddeliveryapp.feature.component.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fooddeliveryapp.core.data.models.Product
import com.example.fooddeliveryapp.feature.component.CartProductCard
import com.example.fooddeliveryapp.feature.component.InfoCard
import com.example.fooddeliveryapp.feature.component.LoadingCard
import com.example.fooddeliveryapp.feature.component.QuantityStepper
import com.example.fooddeliveryapp.feature.util.Alpha
import com.example.fooddeliveryapp.feature.util.DisplayResult
import com.example.fooddeliveryapp.feature.util.RequestState
import com.example.fooddeliveryapp.ui.theme.BrandYellow
import com.example.fooddeliveryapp.ui.theme.FontSize
import com.example.fooddeliveryapp.ui.theme.IconPrimary
import com.example.fooddeliveryapp.ui.theme.Resources
import com.example.fooddeliveryapp.ui.theme.Surface
import com.example.fooddeliveryapp.ui.theme.TextPrimary
import com.example.fooddeliveryapp.ui.theme.oswaldVariableFont

@Composable
fun AddMoreToCartDialog(
    suggestedProducts: RequestState<List<Product>>,
    selectedQuantities: Map<String, Int>,
    initialItemTotal: Double,
    onDismiss: () -> Unit,
    onProductClick: (String) -> Unit,
    onIncrement: (String) -> Unit,
    onDecrement: (String) -> Unit,
    gotoCart: () -> Unit
) {
    val products = (suggestedProducts as? RequestState.Success)?.data.orEmpty()
    val total = products.sumOf { product -> (selectedQuantities[product.id] ?: 0) * product.price }
    val grandTotal = initialItemTotal + total
    
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = {
            Text(
                text = "Something extra?",
                fontSize = FontSize.MEDIUM,
                color = TextPrimary,
                fontFamily = oswaldVariableFont(),
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Box(modifier = Modifier.heightIn(max = 400.dp)) { // Giới hạn chiều cao để không chiếm hết màn hình
                suggestedProducts.DisplayResult(
                    onIdle = {
                        Text(
                            modifier = Modifier.alpha(Alpha.HALF),
                            text = "No suggestions yet.",
                            fontFamily = oswaldVariableFont(),
                            fontSize = FontSize.REGULAR,
                            color = TextPrimary
                        )
                    },
                    onLoading = { LoadingCard(modifier = Modifier.fillMaxWidth().height(100.dp)) },
                    onError = { message ->
                        InfoCard(
                            image = Resources.Icon.Dog,
                            title = "Oops!",
                            subtitle = message
                        )
                    },
                    onSuccess = { productsList ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()), // Thêm scroll ở đây
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            productsList.take(10).forEach { product ->
                                val qty = selectedQuantities[product.id] ?: 0

                                Box(modifier = Modifier.fillMaxWidth()) {
                                    CartProductCard(
                                        product = product,
                                        onClick = onProductClick,
                                        trailingContent = {
                                            QuantityStepper(
                                                quantity = qty,
                                                minValue = 0,
                                                onMinusClick = { onDecrement(product.id)},
                                                onPlusClick = { onIncrement(product.id)}
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = gotoCart,
                modifier = Modifier
                    .height(46.dp)
                    .width(180.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(BrandYellow)
            ){
                Text(
                    text = "Checkout (${"%.2f".format(grandTotal)}£)",
                    fontWeight = FontWeight.Bold,
                    fontSize = FontSize.REGULAR,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(Resources.Icon.ShoppingCart),
                    contentDescription = "Shopping cart icon",
                    modifier = Modifier.size(16.dp),
                    tint = IconPrimary
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Icon(
                    painter = painterResource(Resources.Icon.Close),
                    contentDescription = "Close icon",
                    modifier = Modifier.size(16.dp),
                    tint = IconPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Close",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = FontSize.REGULAR,
                    color = TextPrimary,
                    fontFamily = oswaldVariableFont()
                )
            }
        },
    )
}
