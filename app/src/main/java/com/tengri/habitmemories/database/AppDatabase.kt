package com.tengri.habitmemories.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tengri.habitmemories.database.daos.HabitDao
import com.tengri.habitmemories.database.daos.MemoryDao
import com.tengri.habitmemories.database.entities.Habit
import com.tengri.habitmemories.database.entities.Memory

@Database(entities = [Habit::class, Memory::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun memoryDao(): MemoryDao
}