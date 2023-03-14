package com.example.core.domain.repository

import com.example.core.domain.entity.Reminder

interface ReminderRepository {
    suspend fun addReminder(reminder: Reminder): Long
    suspend fun editReminder(reminder: Reminder)
    suspend fun deleteReminder(reminder: Reminder)
    suspend fun loadReminders(): List<Reminder>
    fun loadReminder(reminderId: Long): Reminder
    suspend fun loadSeenReminders(seen: Boolean): List<Reminder>
    suspend fun setReminderSeen(reminderId: Long, seen: Boolean)
}