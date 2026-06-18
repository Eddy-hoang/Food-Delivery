package com.example.fooddeliveryapp.feature.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.fooddeliveryapp.core.data.models.Product
import com.example.fooddeliveryapp.feature.util.Alpha
import com.example.fooddeliveryapp.ui.theme.BorderIdle
import com.example.fooddeliveryapp.ui.theme.FontSize
import com.example.fooddeliveryapp.ui.theme.Resources
import com.example.fooddeliveryapp.ui.theme.SurfaceLighter
import com.example.fooddeliveryapp.ui.theme.TextPrimary
import com.example.fooddeliveryapp.ui.theme.TextSecondary
import com.example.fooddeliveryapp.ui.theme.oswaldVariableFont

@Composable
fun ProductCard(
    modifier: Modifier = Modifier,
    product: Product,
    onClick: (String) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderIdle, RoundedCornerShape(12.dp))
            .background(SurfaceLighter)
            .clickable { onClick(product.id) }
    ) {
        // Cột nội dung (Cần thiết lập weight(1f))
        Column(
            modifier = Modifier
                .weight(1f) // QUAN TRỌNG: Để cột này co giãn
                .fillMaxHeight()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween // Dàn đều các phần tử
        ) {
            Column {
                Text(
                    text = product.title,
                    fontSize = FontSize.MEDIUM,
                    color = TextPrimary,
                    fontFamily = oswaldVariableFont(),
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    modifier = Modifier.alpha(Alpha.HALF),
                    text = product.description,
                    fontSize = FontSize.SMALL,
                    color = TextPrimary,
                    fontFamily = oswaldVariableFont(),
                    fontWeight = FontWeight.Normal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$${product.price}",
                    fontSize = FontSize.REGULAR,
                    color = TextSecondary,
                    fontFamily = oswaldVariableFont(),
                    fontWeight = FontWeight.Bold,
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(Resources.Icon.Flame),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${product.energyValue}kcal",
                        fontSize = FontSize.EXTRA_SMALL,
                        color = TextSecondary,
                        fontFamily = oswaldVariableFont(),
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        AsyncImage(
            modifier = Modifier
                .width(130.dp)
                .fillMaxHeight(),
            model = ImageRequest.Builder(LocalContext.current)
                .data(product.productImage)
                .crossfade(true)
                .build(),
            contentDescription = "Product image",
            contentScale = ContentScale.Crop
        )
    }
}