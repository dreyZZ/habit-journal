package com.tengri.habitexperiences.database.entities

import androidx.room.ColumnInfo
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
data class Experience(
    @PrimaryKey(autoGenerate = true) var id: Long,
    val habitId: Long,
    var content: String?,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var image: ByteArray?,

    var insertDate: Long?,
    var position: Long
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Experience

        if (id != other.id) return false
        if (habitId != other.habitId) return false
        if (content != other.content) return false
        if (!image.contentEquals(other.image)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + habitId.hashCode()
        result = 31 * result + (content?.hashCode() ?: 0)
        result = 31 * result + image.contentHashCode()
        return result
    }
}