package com.example.fooddeliveryapp.feature.admin_panel.manage_product

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.fooddeliveryapp.feature.component.BurgerTextField
import com.example.fooddeliveryapp.feature.component.ErrorCard
import com.example.fooddeliveryapp.feature.component.LoadingCard
import com.example.fooddeliveryapp.feature.component.PrimaryButton
import com.example.fooddeliveryapp.feature.component.dialog.CategoryDialog
import com.example.fooddeliveryapp.feature.util.DisplayResult
import com.example.fooddeliveryapp.feature.util.RequestState
import com.example.fooddeliveryapp.ui.theme.BorderIdle
import com.example.fooddeliveryapp.ui.theme.ButtonPrimary
import com.example.fooddeliveryapp.ui.theme.FontSize
import com.example.fooddeliveryapp.ui.theme.IconPrimary
import com.example.fooddeliveryapp.ui.theme.Resources
import com.example.fooddeliveryapp.ui.theme.Surface
import com.example.fooddeliveryapp.ui.theme.SurfaceBrand
import com.example.fooddeliveryapp.ui.theme.SurfaceDarker
import com.example.fooddeliveryapp.ui.theme.SurfaceLighter
import com.example.fooddeliveryapp.ui.theme.TextPrimary
import com.example.fooddeliveryapp.ui.theme.TextSecondary
import com.example.fooddeliveryapp.ui.theme.oswaldVariableFont
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageProductScreen(
    navigateBack: () -> Unit,
    id: String?
) {
    val viewModel = koinViewModel<ManageProductViewModel>()
    val screenState = viewModel.screenState
    var showToast by remember { mutableStateOf("") }
    val isFormValid = viewModel.isFormValid
    val createProductState = viewModel.createProductState.collectAsState()
    var dropdownMenuOpened by remember { mutableStateOf(false) }
    val deleteProductState by viewModel.deleteProductState.collectAsState()

    val context = LocalContext.current
    val appContext = context.applicationContext

    val productImageUploadState = viewModel.productImageState

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            viewModel.uploadProductImageToStorage(uri)
        }
    )

    // Các hiệu ứng (giữ nguyên)
    LaunchedEffect(createProductState.value) {
        val state = createProductState.value
        if (state.isSuccess()) {
            Toast.makeText(appContext, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show()
            navigateBack()
            viewModel.resetCreateProductState()
        }
        if (state.isError()) {
            Toast.makeText(appContext, state.getErrorMessage(), Toast.LENGTH_LONG).show()
            viewModel.resetCreateProductState()
        }
    }

    LaunchedEffect(deleteProductState) {
        if (deleteProductState.isSuccess()) {
            showToast = "New product deleted successfully!"
            navigateBack()
            viewModel.resetDeleteProductState()
        }
        if (deleteProductState.isError()) {
            showToast = deleteProductState.getErrorMessage()
        }
    }

    AnimatedVisibility(
        visible = screenState.isCategoryDialogOpen
    ) {
        CategoryDialog(
            categories = screenState.allCategories,
            onDismiss = viewModel::onCategoryDialogDismiss,
            onSelectedCategory = viewModel::onCategorySelected
        )
    }

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (id == null) "New Product" else "Edit Product",
                        fontFamily = oswaldVariableFont(),
                        fontSize = 20.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            painter = painterResource(Resources.Icon.BackArrow),
                            contentDescription = "Back",
                            tint = IconPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {
                    id?.let {
                        Box {
                            IconButton(onClick = { dropdownMenuOpened = true }) {
                                Icon(
                                    painter = painterResource(Resources.Icon.VerticalMenu),
                                    contentDescription = "More options",
                                    tint = IconPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            DropdownMenu(
                                expanded = dropdownMenuOpened,
                                onDismissRequest = { dropdownMenuOpened = false },
                                containerColor = Surface,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                DropdownMenuItem(
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(Resources.Icon.Delete),
                                            contentDescription = "Delete",
                                            tint = ButtonPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    text = {
                                        Text(
                                            text = "Delete Product",
                                            color = ButtonPrimary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    },
                                    onClick = {
                                        dropdownMenuOpened = false
                                        viewModel.deleteProduct(productId = screenState.id)
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
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
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(paddingValues)
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Card ảnh sản phẩm
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clickable(
                            enabled = !productImageUploadState.isLoading()
                        ) {
                            imagePickerLauncher.launch("image/*")
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SurfaceLighter
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        productImageUploadState.DisplayResult(
                            onIdle = {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        modifier = Modifier.size(40.dp),
                                        painter = painterResource(Resources.Icon.Plus),
                                        contentDescription = "Add image",
                                        tint = IconPrimary.copy(alpha = 0.6f)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Tap to add image",
                                        color = TextSecondary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            },
                            onLoading = {
                                LoadingCard(modifier = Modifier.fillMaxSize())
                            },
                            onSuccess = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.TopEnd
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(screenState.productImage)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Product image",
                                        modifier = Modifier.matchParentSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(ButtonPrimary.copy(alpha = 0.9f)),
                                        onClick = {
                                            viewModel.deleteProductImageFromStorage { isSuccess, message ->
                                                Toast.makeText(
                                                    appContext,
                                                    message,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(20.dp),
                                            painter = painterResource(Resources.Icon.Delete),
                                            contentDescription = "Delete image",
                                            tint = Color.White
                                        )
                                    }
                                }
                            },
                            onError = { message ->
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    ErrorCard(message = message)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    TextButton(
                                        onClick = {
                                            viewModel.updateImageState(RequestState.Idle)
                                        }
                                    ) {
                                        Text(
                                            text = "Try Again",
                                            fontSize = 14.sp,
                                            color = ButtonPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        )
                    }
                }

                // Card chứa các trường nhập liệu
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SurfaceLighter
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BurgerTextField(
                            value = screenState.title,
                            onValueChange = viewModel::updateTitle,
                            placeholder = "Title",
                            modifier = Modifier.fillMaxWidth()
                        )
                        BurgerTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            value = screenState.description,
                            onValueChange = viewModel::updateDescription,
                            placeholder = "Description",
                            expanded = true
                        )
                        BurgerTextField(
                            modifier = Modifier.fillMaxWidth(),
                            text = screenState.selectedCategory?.title ?: "",
                            onClick = viewModel::onCategoryFieldClick,
                            placeholder = "Select Category"
                        )
                        BurgerTextField(
                            value = if (screenState.energyValue == null) "" else screenState.energyValue.toString(),
                            onValueChange = { viewModel.updateEnergyValue(it.toIntOrNull() ?: 0) },
                            placeholder = "Energy Value",
                            keyBoardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        BurgerTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            value = screenState.allergyAdvice,
                            onValueChange = viewModel::updateAllergyAdvice,
                            placeholder = "Allergy Advice",
                            expanded = true,
                        )
                        BurgerTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            value = screenState.ingredients,
                            onValueChange = viewModel::updateIngredients,
                            expanded = true,
                            placeholder = "Ingredients"
                        )
                        BurgerTextField(
                            value = if (screenState.price == 0.0) "" else "${screenState.price}",
                            onValueChange = { value ->
                                if (value.isEmpty() || value.toDoubleOrNull() != null) {
                                    viewModel.updatePrice(value.toDoubleOrNull() ?: 0.0)
                                }
                            },
                            placeholder = "Price",
                            keyBoardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            )
                        )
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    modifier = Modifier.padding(start = 12.dp),
                                    text = "New",
                                    fontSize = FontSize.REGULAR,
                                    color = TextPrimary
                                )
                                Switch(
                                    checked = screenState.isNew,
                                    onCheckedChange = viewModel::updateIsNew,
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    modifier = Modifier.padding(start = 12.dp),
                                    text = "Popular",
                                    fontSize = FontSize.REGULAR,
                                    color = TextPrimary
                                )
                                Switch(
                                    checked = screenState.isPopular,
                                    onCheckedChange = viewModel::updateIsPopular,
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    modifier = Modifier.padding(start = 12.dp),
                                    text = "Discounted",
                                    fontSize = FontSize.REGULAR,
                                    color = TextPrimary
                                )
                                Switch(
                                    checked = screenState.isDiscounted,
                                    onCheckedChange = viewModel::updateIsDiscounted,
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
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
            PrimaryButton(
                text = if (id == null) "Add New Product" else "Update Product",
                icon = if (id == null) painterResource(Resources.Icon.Plus)
                else painterResource(Resources.Icon.Checkmark),
                enabled = isFormValid && !productImageUploadState.isLoading(),
                onClick = {
                    if (id != null) {
                        viewModel.updateProductDetails()
                    } else {
                        viewModel.createNewProduct()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(56.dp)
            )
        }
    }
}