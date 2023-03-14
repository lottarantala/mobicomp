package com.example.mobicomp.ui.reminder

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.core.domain.entity.Reminder
import com.google.accompanist.insets.systemBarsPadding
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun ReminderEditScreen(
    navController: NavController,
    reminderId: Long,
    message: String,
    viewModel: ReminderViewModel = hiltViewModel()
) {
    val id = remember { mutableStateOf(reminderId) }
    val reminderMessage = remember { mutableStateOf(message) }
    val latitude = remember {mutableStateOf<Float?>(null)}
    val longitude = remember {mutableStateOf<Float?>(null)}

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
        val date = remember { mutableStateOf(LocalDate.now()) }
        val time = remember { mutableStateOf(LocalTime.now()) }
        Column (
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            TopAppBar {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
                Text(text = "Edit Reminder")
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.padding(16.dp)

            ) {
                OutlinedTextField(
                    value = reminderMessage.value,
                    onValueChange = { reminderMessage.value = it },
                    label = { Text(text = "Reminder message") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(corner = CornerSize(25.dp))
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.weight(0.7f)) {
                        DatePicker(context = LocalContext.current as FragmentActivity, date = date)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(modifier = Modifier.weight(0.3f)) {
                        TimePicker(context = LocalContext.current as FragmentActivity, time = time)
                    }
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
                        viewModel.editReminder(
                            Reminder(
                                reminderId = id.value,
                                message = reminderMessage.value,
                                location_x = latitude.value,
                                location_y = longitude.value,
                                reminderTime = date.value.atTime(time.value),
                                creationTime = LocalDateTime.now(),
                                reminderSeen = false,
                                creatorId = 1,
                            )
                        )
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth().size(55.dp),
                    shape = RoundedCornerShape(corner = CornerSize(25.dp))
                ) {
                    Text(text = "Save changes")
                }
            }
        }
    }
}