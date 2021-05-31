package com.tengri.habitexperiences.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tengri.habitexperiences.database.daos.HabitDao
import com.tengri.habitexperiences.database.daos.ExperienceDao
import com.tengri.habitexperiences.database.entities.Habit
import com.tengri.habitexperiences.database.entities.Experience

@Database(entities = [Habit::class, Experience::class], version = 6)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun experienceDao(): ExperienceDao
}