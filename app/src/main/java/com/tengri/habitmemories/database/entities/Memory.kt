package com.tengri.habitmemories.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Memory(
    @PrimaryKey(autoGenerate = true) var id: Long,
    val habitId: Long,
    val content: String?,
)