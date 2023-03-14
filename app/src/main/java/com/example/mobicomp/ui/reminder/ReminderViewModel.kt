package com.example.mobicomp.ui.reminder

import android.Manifest
import android.content.BroadcastReceiver
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.core.domain.repository.ReminderRepository
import com.example.core.domain.entity.Reminder
import com.example.mobicomp.Graph
import com.example.mobicomp.utils.NotificationWorker
import com.example.mobicomp.utils.ReminderWorker
import com.google.android.gms.location.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val reminderRepository: ReminderRepository
): ViewModel() {
    private var _loaded: Boolean
    private val _listState = MutableStateFlow(ReminderListState())
    val listState: StateFlow<ReminderListState>
        get() = _listState
    private val _reminderViewState = MutableStateFlow<ReminderViewState>(ReminderViewState.Loading)
    val reminderState: StateFlow<ReminderViewState> = _reminderViewState
    private val geofencingClient = LocationServices.getGeofencingClient(Graph.appContext)

    val radius = 200.0f

    init {
        loadReminders()
    }

    fun saveReminder(navController: NavController, reminder: Reminder, useTime: Boolean) {
        viewModelScope.launch {
            val id = reminderRepository.addReminder(reminder)

            if (reminder.reminderTime.isBefore(LocalDateTime.now())) {
                reminderRepository.setReminderSeen(id, true)
            }
            if (reminder.location_x != null && reminder.location_y != null) {
                setLocationReminder(reminder)
            }
            if (useTime) {
                setTimeReminder(reminder)
            }
            delay(80)
            navController.popBackStack()
        }
    }

    fun editReminder(reminder: Reminder) {
        viewModelScope.launch {
            reminderRepository.editReminder(reminder)
        }
    }

    fun deleteReminder(reminder: Reminder, tabName: String, location: Location?) {
        viewModelScope.launch {
            reminderRepository.deleteReminder(reminder)
            reloadReminders(tabName, location)
        }
    }

    fun loadReminders() {
        viewModelScope.launch {
            try {
                _reminderViewState.value = ReminderViewState.Loading
                val reminders = reminderRepository.loadReminders()
                _reminderViewState.value = ReminderViewState.Success(reminders)
                _listState.value = ReminderListState(reminders = reminders, tabs = listOf("Occurred", "Scheduled", "All"))
                println("Loaded reminders: $reminders")
            } catch (e: Exception) {
                _reminderViewState.value = ReminderViewState.Error(e)
            }
        }
    }

    private suspend fun _reloadReminders(all: Boolean, location: Location?) {
        val list: List<Reminder> = if (all) {
            reminderRepository.loadReminders()
        } else {
            reminderRepository.loadSeenReminders(false)
        }
        val filteredList = if (location != null) {
            list.filter { reminder ->
                reminder.location_y != null && reminder.location_x != null &&
                        inRadius(reminder.location_x, reminder.location_y, location, radius) ||
                        (reminder.location_y == null && reminder.location_x == null)
            }
        } else if (all) {
            list
        } else {
            list.filter { reminder ->
                reminder.location_y == null && reminder.location_x == null
            }
        }
        val listButSorted: List<Reminder> = filteredList.sortedByDescending { it.reminderTime }
        _reminderViewState.value = ReminderViewState.Success(listButSorted)
        _listState.value = ReminderListState(
            reminders = listButSorted,
            tabs = listOf("Occurred", "Scheduled", "All")
        )
    }

    fun reloadReminders(tabName: String, location: Location?) {
        if (tabName == "Occurred" || tabName == "Scheduled" || tabName == "All") {
            viewModelScope.launch {
                _reloadReminders(true, location)
            }
        }
    }

    private fun inRadius(reminderLatitude: Float?, reminderLongitude: Float?, location: Location, radius: Float): Boolean {
        if (reminderLatitude == null || reminderLongitude == null) {
            return false
        }
        val reminderLocation = Location("")
        reminderLocation.latitude = reminderLatitude.toDouble()
        reminderLocation.longitude = reminderLongitude.toDouble()
        val distance = location.distanceTo(reminderLocation)
        return distance <= radius
    }

    private fun setLocationReminder(reminder: Reminder) {
        val geofence = Geofence.Builder()
            .setRequestId(reminder.message)
            .setCircularRegion(reminder.location_x!!.toDouble(), reminder.location_y!!.toDouble(), radius)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setLoiteringDelay(2)
            .setNotificationResponsiveness(0)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val intent = Intent(Graph.appContext, GeofenceBroadcastReceiver::class.java).apply{
            putExtra("reminder_message", reminder.message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            Graph.appContext,
            0,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (ActivityCompat.checkSelfPermission(
                Graph.appContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        if (ActivityCompat.checkSelfPermission(
                Graph.appContext,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        geofencingClient.addGeofences(geofencingRequest, pendingIntent).run {
            addOnSuccessListener {
                Toast.makeText(Graph.appContext, "Added reminder with geofence", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setTimeReminder(reminder: Reminder) {
        val timeZoneId = ZoneId.systemDefault()
        val timeNow = Calendar.getInstance()
        val reminderDate = Date.from(reminder.reminderTime.atZone(timeZoneId).toInstant())
        val reminderTime = Calendar.getInstance()
        reminderTime.time = reminderDate
        val reminderDelay = reminderTime.timeInMillis/1000L - timeNow.timeInMillis/1000L
        val reminderWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(reminderDelay, TimeUnit.SECONDS)
            .setInputData(workDataOf(
                "reminderTime" to reminder.reminderTime.toString(),
                "message" to reminder.message
            )).build()

        WorkManager.getInstance(Graph.appContext).enqueue(reminderWorkRequest)
        Toast.makeText(Graph.appContext, "New reminder set", Toast.LENGTH_SHORT).show()
    }

    fun setReminderAsSeen(reminderId: Long) {
        viewModelScope.launch {
            reminderRepository.setReminderSeen(reminderId, true)
        }
    }

    init {
        _loaded = false
    }
}

data class ReminderListState(
    val reminders: List<Reminder> = emptyList(),
    val tabs: List<String> = emptyList()
)

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }
        val reminderMessage = intent!!.getStringExtra("reminder_message")
        if (reminderMessage != null) {
            val notificationWorker = NotificationWorker(Graph.appContext)

            notificationWorker.createNotification(
                "at your location",
                reminderMessage
            )
            val geofencingClient = LocationServices.getGeofencingClient(Graph.appContext)
            geofencingClient.removeGeofences(listOf(reminderMessage)).run {
                addOnSuccessListener {
                    Log.d(TAG, "Removed geofence: $reminderMessage")
                }
                addOnFailureListener {
                    Log.e(TAG, "Failed to remove geofence: $reminderMessage")
                }
            }
        }

    }
}