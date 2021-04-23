package com.tengri.habitmemories.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tengri.habitmemories.database.daos.HabitDao
import com.tengri.habitmemories.database.entities.Habit

@Database(entities = [Habit::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
}