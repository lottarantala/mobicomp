package com.example.core.database.dao

import androidx.room.*
import com.example.core.database.entity.ReminderEntity

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(reminder: ReminderEntity): Long

    @Query("SELECT * FROM reminders WHERE reminderId LIKE :reminderId")
    fun findOne(reminderId: Long): ReminderEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ReminderEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(entity: ReminderEntity)

    @Delete
    suspend fun delete(reminder: ReminderEntity)

    @Query("SELECT * FROM reminders")
    suspend fun findAll(): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE reminderSeen LIKE :seen")
    suspend fun loadSeenReminders(seen: Boolean): List<ReminderEntity>
}