package com.tengri.habitmemories.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class HabitWithMemories(
    @Embedded val habit: Habit,
    @Relation(
        parentColumn = "id",
        entityColumn = "habitId"
    )
    val memories: List<Memory>
)