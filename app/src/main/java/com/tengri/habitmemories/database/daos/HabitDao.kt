package com.tengri.habitmemories.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.tengri.habitmemories.database.entities.Habit

@Dao
interface HabitDao {
    @Query("SELECT * FROM habit")
    fun getAll(): List<Habit>

    @Query("SELECT * FROM habit WHERE uid IN (:habitIds)")
    fun loadAllByIds(habitIds: IntArray): List<Habit>

    @Query("SELECT * FROM habit WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Habit

    @Insert
    fun insertAll(vararg habits: Habit)

    @Delete
    fun delete(habit: Habit)
}