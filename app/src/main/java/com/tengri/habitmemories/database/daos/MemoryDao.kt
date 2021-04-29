package com.tengri.habitmemories.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.tengri.habitmemories.database.entities.Memory

@Dao
interface MemoryDao {

    @Query("SELECT * FROM memory WHERE habitId=:habitId")
    fun getByHabitId(habitId: Long): List<Memory>

    @Insert
    fun insertAll(vararg memories: Memory): List<Long>

    @Delete
    fun delete(memory: Memory)
}