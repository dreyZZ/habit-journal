package com.tengri.habitmemories.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Habit(
    @PrimaryKey(autoGenerate = true) var id: Long,
    var name: String?,
    var position: Long,
    var color: Int? = null
)