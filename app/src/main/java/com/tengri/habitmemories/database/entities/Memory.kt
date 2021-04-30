package com.tengri.habitmemories.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Habit::class,
        parentColumns = ["id"],
        childColumns = ["habitId"],
        onDelete = CASCADE
    )]
)
data class Memory(
    @PrimaryKey(autoGenerate = true) var id: Long,
    val habitId: Long,
    val content: String?,
)