package com.example.nole

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.nole.ui.login.LoginScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        LoginScreen(
            onLoginSuccess = {
                // Aquí pondrás la navegación a la pantalla principal cuando el login sea correcto
            }
        )
    }
}