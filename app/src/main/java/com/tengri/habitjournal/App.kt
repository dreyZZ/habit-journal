package com.tengri.habitjournal

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import java.io.File

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        instance = this
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        experienceImageDir = File(getExternalFilesDir("")!!.path + File.separator + "experience-images")
        experienceImageDir.mkdirs()
    }

    companion object {
        lateinit var instance: App
            private set

        lateinit var sharedPreferences: SharedPreferences
            private set

        lateinit var experienceImageDir: File
            private set
    }
}