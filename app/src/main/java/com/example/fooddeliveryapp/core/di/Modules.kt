package com.example.fooddeliveryapp.core.di

import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.core.data.auth.GoogleUiClient
import com.example.fooddeliveryapp.core.data.domain.CountryRepository
import com.example.fooddeliveryapp.core.data.domain.CountryRepositoryImpl
import com.example.fooddeliveryapp.core.data.domain.CustomerRepository
import com.example.fooddeliveryapp.core.data.remote.RestCountriesApi
import com.example.fooddeliveryapp.core.data.repoImpl.CustomerRepoImpl
import com.example.fooddeliveryapp.feature.auth.AuthViewModel
import com.example.fooddeliveryapp.feature.home.HomeViewModel
import com.example.fooddeliveryapp.feature.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
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
            .baseUrl("https://restcountries.com/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single <RestCountriesApi>{ get<Retrofit>().create(RestCountriesApi::class.java)  }
    single<CountryRepository> { CountryRepositoryImpl(get()) }

    single<FirebaseAuth> { FirebaseAuth.getInstance() }

    single<CustomerRepository> { CustomerRepoImpl() }

    viewModel { AuthViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { ProfileViewModel(get(),get()) }


    single {
        GoogleUiClient(
            context = androidContext(),
            auth = get(),
            serverClient = androidContext().getString(R.string.default_web_client_id)
        )
    }
}
