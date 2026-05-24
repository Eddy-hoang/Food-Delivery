package com.example.fooddeliveryapp.feature.auth

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.core.data.auth.GoogleUiClient
import com.example.fooddeliveryapp.feature.component.GoogleButton
import com.example.fooddeliveryapp.ui.theme.FontSize
import com.example.fooddeliveryapp.ui.theme.Resources
import com.example.fooddeliveryapp.ui.theme.oswaldVariableFont
import com.example.fooddeliveryapp.feature.component.PrimaryButton
import com.example.fooddeliveryapp.feature.util.RequestState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun AuthScreen(
    navigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity
    val scope = rememberCoroutineScope ()

   // Use koinViewModel for ViewModels a   nd koinInject for regular components
    val authViewModel: AuthViewModel = koinViewModel()
    val googleAuthUiClient: GoogleUiClient = koinInject()
    var  loaddingState by remember { mutableStateOf(false)  }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.burgers),
                    contentDescription = " Burgers logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(
                            width = 220.dp,
                            height = 130.dp
                        )
                )
                Text(
                    text = stringResource(R.string.sign_in_text),
                    fontFamily = oswaldVariableFont(),
                    fontSize = FontSize.MEDIUM
                )

            }
            GoogleButton(
                loading = loaddingState,
                onClick = {
                    scope.launch {
                        loaddingState = true
                        try{
                            val authResult = googleAuthUiClient.signInWithGoogle(activity)
                            val user = authResult.user
                            if(user != null){
                                authViewModel.createCustomer(
                                    user = user,
                                    onSucess = {
                                        RequestState.Success(user)
                                    },
                                    onError = {
                                        RequestState.Error("Unable to create new user")
                                    }
                                )
                                delay(200)
                                navigateToHome()
                            }else{
                                RequestState.Error("User is null")
                            }
                        }catch (e: Exception){
                            RequestState.Error(e.message?:"Sign-in error.")
                        }
                    }
                },
                icon = painterResource(Resources.Image.GoogleLogo),
            )

            Spacer(modifier = Modifier.height(14.dp))

            PrimaryButton(
                text= stringResource(R.string.guest_text),
                icon = painterResource(R.drawable.log_in),
                onClick = {
                    scope.launch {
                        try {
                            val guestResult = googleAuthUiClient.guestSign()
                            val user = guestResult.user
                            if(user != null){
                                authViewModel.createCustomer(
                                    user = user,
                                    onSucess = {
                                        RequestState.Success(user)
                                    },
                                    onError = {
                                        RequestState.Error("Unable to create new user")
                                    }
                                )
                                delay(200)
                                navigateToHome()
                            }else{
                                RequestState.Error("Guest sign-in failed.")
                            }
                        }catch (e: Exception){
                            RequestState.Error(e.message?:"Guest sign-in error.")
                        }
                    }
                }
            )
        }
    }
}
