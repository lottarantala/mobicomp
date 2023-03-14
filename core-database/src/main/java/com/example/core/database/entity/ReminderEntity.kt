package com.example.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "reminders",
    indices = [ Index("reminderId", unique = true) ]
)

data class ReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val reminderId: Long = 0,
    val message: String,
    val location_x: Float?,
    val location_y: Float?,
    val reminderTime: LocalDateTime,
    val creationTime: LocalDateTime,
    val creatorId: Long,
    val reminderSeen: Boolean,
)