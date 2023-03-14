package com.example.core.domain.entity

import java.time.LocalDateTime

data class Reminder(
    val reminderId: Long = 0,
    val message: String,
    val location_x: Float? = 0.0F,
    val location_y: Float? = 0.0F,
    val reminderTime: LocalDateTime,
    val creationTime: LocalDateTime = LocalDateTime.now(),
    val reminderSeen: Boolean = false,
    val creatorId: Long,
)