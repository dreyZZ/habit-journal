package com.tengri.habitmemories.database.daos

import androidx.room.*
import com.tengri.habitmemories.database.entities.Memory

@Dao
interface MemoryDao {

    @Query("SELECT * FROM memory WHERE habitId=:habitId")
    fun getByHabitId(habitId: Long): List<Memory>

    @Insert
    fun insertAll(vararg memories: Memory): List<Long>

    @Update
    fun update(memory: Memory)

    @Delete
    fun delete(memory: Memory)
}