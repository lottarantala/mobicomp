package com.example.mobicomp.ui.maps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ktx.awaitMap
import com.example.mobicomp.utils.rememberMapViewWithLifecycle
import java.util.*
import kotlinx.coroutines.launch

@Composable
fun ReminderLocation(navController: NavController) {
    val mapView: MapView = rememberMapViewWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val locationState = remember { mutableStateOf<LatLng?>(null)}

    AndroidView({mapView}) {
        coroutineScope.launch {
            val map = mapView.awaitMap()
            map.uiSettings.isZoomControlsEnabled = true
            map.uiSettings.isScrollGesturesEnabled = true
            val location = if (locationState.value != null) {
                locationState.value
            } else {
                LatLng(65.030534, 25.475654)}
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(location!!.latitude, location!!.longitude),
                    14f
                )
            )
            setMapLongClick(map, navController)
        }
    }
}

private fun setMapLongClick(
    map: GoogleMap,
    navController: NavController
) {
    map.setOnMapLongClickListener { latlng ->
        val snippet = String.format(
            Locale.getDefault(),
            "Lat: %1$.2f, Lng: %2$.2f",
            latlng.latitude,
            latlng.longitude
        )

        map.addMarker(
            MarkerOptions().position(latlng).title("Reminder location").snippet(snippet)
        ).apply {
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("location_data", latlng)
        }
    }
}