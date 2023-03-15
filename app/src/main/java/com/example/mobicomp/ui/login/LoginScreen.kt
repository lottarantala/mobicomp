package com.example.mobicomp.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.example.mobicomp.ui.theme.Primary_dark
import com.example.mobicomp.ui.theme.Primary_light
import com.example.mobicomp.ui.theme.Secondary_dark

@Composable
fun LoginScreen(
    navController: NavController,
    prefs: SharedPreferences,
    context: Context
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        val username = remember { mutableStateOf("") }
        val password = remember { mutableStateOf("") }

        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            Icon(
                painter = rememberVectorPainter(Icons.Filled.Person),
                contentDescription = "login_image",
                modifier = Modifier
                    .fillMaxWidth()
                    .size(150.dp),
                tint = Primary_light
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = username.value,
                onValueChange = { text -> username.value = text },
                label = { Text(text = "Username") },
                shape = RoundedCornerShape(corner = CornerSize(50.dp))
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password.value,
                onValueChange = { passwordString -> password.value = passwordString },
                label = { Text(text = "Password") },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(corner = CornerSize(50.dp))
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { checkPassword(context, username.value, password.value, sharedPreferences = prefs, navController = navController) },
                enabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(corner = CornerSize(50.dp)),
                colors = ButtonDefaults.buttonColors(backgroundColor = Primary_dark)
            ) {
                Text(text = "Login")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { },
                enabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(corner = CornerSize(50.dp)),
                colors = ButtonDefaults.buttonColors(backgroundColor = Secondary_dark)
            ) {
                Text(text = "Sign Up")
            }
        }
    }
}

private fun checkPassword(
    context: Context,
    username: String,
    password: String,
    sharedPreferences: SharedPreferences,
    navController: NavController
) {
    val storedUsername = sharedPreferences.getString("username", "")
    val storedPassword = sharedPreferences.getString("password", "")

    if (username == storedUsername && password == storedPassword) {
        navController.navigate(route = "home")
    } else {
        Toast.makeText(context, "Incorrect username or password", Toast.LENGTH_SHORT).show()
    }
}


