package com.example.fooddeliveryapp.core.di

import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.core.data.auth.GoogleUiClient
import com.example.fooddeliveryapp.core.data.domain.AdminRepository
import com.example.fooddeliveryapp.core.data.domain.CartRepository
import com.example.fooddeliveryapp.core.data.domain.CountryRepository
import com.example.fooddeliveryapp.core.data.domain.CountryRepositoryImpl
import com.example.fooddeliveryapp.core.data.domain.CustomerRepository
import com.example.fooddeliveryapp.core.data.domain.PaymentRepository
import com.example.fooddeliveryapp.core.data.domain.ProductRepository
import com.example.fooddeliveryapp.core.data.remote.PaymentApi
import com.example.fooddeliveryapp.core.data.remote.RestCountriesApi
import com.example.fooddeliveryapp.core.data.repoImpl.CustomerRepoImpl
import com.example.fooddeliveryapp.core.data.repolmpl.AdminRepoImpl
import com.example.fooddeliveryapp.core.data.repolmpl.CartRepoImpl
import com.example.fooddeliveryapp.core.data.repolmpl.PaymentRepositoryImpl
import com.example.fooddeliveryapp.core.data.repolmpl.ProductRepoImpl
import com.example.fooddeliveryapp.feature.admin_panel.AdminPanelViewModel
import com.example.fooddeliveryapp.feature.admin_panel.manage_product.ManageProductViewModel
import com.example.fooddeliveryapp.feature.auth.AuthViewModel
import com.example.fooddeliveryapp.feature.home.HomeViewModel
import com.example.fooddeliveryapp.feature.payment.momo.MoMoCheckoutViewModel
import com.example.fooddeliveryapp.feature.payment.momo.MoMoPaymentCoordinator
import com.example.fooddeliveryapp.feature.payment.paypal.CheckoutViewModel
import com.example.fooddeliveryapp.feature.product_details.ProductDetailsViewModel
import com.example.fooddeliveryapp.feature.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.stephennnamani.burgerrestaurantapp.feature.home.cart.CartViewModel
import com.stephennnamani.burgerrestaurantapp.feature.home.categories.FoodMenuViewModel
import com.stephennnamani.burgerrestaurantapp.feature.home.product_overview.ProductOverviewViewModel
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Set to true to connect to local Firebase Functions emulator (e.g., http://10.0.2.2:5001)
// Set to false to connect to the deployed production Firebase Functions (https://us-central1-...)
private const val USE_EMULATOR = true

private fun getBaseUrl(): String {
    return if (USE_EMULATOR) {
        "http://10.0.2.2:5001/food-delivery-app-f8eaf/us-central1/api/"
    } else {
        "https://us-central1-food-delivery-app-f8eaf.cloudfunctions.net/api/"
    }
}

val appModule = module {

    single {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(getBaseUrl())
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single <RestCountriesApi>{
        Retrofit.Builder()
            .baseUrl("https://files-03.restcountries.com/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RestCountriesApi::class.java)
    }

    single <PaymentApi> { get<Retrofit>().create(PaymentApi::class.java) }

    single<CountryRepository> { CountryRepositoryImpl(get()) }
    single<FirebaseAuth> { FirebaseAuth.getInstance() }
    single<CustomerRepository> { CustomerRepoImpl() }
    single<AdminRepository> { AdminRepoImpl() }
    single<ProductRepository> { ProductRepoImpl() }
    single<CartRepository> { CartRepoImpl(get(), get()) }
    single<PaymentRepository> { PaymentRepositoryImpl(get()) }

    single { MoMoPaymentCoordinator(androidContext()) }


    viewModel { AuthViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { ProfileViewModel(get(),get()) }
    viewModel { AdminPanelViewModel(get()) }
    viewModel { ManageProductViewModel(get(), get()) }
    viewModel { ProductOverviewViewModel(get(), get()) }
    viewModel { ProductDetailsViewModel(get(),get(),get()) }
    viewModel { CartViewModel(get()) }
    viewModel { FoodMenuViewModel(get(),get(),get()) }
    viewModel { CheckoutViewModel(get()) }
    viewModel { MoMoCheckoutViewModel(get(), get(), get()) }


    single {
        GoogleUiClient(
            context = androidContext(),
            auth = get(),
            serverClient = androidContext().getString(R.string.default_web_client_id)
        )
    }
}