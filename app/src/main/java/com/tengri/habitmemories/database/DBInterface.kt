package com.tengri.habitmemories.database

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tengri.habitmemories.App

object DBInterface {
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE Habit ADD COLUMN position INTEGER")
            database.execSQL("UPDATE Habit SET position = id")
        }
    }

    val db = Room.databaseBuilder(
        App.instance.applicationContext,
            AppDatabase::class.java, "habit-memories"
        ).addMigrations(MIGRATION_1_2).allowMainThreadQueries().build()
}