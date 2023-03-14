package com.example.mobicomp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MobiCompApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}