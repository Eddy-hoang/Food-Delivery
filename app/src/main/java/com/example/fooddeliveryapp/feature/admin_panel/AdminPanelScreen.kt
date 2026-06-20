package com.example.fooddeliveryapp.feature.admin_panel

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.fooddeliveryapp.feature.component.InfoCard
import com.example.fooddeliveryapp.feature.component.LoadingCard
import com.example.fooddeliveryapp.feature.component.ProductCard
import com.example.fooddeliveryapp.feature.util.DisplayResult
import com.example.fooddeliveryapp.ui.theme.BorderIdle
import com.example.fooddeliveryapp.ui.theme.ButtonPrimary
import com.example.fooddeliveryapp.ui.theme.FontSize
import com.example.fooddeliveryapp.ui.theme.IconPrimary
import com.example.fooddeliveryapp.ui.theme.Resources
import com.example.fooddeliveryapp.ui.theme.Surface
import com.example.fooddeliveryapp.ui.theme.SurfaceLighter
import com.example.fooddeliveryapp.ui.theme.TextPrimary
import com.example.fooddeliveryapp.ui.theme.oswaldVariableFont
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    navigateBack: () -> Unit,
    navigateToManageProduct: (String?) -> Unit
){
    val viewModel = koinViewModel<AdminPanelViewModel>()
    val products = viewModel.products.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var searchBarVisible by remember { mutableStateOf(false) }


    Scaffold(
        containerColor = Surface,
        topBar = {
            AnimatedContent(
                targetState = searchBarVisible
            ) { visible ->
                if (visible){
                    SearchBar(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .fillMaxWidth(),
                        inputField = {
                            SearchBarDefaults.InputField(
                                modifier = Modifier.fillMaxWidth(),
                                query = searchQuery,
                                onQueryChange = viewModel::updateSearchQuery,
                                expanded = false,
                                onExpandedChange = {},
                                onSearch = {},
                                placeholder = {
                                    Text(
                                        text = "Search product here",
                                        fontSize = FontSize.REGULAR,
                                        color = TextPrimary
                                    )
                                },
                                trailingIcon = {
                                    IconButton(
                                        modifier = Modifier.
                                        size(14.dp),
                                        onClick = {
                                            if (searchQuery.isNotEmpty()) viewModel.updateSearchQuery("")
                                            else searchBarVisible = false
                                        }) {
                                        Icon(
                                            painter = painterResource(Resources.Icon.Close),
                                            contentDescription = "Close icon",
                                            tint = IconPrimary
                                        )
                                    }
                                }
                            )
                        },
                        colors = SearchBarColors(
                            containerColor = SurfaceLighter,
                            dividerColor = BorderIdle
                        ),
                        expanded = false,
                        onExpandedChange = {},
                        content = {}
                    )
                } else {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Admin Panel",
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
                            IconButton(
                                onClick = { searchBarVisible = true }
                            ) {
                                Icon(
                                    painter = painterResource(Resources.Icon.Search),
                                    contentDescription = "Search icon",
                                    tint = IconPrimary
                                )
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
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {navigateToManageProduct(null)},
                containerColor = ButtonPrimary,
                contentColor = IconPrimary,
                content = {
                    Icon(
                        painter = painterResource(Resources.Icon.Plus),
                        contentDescription = "Add icon"
                    )
                }
            )
        }

    ) {paddingValues ->
        products.value.DisplayResult(
            modifier = Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                ),
            onLoading = { LoadingCard(modifier =Modifier.fillMaxSize()) },
            onSuccess = { latestProducts ->
                Log.d("AdminPanel", "Success, size = ${latestProducts.size}")
                AnimatedContent(
                    targetState = latestProducts
                ) { productList ->
                    if (productList.isNotEmpty()){
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = latestProducts,
                                key = {it.id}
                            ){ product ->
                                ProductCard(
                                    product = product,
                                    onClick = {
                                        navigateToManageProduct(product.id)
                                    }
                                )
                            }
                        }
                    } else {
                        InfoCard(
                            image = Resources.Icon.Dog,
                            title = "Oops!",
                            subtitle = "Product not found"
                        )
                    }
                }
            },
            onError = { message ->
                Log.d("AdminPanel", "Error, message = $message")
                InfoCard(
                    image = Resources.Icon.Dog,
                    title = "Oops!",
                    subtitle = message
                )
            }
        )
    }
}