package com.example.fooddeliveryapp

import android.app.Application
import android.util.Log
import com.cloudinary.android.MediaManager
import com.example.fooddeliveryapp.core.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class FoodApplication : Application() {
    override fun onCreate() {
        super.onCreate()


        val config = mapOf(
            "cloud_name" to "dmjrv1vzw",
            "api_key" to "283875926324232",
            "api_secret" to "p8dUf8ld6oNnxIxj1fLfTDbwSn0"
        )
        MediaManager.init(this, config)

        try {
            MediaManager.init(this, config)
            Log.d("Cloudinary", "MediaManager đã khởi tạo thành công")
        } catch (e: Exception) {
            Log.e("Cloudinary", "Lỗi khởi tạo MediaManager: ${e.message}")
        }

        // 2. CẤU HÌNH KOIN
        startKoin {
            androidContext(this@FoodApplication)
            modules(appModule)
        }
    }
}
