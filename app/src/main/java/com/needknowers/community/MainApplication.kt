package com.needknowers.community

import android.app.Application
import com.google.android.libraries.places.api.Places
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit



class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Places.initialize(applicationContext, getString(R.string.MAP_API_KEY))

    }
}