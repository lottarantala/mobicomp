package com.example.mobicomp.ui.reminder

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.core.domain.entity.Reminder
import com.google.accompanist.insets.systemBarsPadding
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.random.Random
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.compose.ui.graphics.Color
import com.example.mobicomp.Graph
import com.google.android.gms.maps.model.LatLng

@Composable
fun ReminderScreen(
    navController: NavController,
    viewModel: ReminderViewModel = hiltViewModel(),
) {

    val message = remember {mutableStateOf("")}
    val latitude = remember {mutableStateOf<Float?>(null)}
    val longitude = remember {mutableStateOf<Float?>(null)}
    val timeEnabled = remember {mutableStateOf(true)}

    val locationData = navController.currentBackStackEntry?.savedStateHandle?.getLiveData<LatLng>("location_data")?.value
    if (locationData != null) {
        latitude.value = locationData.latitude.toFloat()
        longitude.value = locationData.longitude.toFloat()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            val date = remember { mutableStateOf(LocalDate.now()) }
            val time = remember { mutableStateOf(LocalTime.now()) }
            TopAppBar {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
                Text(text = "Reminder")
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.padding(16.dp)
            ) {
                OutlinedTextField(
                    value = message.value,
                    onValueChange = {message.value = it},
                    label = { Text(text = "Reminder message")},
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(corner = CornerSize(50.dp))
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.weight(0.4f)) {
                        DatePicker(context = LocalContext.current as FragmentActivity, date = date)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(0.3f)) {
                        TimePicker(context = LocalContext.current as FragmentActivity, time = time)
                    }
                    Checkbox(
                        checked = timeEnabled.value,
                        onCheckedChange = { checked -> timeEnabled.value = checked },
                        modifier = Modifier.padding(10.dp).weight(0.1f),
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color.Green
                        )
                    )
                    Text(text = "Use time")
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                        LocationPicker(
                            context = LocalContext.current as FragmentActivity,
                            navController = navController,
                            launcher = launcher,
                            latitude = latitude,
                            longitude = longitude
                        )
                }

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = {
                        val id = Random.nextLong()
                        viewModel.saveReminder(
                            navController = navController,
                            useTime = timeEnabled.value,
                            reminder = Reminder(
                                reminderId = id,
                                message = message.value,
                                location_x = latitude.value,
                                location_y = longitude.value,
                                reminderTime = date.value.atTime(time.value),
                                creationTime = LocalDateTime.now(),
                                reminderSeen = false,
                                creatorId = 1
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(corner = CornerSize(50.dp))
                ) {
                    Text("Save reminder")
                }
            }
        }
    }
}

@Composable
fun DatePicker(context: Context, date: MutableState<LocalDate>) {
    val year: Int
    val month: Int
    val day: Int

    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.time = Date()

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            date.value = LocalDate.of(year, month + 1, dayOfMonth)
        }, year, month, day
    )

    OutlinedTextField(
        modifier = Modifier.clickable { datePickerDialog.show() },
        value = date.value.toString(),
        onValueChange = {},
        label = { Text(text = "Date") },
        shape = RoundedCornerShape(corner = CornerSize(50.dp)),
        enabled = false
    )
}

@Composable
fun TimePicker( context: Context, time: MutableState<LocalTime> ) {
    val hour: Int = time.value.hour
    val minute: Int = time.value.minute

    val dialog = TimePickerDialog(
        context,
        { _: TimePicker, hour: Int, minute: Int ->
            time.value = LocalTime.of(hour, minute)
        }, hour, minute, true
    )

    OutlinedTextField(
        modifier = Modifier.clickable { dialog.show() },
        label = { Text(text = "Time") },
        value = time.value.format(DateTimeFormatter.ofPattern("HH:mm")).toString(),
        onValueChange = {},
        enabled = false,
        shape = RoundedCornerShape(corner = CornerSize(50.dp))
    )
}

@Composable
fun LocationPicker(
    context: Context,
    navController: NavController,
    launcher: ActivityResultLauncher<String>,
    latitude: MutableState<Float?>,
    longitude: MutableState<Float?>
) {
    OutlinedButton(
        shape = RoundedCornerShape(corner = CornerSize(50.dp)),
        modifier = if (latitude.value != null && longitude.value != null) {
            Modifier
                .fillMaxWidth(0.5f)
                .size(55.dp)
                .height(58.dp)
        } else {
            Modifier
                .fillMaxWidth()
                .size(55.dp)
                .height(58.dp)
        },
        onClick = {
            requestPermission(
                context = context,
                permission = Manifest.permission.ACCESS_FINE_LOCATION,
                requestPermission = {
                    launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            )
            requestPermission(
                context = context,
                permission = Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                requestPermission = {
                    launcher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                }
            ).apply {
                navController.navigate("map")
            }
        }
    ) {
        Text(
            text = if (latitude.value != null && longitude.value != null) {
                String.format(
                    Locale.getDefault(),
                    "Lat: %1$.2f\r\nLng: %2$.2f",
                    latitude.value,
                    longitude.value
                )
            } else {
                "Set location"
            }
        )
    }
    if (latitude.value != null && longitude.value != null) {
        OutlinedButton(
            onClick = {
                latitude.value = null
                longitude.value = null
                Toast.makeText(Graph.appContext, "Cleared location", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(corner = CornerSize(50.dp)),
            modifier = Modifier.fillMaxWidth(1f).size(55.dp).height(58.dp)
        ) {
            Text(text = "Clear location")
        }
    }
}

private fun requestPermission(
    context: Context,
    permission: String,
    requestPermission: () -> Unit
) {
    if (ContextCompat.checkSelfPermission(
            context,
            permission
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        requestPermission()
    }
}