package com.example.mobicomp

import android.content.Context

object Graph {
    lateinit var appContext: Context

    fun provide(context: Context) {
        appContext = context
    }
}