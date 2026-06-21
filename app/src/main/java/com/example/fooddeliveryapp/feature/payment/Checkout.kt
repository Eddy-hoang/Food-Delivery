package com.example.fooddeliveryapp.feature.payment

import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fooddeliveryapp.feature.component.InfoCard
import com.example.fooddeliveryapp.feature.payment.momo.MoMoCheckoutViewModel
import com.example.fooddeliveryapp.feature.payment.momo.MoMoPaymentResult
import com.example.fooddeliveryapp.feature.payment.paypal.CheckoutViewModel
import com.example.fooddeliveryapp.feature.util.Alpha
import com.example.fooddeliveryapp.feature.util.DisplayResult
import com.example.fooddeliveryapp.feature.util.MessageUtils
import com.example.fooddeliveryapp.feature.util.RequestState
import com.example.fooddeliveryapp.ui.theme.BrandBrown
import com.example.fooddeliveryapp.ui.theme.BrandYellow
import com.example.fooddeliveryapp.ui.theme.FontSize
import com.example.fooddeliveryapp.ui.theme.IconPrimary
import com.example.fooddeliveryapp.ui.theme.Resources
import com.example.fooddeliveryapp.ui.theme.Surface
import com.example.fooddeliveryapp.ui.theme.SurfaceBrand
import com.example.fooddeliveryapp.ui.theme.SurfaceDarker
import com.example.fooddeliveryapp.ui.theme.SurfaceLighter
import com.example.fooddeliveryapp.ui.theme.TextPrimary
import com.example.fooddeliveryapp.ui.theme.TextWhite
import com.example.fooddeliveryapp.ui.theme.oswaldVariableFont
import org.koin.androidx.compose.koinViewModel

