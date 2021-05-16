package com.tengri.habitmemories.database

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tengri.habitmemories.App
import java.util.*

object DBInterface {
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE Habit ADD COLUMN position INTEGER")
            database.execSQL("UPDATE Habit SET position = id")
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE Habit ADD COLUMN color INTEGER")
        }
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE Memory ADD COLUMN insertDate INTEGER")
            database.execSQL("UPDATE Memory SET insertDate = ${Date().time}")
        }
    }

    private val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE Memory RENAME TO Experience")
        }
    }

    val db = Room.databaseBuilder(
        App.instance.applicationContext,
        AppDatabase::class.java, "habit-memories"
    ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
        .allowMainThreadQueries().build()
}