package com.needknowers.community

import android.app.Application
import com.google.android.libraries.places.api.Places

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Places.initialize(applicationContext, "AIzaSyDrU5M3_q6fgyuceTVzj7zHR9D5UvnjMXw")

    }
}