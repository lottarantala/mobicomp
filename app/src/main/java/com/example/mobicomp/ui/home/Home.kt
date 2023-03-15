package com.example.mobicomp.ui.home

import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.core.domain.entity.Reminder
import com.example.mobicomp.R
import com.example.mobicomp.ui.reminder.ReminderViewModel
import com.example.mobicomp.ui.reminder.ReminderViewState
import com.google.accompanist.insets.systemBarsPadding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.example.mobicomp.ui.theme.Primary_dark
import com.example.mobicomp.ui.theme.Secondary_light
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun Home(
    viewModel: ReminderViewModel = hiltViewModel(),
    navController: NavController
) {
    val latitudeValue = remember { mutableStateOf<Float?>(65.03053F) }
    val longitudeValue = remember { mutableStateOf<Float?>(25.475655F) }
    val latlng = navController.currentBackStackEntry?.savedStateHandle?.get<LatLng>("location_data")

    if (latlng != null) {
        latitudeValue.value = latlng.latitude.toFloat()
        longitudeValue.value = latlng.longitude.toFloat()
        navController.currentBackStackEntry?.savedStateHandle?.set("location_data", null)
    }

    val appBarColor = MaterialTheme.colors.surface.copy(alpha = 0.87f)
    Scaffold(
        drawerContent = { DrawerContent(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(route = "reminder") },
                contentColor = Color.Black,
                backgroundColor = Primary_dark,
                modifier = Modifier.padding(all = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    ) {
        Column (
            modifier = Modifier
                .systemBarsPadding()
                .fillMaxWidth()
        ) {

            HomeAppBar(
                backgroundColor = appBarColor,
                navController = navController
            )

            ReminderTab(
                reminderViewModel = viewModel,
                navController = navController,
                latitude = latitudeValue.value,
                longitude = longitudeValue.value
            )
        }
    }
}

@Composable
private fun ReminderTab(
    reminderViewModel: ReminderViewModel = hiltViewModel(),
    navController: NavController,
    latitude: Float?,
    longitude: Float?
) {
    val viewState by reminderViewModel.reminderState.collectAsState()
    val tabs = listOf("Occurred", "Upcoming", "All")
    val selectedTab = remember { mutableStateOf("Occurred") }
    val location = if (latitude != null && longitude != null) {
        Location("").apply {
            this.latitude = latitude.toDouble()
            this.longitude = longitude.toDouble()
        }
    } else {
        null
    }

    LaunchedEffect(key1 = Unit) {
        reminderViewModel.reloadReminders(selectedTab.value, location)
    }

    Column {
        val tabIndex = tabs.indexOfFirst { it == selectedTab.value }
        TabRow(
            selectedTabIndex = tabIndex,
            indicator = emptyTabIndicator,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { index, tabName ->
                Tab(
                    selected = index == tabIndex,
                    onClick = {
                        selectedTab.value = tabName
                        reminderViewModel.reloadReminders(tabName, location)
                    }
                ) {
                    ChoiceChipContent(
                        text = tabName,
                        selected = index == tabIndex,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                    )
                }
            }
        }
        when (viewState) {
            is ReminderViewState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(Alignment.CenterVertically)
                        .padding(vertical = 16.dp)
                )
            }
            is ReminderViewState.Success -> {
                val reminderList = (viewState as ReminderViewState.Success).data
                val occurredReminders = reminderList.filter { it.reminderTime.isBefore(LocalDateTime.now()) }
                val scheduledReminders = reminderList.filter { it.reminderTime.isAfter(LocalDateTime.now()) }

                when (selectedTab.value) {
                    "Occurred" -> {
                        ReminderList(
                            navController = navController,
                            reminderList = occurredReminders,
                            tabSelected = selectedTab.value,
                            reminderViewModel = reminderViewModel,
                            location = location
                        )
                    }
                    "Upcoming" -> {
                        ReminderList(
                            navController = navController,
                            reminderList = scheduledReminders,
                            tabSelected = selectedTab.value,
                            reminderViewModel = reminderViewModel,
                            location = location
                        )
                    }
                    "All" -> {
                        ReminderList(
                            navController = navController,
                            reminderList = reminderList,
                            tabSelected = selectedTab.value,
                            reminderViewModel = reminderViewModel,
                            location = location
                        )
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun ReminderList(
    reminderList: List<Reminder>,
    navController: NavController,
    reminderViewModel: ReminderViewModel,
    tabSelected: String,
    location: Location?
) {
    LazyColumn(
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.Center
    ) {
        items(reminderList) { reminder ->
                ReminderListItem(
                    reminder = reminder,
                    onClick = { reminderViewModel.setReminderAsSeen(reminder.reminderId) },
                    modifier = Modifier.fillParentMaxWidth(),
                    navController = navController,
                    tabSelected = tabSelected,
                    reminderViewModel = reminderViewModel,
                    location = location
                )
        }
    }
}

@Composable
private fun ReminderListItem(
    reminder: Reminder,
    onClick: () -> Unit,
    navController: NavController,
    reminderViewModel: ReminderViewModel,
    modifier: Modifier = Modifier,
    tabSelected: String,
    location: Location?
) {
    ConstraintLayout(modifier = modifier.clickable { onClick() }) {
        val (divider, message, reminderTime, editButton, deleteButton, date) = createRefs()
        Divider(
            Modifier.constrainAs(divider) {
                top.linkTo(parent.top)
                centerHorizontallyTo(parent)
                width = Dimension.fillToConstraints
            }
        )

        Text(
            text = reminder.message,
            maxLines = 1,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.constrainAs(message) {
                linkTo(
                    start = parent.start,
                    end = editButton.start,
                    startMargin = 24.dp,
                    endMargin = 16.dp,
                    bias = 0f
                )
                top.linkTo(parent.top, margin = 10.dp)
                width = Dimension.preferredWrapContent
            }
        )

        Text(
            text = run { reminder.reminderTime.formatToString() },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.constrainAs(date) {
                linkTo(
                    start = reminderTime.end,
                    end = editButton.start,
                    startMargin = 8.dp,
                    endMargin = 16.dp,
                    bias = 0f
                )
                centerVerticallyTo(reminderTime)
                top.linkTo(message.bottom, 6.dp)
                bottom.linkTo(parent.bottom, 10.dp)
            }
        )

        IconButton(
            onClick = { navController.navigate(route = "editReminder/${reminder.reminderId}/${reminder.message}/${reminder.reminderTime}") },
            modifier = Modifier
                .size(50.dp)
                .padding(6.dp)
                .constrainAs(editButton) {
                    top.linkTo(parent.top, 10.dp)
                    bottom.linkTo(parent.bottom, 10.dp)
                    end.linkTo(deleteButton.start, 1.dp)
                }
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = stringResource(R.string.edit_icon),
                tint = Secondary_light
            )
        }

        IconButton(
            onClick = { reminderViewModel.deleteReminder(reminder, tabName = tabSelected, location) },
            modifier = Modifier
                .size(50.dp)
                .padding(6.dp)
                .constrainAs(deleteButton) {
                    top.linkTo(parent.top, 10.dp)
                    bottom.linkTo(parent.bottom, 10.dp)
                    end.linkTo(parent.end)
                }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_delete_24),
                contentDescription = ""
            )
        }
    }
}

@Composable
private fun HomeAppBar(
    navController: NavController,
    backgroundColor: Color
) {
    TopAppBar(
        title = {
            Text(
                text = "Reminder app",
                color = Primary_dark,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .heightIn(max = 24.dp)
            )
        },
        backgroundColor = backgroundColor,
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = stringResource(R.string.search))

            }
            IconButton(onClick = { navController.navigate(route = "profile") }) {
                Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = stringResource(R.string.account))
            }
        }
    )
}

@Composable
private fun DrawerContent(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Button(
            onClick = { navController.navigate(route = "login") },
            enabled = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(corner = CornerSize(50.dp))
        ) {
            Text(text = "Log out")
        }
    }

}

@Composable
private fun ChoiceChipContent(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        contentColor = when {
            selected -> MaterialTheme.colors.primary
            else -> MaterialTheme.colors.onSurface
        },
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
            color = Primary_dark
        )
    }
}

private fun LocalDateTime.formatToString(): String {
    val pattern = "uuuu-MM-dd HH:mm"
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return this.format(formatter)
}

private val emptyTabIndicator: @Composable (List<TabPosition>) -> Unit = {}