package com.tengri.habitmemories.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.tengri.habitmemories.database.entities.Memory

@Dao
interface MemoryDao {
//    @Query("SELECT * FROM memory")
//    fun getAll(): List<Memory>

    @Query("SELECT * FROM memory WHERE id IN (:memoryIds)")
    fun loadAllByIds(memoryIds: IntArray): List<Memory>

    @Insert
    fun insertAll(vararg memories: Memory): List<Long>

    @Delete
    fun delete(memory: Memory)
}