package com.ydanneg.erply

import android.app.Application
import com.ydanneg.erply.sync.Sync
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ErplyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Sync.initialize(context = this)
    }
}