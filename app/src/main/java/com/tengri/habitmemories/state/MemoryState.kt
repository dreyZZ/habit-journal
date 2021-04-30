package com.tengri.habitmemories.state

import com.tengri.habitmemories.database.DBInterface
import com.tengri.habitmemories.database.daos.MemoryDao
import com.tengri.habitmemories.database.entities.Memory

object MemoryState {
    private var memoryDao: MemoryDao = DBInterface.db.memoryDao()
    lateinit var memories: MutableList<Memory>

    init {
    }

    fun addMemory(memory: Memory) {
        // TODO: 24/04/2021 block ediyor olabilir
        val ids = memoryDao.insertAll(memory)

        memory.id = ids[0]

        memories.add(memory)
    }

    fun deleteMemory(memory: Memory) {
        memoryDao.delete(memory)

        memories.remove(memory)
    }

    fun lastIndex(): Int {
        return memories.size - 1
    }

    fun getMemories(habitId: Long): MutableList<Memory> {
        memories = memoryDao.getByHabitId(habitId) as MutableList<Memory>
        return memories
    }
}