package com.example.core.data

import com.example.core.data.datasource.reminder.ReminderDataSource
import com.example.core.data.datasource.reminder.ReminderDataSourceImpl
import com.example.core.data.repository.ReminderRepositoryImpl
import com.example.core.domain.repository.ReminderRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindReminderDataSource(
        reminderDataSource: ReminderDataSourceImpl
    ): ReminderDataSource

    @Singleton
    @Binds
    fun bindReminderRepository(
        reminderRepository: ReminderRepositoryImpl
    ): ReminderRepository

}