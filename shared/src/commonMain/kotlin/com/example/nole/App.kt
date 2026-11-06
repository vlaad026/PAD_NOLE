package com.example.nole

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.nole.Features.Chats.ChatsScreen
import com.example.nole.Features.Dashboard.DashboardScreen
import com.example.nole.Features.Exchange.ExchangeScreen
import com.example.nole.Features.Login.LoginScreen

enum class Pantalla {
    DASHBOARD,
    LOGIN,
    EXCHANGE,
    CHATS
}

@Composable
@Preview
fun App() {
    var pantallaActual by remember { mutableStateOf(Pantalla.DASHBOARD) }
    var productoId by remember { mutableStateOf<String?>(null) }

    MaterialTheme {
        when (pantallaActual) {
            Pantalla.DASHBOARD -> {
                DashboardScreen(
                    onNavigateToLogin = {
                        pantallaActual = Pantalla.LOGIN
                    },
                    onNavigateToChats = {
                        pantallaActual = Pantalla.CHATS
                    },
                    onProductClick = { id ->
                        productoId = id
                        pantallaActual = Pantalla.EXCHANGE
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
            Pantalla.EXCHANGE -> {
                val id = productoId
                if (id != null) {
                    ExchangeScreen(
                        productId = id,
                        onBackClick = {
                            pantallaActual = Pantalla.DASHBOARD
                        },
                        onNavigateToChats = {
                            pantallaActual = Pantalla.CHATS
                        },
                        onNavigateToLogin = {
                            pantallaActual = Pantalla.LOGIN
                        }
                    )
                }
            }
            Pantalla.CHATS -> {
                ChatsScreen(
                    onBackClick = {
                        pantallaActual = Pantalla.DASHBOARD
                    },
                    onHomeClick = {
                        pantallaActual = Pantalla.DASHBOARD
                    },
                    onChatClick = { productId ->
                        productoId = productId
                        pantallaActual = Pantalla.EXCHANGE
                    }
                )
            }
        }
    }
}