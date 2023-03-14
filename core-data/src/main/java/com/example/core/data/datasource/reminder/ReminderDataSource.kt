package com.example.core.data.datasource.reminder

import com.example.core.domain.entity.Reminder

interface ReminderDataSource {
    suspend fun addReminder(reminder: Reminder): Long
    suspend fun editReminder(reminder: Reminder)
    suspend fun deleteReminder(reminder: Reminder)
    fun loadReminder(reminderId: Long): Reminder
    suspend fun loadReminders(): List<Reminder>
    suspend fun loadSeenReminders(seen: Boolean): List<Reminder>
    suspend fun setReminderSeen(reminderId: Long, seen: Boolean)
}