package com.example.mobicomp.ui.reminder

import com.example.core.domain.entity.Reminder

sealed interface ReminderViewState {
    object Loading: ReminderViewState
    data class Error(val throwable: Throwable) : ReminderViewState
    data class Success(
        val data: List<Reminder>
    ): ReminderViewState
}