package com.example.fooddeliveryapp.feature.profile

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.fooddeliveryapp.feature.component.PrimaryButton
import com.example.fooddeliveryapp.feature.component.InfoCard
import com.example.fooddeliveryapp.feature.component.LoadingCard
import com.example.fooddeliveryapp.feature.component.dialog.CountryPickerDialog
import com.example.fooddeliveryapp.feature.profile.component.ProfileForm
import com.example.fooddeliveryapp.feature.profile.component.ProfilePhotoEditor
import com.example.fooddeliveryapp.feature.util.DisplayResult
import com.example.fooddeliveryapp.ui.theme.FontSize
import com.example.fooddeliveryapp.ui.theme.IconPrimary
import com.example.fooddeliveryapp.ui.theme.Resources
import com.example.fooddeliveryapp.ui.theme.Surface
import com.example.fooddeliveryapp.ui.theme.TextPrimary
import com.example.fooddeliveryapp.ui.theme.oswaldVariableFont
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navigateBack: () -> Unit
) {
    val profileViewModel = koinViewModel<ProfileViewModel>()
    val screenState = profileViewModel.screenState
    val screenReady = profileViewModel.screenReady
    val isFormValid = profileViewModel.isFormValid
    val countriesState = profileViewModel.countriesState
    val photoState = profileViewModel.photoState

    var countryDialogOpen by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ){
        uri ->
        if (uri != null){
            profileViewModel.pickAndUploadProfilePhoto(uri)
        }
    }
    val authPhotoUrl = remember {
        FirebaseAuth.getInstance().currentUser?.photoUrl?.toString()
    }
    val resolvedPhotoUrl = screenState.profilePictureUrl?.takeUnless { it.isBlank() } ?: authPhotoUrl

    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My profile",
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
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .imePadding()
        ) {
            if (countryDialogOpen) {
                countriesState.DisplayResult(
                    onLoading = { LoadingCard(modifier = Modifier.fillMaxSize()) },
                    onError = {},
                    onSuccess = { countries ->
                        CountryPickerDialog(
                            countries = countries,
                            selectedCountry = screenState.country,
                            onDismiss = { countryDialogOpen = false },
                            onConfirmClick = { selectedCountry ->
                                profileViewModel.updateCountry(selectedCountry)
                                countryDialogOpen = false
                            }
                        )
                    }
                )
            }
            screenReady.DisplayResult(
                onLoading = { LoadingCard(modifier = Modifier.fillMaxSize()) },
                onSuccess = {
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfilePhotoEditor(
                        photoUrl = resolvedPhotoUrl,
                        isUpLoading = photoState.isUpLoading,
                        progress = photoState.progress,
                        onPickClick = {
                            photoPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )
                    ProfileForm(
                        modifier = Modifier.weight(1f),
                        firstName = screenState.firstName,
                        onFirstNameChange = profileViewModel::updateFirstName,
                        lastName = screenState.lastName,
                        onLastNameChange = profileViewModel::updateLastName,
                        email = screenState.email,
                        city = screenState.city,
                        onCityChange = profileViewModel::updateCity,
                        postalCode = screenState.postalCode,
                        onPostalCodeChange = profileViewModel::updatePostalCode,
                        address = screenState.address,
                        onAddressChange = profileViewModel::updateAddress,
                        phoneNumber = screenState.phoneNumber?.number,
                        onPhoneNumberChange = profileViewModel::updatePhoneNumber,
                        country = screenState.country,
                        onCountrySelect = { countryDialogOpen = true}
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    PrimaryButton(
                        text = "Update",
                        icon = painterResource(Resources.Icon.Checkmark),
                        enabled = isFormValid,
                        onClick = {
                            profileViewModel.updateCustomer(
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        "Profile updated successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onError = { error ->
                                    Toast.makeText(
                                        context,
                                        "Update failed: $error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    )
                },
                onError = { message ->
                    InfoCard(
                        image = Resources.Icon.Dog,
                        title = "Ops",
                        subtitle = message
                    )
                }
            )
        }
    }
}
