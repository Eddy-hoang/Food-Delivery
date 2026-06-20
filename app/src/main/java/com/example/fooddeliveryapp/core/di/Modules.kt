package com.example.fooddeliveryapp.core.di

import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.core.data.auth.GoogleUiClient
import com.example.fooddeliveryapp.core.data.domain.AdminRepository
import com.example.fooddeliveryapp.core.data.domain.CountryRepository
import com.example.fooddeliveryapp.core.data.domain.CountryRepositoryImpl
import com.example.fooddeliveryapp.core.data.domain.CustomerRepository
import com.example.fooddeliveryapp.core.data.domain.ProductRepository
import com.example.fooddeliveryapp.core.data.remote.RestCountriesApi
import com.example.fooddeliveryapp.core.data.repoImpl.CustomerRepoImpl
import com.example.fooddeliveryapp.core.data.repolmpl.AdminRepoImpl
import com.example.fooddeliveryapp.core.data.repolmpl.ProductRepoImpl
import com.example.fooddeliveryapp.feature.admin_panel.AdminPanelViewModel
import com.example.fooddeliveryapp.feature.admin_panel.manage_product.ManageProductViewModel
import com.example.fooddeliveryapp.feature.auth.AuthViewModel
import com.example.fooddeliveryapp.feature.home.HomeViewModel
import com.example.fooddeliveryapp.feature.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.stephennnamani.burgerrestaurantapp.feature.home.product_overview.ProductOverviewViewModel
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

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
            .baseUrl("https://files-03.restcountries.com/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }
    single <RestCountriesApi>{ get<Retrofit>().create(RestCountriesApi::class.java)  }
    single<CountryRepository> { CountryRepositoryImpl(get()) }
    single<FirebaseAuth> { FirebaseAuth.getInstance() }
    single<CustomerRepository> { CustomerRepoImpl() }
    single<AdminRepository> { AdminRepoImpl() }
    single<ProductRepository> { ProductRepoImpl() }



    viewModel { AuthViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { ProfileViewModel(get(),get()) }
    viewModel { AdminPanelViewModel(get()) }
    viewModel { ManageProductViewModel(get(), get()) }
    viewModel { ProductOverviewViewModel(get(), get()) }



    single {
        GoogleUiClient(
            context = androidContext(),
            auth = get(),
            serverClient = androidContext().getString(R.string.default_web_client_id)
        )
    }
}