package com.example.fooddeliveryapp

import android.app.Application
import com.example.fooddeliveryapp.core.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class FoodApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@FoodApplication)
            modules(appModule)
        }
    }
}