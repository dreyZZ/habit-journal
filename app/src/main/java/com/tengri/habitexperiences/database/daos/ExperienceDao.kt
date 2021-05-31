package com.tengri.habitexperiences.database.daos

import androidx.room.*
import com.tengri.habitexperiences.database.entities.Experience

@Dao
interface ExperienceDao {

    @Query("SELECT * FROM experience WHERE habitId=:habitId")
    fun getByHabitId(habitId: Long): List<Experience>

    @Insert
    fun insertAll(vararg experiences: Experience): List<Long>

    @Update
    fun update(vararg experiences: Experience)

    @Delete
    fun delete(experience: Experience)
}