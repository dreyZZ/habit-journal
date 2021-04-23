package com.tengri.habitmemories.database

import androidx.room.Room
import com.tengri.habitmemories.App

object DBInterface {
    val db = Room.databaseBuilder(
        App.instance,
            AppDatabase::class.java, "habit-memories"
        ).build()
}