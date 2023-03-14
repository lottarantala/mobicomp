package com.example.mobicomp.utils

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(
    val context: Context,
    val params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        NotificationWorker(context).createNotification(
            inputData.getString("message").toString(),
            inputData.getString("reminderTime").toString()
        )

        return Result.success()
    }
}