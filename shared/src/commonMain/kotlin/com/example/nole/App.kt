package com.example.nole

import DashboardScreen
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.nole.Features.Login.LoginScreen

enum class Pantalla {
    DASHBOARD,
    LOGIN
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        var pantallaActual by remember { mutableStateOf(Pantalla.DASHBOARD) }

        when (pantallaActual) {
            Pantalla.DASHBOARD -> {
                DashboardScreen(
                    onNavigateToLogin = {
                        pantallaActual = Pantalla.LOGIN
                    }
                )
            }
            Pantalla.LOGIN -> {
                LoginScreen()
            }
        }
    }
}