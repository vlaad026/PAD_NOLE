package com.example.nole

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

// Imports directos respetando las mayúsculas de tus carpetas
import com.example.nole.Features.Login.LoginScreen
import com.example.nole.ui.dashboard.DashboardScreen

enum class Pantalla {
    DASHBOARD,
    LOGIN
}

@Composable
@Preview
fun App() {
    var pantallaActual by remember { mutableStateOf(Pantalla.DASHBOARD) }

    MaterialTheme {
        when (pantallaActual) {
            Pantalla.DASHBOARD -> {
                DashboardScreen(
                    onNavigateToLogin = {
                        pantallaActual = Pantalla.LOGIN
                    }
                )
            }
            Pantalla.LOGIN -> {
                LoginScreen(
                    onBackClick = {
                        pantallaActual = Pantalla.DASHBOARD
                    },
                    onLoginSuccess = {
                        pantallaActual = Pantalla.DASHBOARD
                    }
                )
            }
        }
    }
}