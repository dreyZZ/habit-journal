package com.tengri.habitmemories

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        instance = this
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    }

    companion object {
        lateinit var instance: App
            private set

        lateinit var sharedPreferences: SharedPreferences
            private set
    }
}