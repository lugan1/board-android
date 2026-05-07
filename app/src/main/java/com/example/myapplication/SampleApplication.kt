package com.example.myapplication

import android.app.Application
import co.touchlab.kermit.LogcatWriter
import co.touchlab.kermit.Logger
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Logger.setLogWriters(LogcatWriter())
        Logger.setTag("SampleApplication")
    }
}
