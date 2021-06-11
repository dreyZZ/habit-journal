package com.tengri.habitjournal.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tengri.habitjournal.database.daos.HabitDao
import com.tengri.habitjournal.database.daos.ExperienceDao
import com.tengri.habitjournal.database.entities.Habit
import com.tengri.habitjournal.database.entities.Experience

@Database(entities = [Habit::class, Experience::class], version = 6)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun experienceDao(): ExperienceDao
}