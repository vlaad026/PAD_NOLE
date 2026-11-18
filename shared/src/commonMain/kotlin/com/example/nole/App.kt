package com.example.nole

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nole.features.chats.ChatsScreen
import com.example.nole.features.dashboard.DashboardScreen
import com.example.nole.features.exchange.ExchangeScreen
import com.example.nole.features.login.LoginScreen
import com.example.nole.data.MarketplaceRepository
import com.example.nole.core.ui.NoleTheme

enum class Pantalla {
    DASHBOARD,
    LOGIN,
    EXCHANGE,
    CHATS
}

@Composable
@Preview
fun App() {
    var pantallaActual by remember { mutableStateOf(Pantalla.LOGIN) }
    var productoId by remember { mutableStateOf<String?>(null) }
    var huboSesion by remember { mutableStateOf(false) }
    var authEpoch by remember { mutableIntStateOf(0) }
    val currentUser by MarketplaceRepository.currentUser.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        MarketplaceRepository.start()
    }

    LaunchedEffect(currentUser?.id) {
        if (currentUser != null) {
            huboSesion = true
        } else if (huboSesion) {
            productoId = null
            pantallaActual = Pantalla.LOGIN
            authEpoch += 1
        }
    }

    NoleTheme {
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
                    authKey = "login-$authEpoch",
                    onBackClick = {},
                    onLoginSuccess = {
                        productoId = null
                        pantallaActual = Pantalla.DASHBOARD
                    },
                    showBack = false,
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