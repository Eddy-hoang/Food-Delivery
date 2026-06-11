package com.example.fooddeliveryapp.feature.admin_panel.manage_product

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fooddeliveryapp.feature.component.BurgerTextField
import com.example.fooddeliveryapp.feature.component.PrimaryButton
import com.example.fooddeliveryapp.ui.theme.BorderIdle
import com.example.fooddeliveryapp.ui.theme.FontSize
import com.example.fooddeliveryapp.ui.theme.IconPrimary
import com.example.fooddeliveryapp.ui.theme.Resources
import com.example.fooddeliveryapp.ui.theme.Surface
import com.example.fooddeliveryapp.ui.theme.SurfaceLighter
import com.example.fooddeliveryapp.ui.theme.TextPrimary
import com.example.fooddeliveryapp.ui.theme.oswaldVariableFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageProductScreen(
    navigateBack: () -> Unit,
    id: String?
) {
    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (id == null) "New Product" else "Edit Product",
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
                colors = TopAppBarDefaults.run {
                    topAppBarColors(
                        containerColor = Surface,
                        scrolledContainerColor = Surface,
                        navigationIconContentColor = IconPrimary,
                        titleContentColor = TextPrimary,
                        actionIconContentColor = IconPrimary
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(paddingValues)
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(250.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            width = 1.dp,
                            color = BorderIdle,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(SurfaceLighter),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(Resources.Icon.Plus),
                        contentDescription = "Add icon",
                        tint = IconPrimary
                    )
                }
                BurgerTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = "Title"
                )
                BurgerTextField(
                    modifier = Modifier.height(120.dp),
                    value = "",
                    onValueChange = {},
                    placeholder = "Description",
                    expanded = true
                )
                BurgerTextField(
                    modifier = Modifier.fillMaxWidth(),
                    text = "",
                    onClick = {},
                    placeholder = "Category"
                )
                BurgerTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = "Energy Value",
                    keyBoardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                BurgerTextField(
                    modifier = Modifier.height(80.dp),
                    value = "",
                    onValueChange = {},
                    placeholder = "Allergy Advice",
                    expanded = true,
                    )
                BurgerTextField(
                    modifier = Modifier.height(80.dp),
                    value = "",
                    onValueChange = {},
                    expanded = true,
                    placeholder = "Ingredients"
                )
                BurgerTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = "Price",
                    keyBoardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            PrimaryButton(
                text = if (id == null) "Add New Product" else "Update Product",
                icon = if (id == null) painterResource(Resources.Icon.Plus)
                else painterResource(Resources.Icon.Checkmark),
                enabled = false,
                onClick = {}
            )
        }

    }
}