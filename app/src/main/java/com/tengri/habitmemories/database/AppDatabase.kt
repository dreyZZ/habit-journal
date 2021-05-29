package com.tengri.habitmemories.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tengri.habitmemories.database.daos.HabitDao
import com.tengri.habitmemories.database.daos.ExperienceDao
import com.tengri.habitmemories.database.entities.Habit
import com.tengri.habitmemories.database.entities.Experience

@Database(entities = [Habit::class, Experience::class], version = 6)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun experienceDao(): ExperienceDao
}