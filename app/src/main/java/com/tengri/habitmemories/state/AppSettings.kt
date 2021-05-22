package com.tengri.habitmemories.state

import android.content.SharedPreferences
import com.tengri.habitmemories.App

object AppSettings {

    private var prefs: SharedPreferences = App.sharedPreferences

    var habitListFilter: String = "clear"
        set(value) {
            field = value

            prefs.edit().putString("habitListFilter", value).apply()
        }

    init {
        habitListFilter = prefs.getString("habitListFilter", "clear")!!
    }

}
