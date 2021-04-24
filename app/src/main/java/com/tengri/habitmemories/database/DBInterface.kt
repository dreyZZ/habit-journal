package com.tengri.habitmemories.database

import androidx.room.Room
import com.tengri.habitmemories.App

object DBInterface {
    val db = Room.databaseBuilder(
        App.instance.applicationContext,
            AppDatabase::class.java, "habit-memories"
        ).allowMainThreadQueries().build()
}