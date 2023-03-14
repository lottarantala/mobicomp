package com.example.mobicomp.ui.profile

import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun Profile(
    sharedPreferences: SharedPreferences,
    navController: NavController
) {
    val username = sharedPreferences.getString("username", "")
    val password = sharedPreferences.getString("password", "")

    Surface() {
        IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
        }

        Text(
            text = "Profile",
            modifier = Modifier.fillMaxWidth().padding(28.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h6
        )

        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.height(30.dp))

            Icon(
                painter = rememberVectorPainter(Icons.Filled.Person),
                contentDescription = "login_image",
                modifier = Modifier
                    .fillMaxWidth()
                    .size(150.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Username:",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h6
            )
            Text(
                text = "$username",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "Password:",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h6
            )
            Text(
                text = "$password",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1
            )
        }
    }
}