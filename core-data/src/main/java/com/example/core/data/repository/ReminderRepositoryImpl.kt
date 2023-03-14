package com.example.core.data.repository

import com.example.core.data.datasource.reminder.ReminderDataSource
import com.example.core.domain.entity.Reminder
import com.example.core.domain.repository.ReminderRepository
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val reminderDataSource: ReminderDataSource
) : ReminderRepository {
    override suspend fun addReminder(reminder: Reminder): Long {
        return reminderDataSource.addReminder(reminder)
    }
    override suspend fun editReminder(reminder: Reminder) {
        reminderDataSource.editReminder(reminder)
    }
    override suspend fun deleteReminder(reminder: Reminder) {
        reminderDataSource.deleteReminder(reminder)
    }
    override fun loadReminder(reminderId: Long): Reminder {
        return reminderDataSource.loadReminder(reminderId)
    }
    override suspend fun loadReminders(): List<Reminder> {
        return reminderDataSource.loadReminders()
    }
    override suspend fun loadSeenReminders(seen: Boolean): List<Reminder> {
        return reminderDataSource.loadSeenReminders(seen)
    }
    override suspend fun setReminderSeen(reminderId: Long, seen: Boolean) {
        reminderDataSource.setReminderSeen(reminderId, seen)
    }
}