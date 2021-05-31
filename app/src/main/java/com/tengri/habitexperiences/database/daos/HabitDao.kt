package com.tengri.habitexperiences.database.daos

import androidx.room.*
import com.tengri.habitexperiences.database.entities.Habit

@Dao
interface HabitDao {
    @Query("SELECT * FROM habit")
    fun getAll(): List<Habit>

    @Query("SELECT * FROM habit WHERE id IN (:habitIds)")
    fun loadAllByIds(habitIds: IntArray): List<Habit>

    @Query("SELECT * FROM habit WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Habit

    @Query("SELECT * FROM habit WHERE id LIKE :id LIMIT 1")
    fun findById(id: Long): Habit

    @Update
    fun update(vararg habits: Habit)

    @Insert
    fun insertAll(vararg habits: Habit): List<Long>

    @Delete
    fun delete(habit: Habit)
}