enum class PaymentMethod {
    Card,
    Momo
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navigateBack: () -> Unit,
    navigateCart: () -> Unit,
    totalAmount: Double
) {
    val checkoutVm = koinViewModel<CheckoutViewModel>()
    val moMoVm = koinViewModel<MoMoCheckoutViewModel>()

    val checkoutUiState by checkoutVm.uiState.collectAsStateWithLifecycle()
    val moMoUiState by moMoVm.uiState.collectAsStateWithLifecycle()

    var method by remember { mutableStateOf(PaymentMethod.Card) }
    var savedCard by remember { mutableStateOf(true) }

    val activity = LocalContext.current

    MessageUtils.ShowToast(message = moMoUiState.toast)

    LaunchedEffect(moMoUiState.toast) {
        if (moMoUiState.toast.isNotBlank()) moMoVm.consumeToast()
    }

    LaunchedEffect(moMoUiState.navigateToCart) {
        if (moMoUiState.navigateToCart) {
            moMoVm.consumeNavigateToCart()
            navigateCart()
        }
    }

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Checkout",
                        fontFamily = oswaldVariableFont(),
                        fontSize = FontSize.LARGE,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            painter = painterResource(Resources.Icon.BackArrow),
                            contentDescription = "Back arrow icon",
                            tint = IconPrimary
                        )
                    }
                },
                actions = {
                    Text(
                        text = "${"%.2f".format(totalAmount)}đ",
                        fontFamily = oswaldVariableFont(),
                        fontSize = FontSize.MEDIUM,
                        fontWeight = FontWeight.Bold,
                        color = BrandBrown,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Surface,
                    scrolledContainerColor = Surface,
                    navigationIconContentColor = IconPrimary,
                    titleContentColor = TextPrimary,
                    actionIconContentColor = IconPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PaymentMethodToggle(
                selected = method,
                onSelect = { method = it }
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(SurfaceDarker)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (method == PaymentMethod.Card) {
                        CardPaymentForm(
                            savedCard = savedCard,
                            onToggleSave = { savedCard = it }
                        )
                    } else {
                        MoMoSection(moMoUiState.state)
                    }
                }
            }

            checkoutUiState.delivery.DisplayResult(
                onLoading = {
                    DeliveryDetailsCard(
                        address = "Loading...",
                        postCode = "Loading...",
                        onEditAddress = {},
                        onEditPostcode = {}
                    )
                },
                onError = {
                    DeliveryDetailsCard(
                        address = "Unknown",
                        postCode = "Unknown",
                        onEditAddress = {},
                        onEditPostcode = {}
                    )
                },
                onSuccess = { delivery ->
                    DeliveryDetailsCard(
                        address = delivery.addressLine,
                        postCode = delivery.postcode,
                        onEditAddress = {},
                        onEditPostcode = {}
                    )
                }
            )

            Button(
                onClick = {
                    if (method == PaymentMethod.Card) {
                        // Logic cho Card (Stub)
                        return@Button
                    }
                    moMoVm.startMoMoPayment(
                        activity as ComponentActivity,
                        totalAmount
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(BrandYellow)
            ) {
                Text(
                    text = "CONFIRM & PAY",
                    fontFamily = oswaldVariableFont(),
                    fontSize = FontSize.REGULAR,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun MoMoSection(state: RequestState<MoMoPaymentResult>) {
    state.DisplayResult(
        onIdle = {
            InfoCard(
                image = Resources.Image.MomoLogo,
                title = "MoMo sẵn sàng",
                subtitle = "Nhấn XÁC NHẬN & THANH TOÁN để tiếp tục."
            )
        },
        onLoading = {
            InfoCard(
                image = Resources.Image.MomoLogo,
                title = "Đang xử lý...",
                subtitle = "Chúng tôi đang kết nối bảo mật với MoMo."
            )
        },
        onError = { msg ->
            InfoCard(
                image = Resources.Icon.Dog,
                title = "Lỗi!",
                subtitle = msg
            )
        },
        onSuccess = { result ->
            InfoCard(
                image = Resources.Image.MomoLogo,
                title = "Thanh toán thành công",
                subtitle = "Mã đơn hàng: ${result.orderId}"
            )
        }
    )
}

@Composable
private fun PaymentMethodToggle(
    selected: PaymentMethod,
    onSelect: (PaymentMethod) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TogglePill(
            text = "Thẻ",
            selected = selected == PaymentMethod.Card,
            onClick = { onSelect(PaymentMethod.Card) },
            leadingIcon = Resources.Icon.Card
        )
        Spacer(modifier = Modifier.width(8.dp))
        TogglePill(
            text = "MoMo",
            selected = selected == PaymentMethod.Momo,
            onClick = { onSelect(PaymentMethod.Momo) },
            leadingIcon = Resources.Image.MomoLogo
        )
    }
}

@Composable
private fun TogglePill(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    leadingIcon: Int
) {
    val background = if (selected) BrandBrown else SurfaceLighter
    val foreground = if (selected) TextWhite else TextPrimary

    Button(
        onClick = onClick,
        modifier = Modifier.height(44.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(background),
        border = BorderStroke(1.dp, BrandBrown)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            color = foreground
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            painter = painterResource(leadingIcon),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun CardPaymentForm(
    savedCard: Boolean,
    onToggleSave: (Boolean) -> Unit
) {
    Text(
        text = "CARD NUMBER",
        fontSize = FontSize.REGULAR,
        color = TextPrimary.copy(alpha = Alpha.HALF)
    )
    Spacer(modifier = Modifier.height(4.dp))
    OutlinedTextField(
        value = "1234 5678 9012 3456",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        trailingIcon = {
            Icon(
                painter = painterResource(Resources.Icon.MasterCard),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedTextColor = TextPrimary
        )
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = "NAME ON CARD",
        fontSize = FontSize.REGULAR,
        color = TextPrimary.copy(alpha = Alpha.HALF)
    )
    Spacer(modifier = Modifier.height(4.dp))
    OutlinedTextField(
        value = "Stephen Nnamani",
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedTextColor = TextPrimary
        )
    )

    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = "EXPIRY DATE",
        fontSize = FontSize.REGULAR,
        color = TextPrimary.copy(alpha = Alpha.HALF)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        DropdownStub(value = "01", modifier = Modifier.weight(1f))
        DropdownStub(value = "26", modifier = Modifier.weight(1f))
    }
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = "CVV",
        fontSize = FontSize.REGULAR,
        color = TextPrimary.copy(alpha = Alpha.HALF)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        CvvBox(value = "6")
        CvvBox(value = "1")
        CvvBox(value = "2")
    }
    Spacer(modifier = Modifier.height(12.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Save card details",
            fontSize = FontSize.REGULAR,
            color = TextPrimary
        )
        Switch(
            checked = savedCard,
            onCheckedChange = onToggleSave,
            colors = SwitchDefaults.colors(
                checkedTrackColor = SurfaceBrand,
                uncheckedTrackColor = SurfaceDarker,
                checkedThumbColor = Surface,
                uncheckedThumbColor = Surface,
                checkedBorderColor = SurfaceBrand,
                uncheckedBorderColor = SurfaceDarker
            )
        )
    }
}

@Composable
private fun DropdownStub(
    value: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.width(120.dp),
        singleLine = true,
        trailingIcon = {
            Icon(
                painter = painterResource(Resources.Icon.Dropdown),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandYellow,
            unfocusedTextColor = TextPrimary
        )
    )
}

@Composable
private fun CvvBox(value: String) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(54.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandYellow,
            unfocusedTextColor = TextPrimary
        )
    )
}

@Composable
private fun DeliveryDetailsCard(
    address: String,
    postCode: String?,
    onEditAddress: () -> Unit,
    onEditPostcode: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(Surface)
    ) {
        Column(
            modifier = Modifier
                .border(
                    1.dp,
                    BrandBrown,
                    RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = "Delivery details:",
                fontSize = FontSize.REGULAR,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            DeliveryRow(value = address, onEdit = onEditAddress)
            Spacer(modifier = Modifier.height(8.dp))
            DeliveryRow(value = postCode ?: "Unknown", onEdit = onEditPostcode)
        }
    }
}

@Composable
private fun DeliveryRow(
    value: String,
    onEdit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50.dp))
            .background(SurfaceLighter)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = value,
            fontSize = FontSize.REGULAR,
            color = TextPrimary,
        )
        Spacer(modifier = Modifier.width(12.dp))
        OutlinedButton(
            onClick = onEdit,
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier.width(100.dp),
            colors = ButtonDefaults.outlinedButtonColors(Surface)
        ) {
            Text(
                text = "Edit",
                fontSize = FontSize.REGULAR,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            )
        }
    }
}
