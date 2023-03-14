package com.example.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.core.database.dao.ReminderDao
import com.example.core.database.entity.ReminderEntity
import com.example.core.database.utils.LocalDateTimeConverter

@Database(
    entities = [ReminderEntity::class],
    version = 3
)

@TypeConverters(LocalDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
}