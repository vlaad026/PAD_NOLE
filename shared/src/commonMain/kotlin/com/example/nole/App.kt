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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nole.core.ui.NoleTheme
import com.example.nole.di.AppContainer
import com.example.nole.di.DefaultAppContainer
import com.example.nole.features.chats.ChatsScreen
import com.example.nole.features.chats.ChatsViewModel
import com.example.nole.features.dashboard.DashboardScreen
import com.example.nole.features.dashboard.DashboardViewModel
import com.example.nole.features.exchange.ExchangeScreen
import com.example.nole.features.exchange.ExchangeViewModel
import com.example.nole.features.login.LoginScreen
import com.example.nole.features.login.LoginViewModel

enum class Pantalla {
    DASHBOARD,
    LOGIN,
    EXCHANGE,
    CHATS
}

@Composable
@Preview
fun App(appContainer: AppContainer = DefaultAppContainer()) {
    val repository = appContainer.marketplaceRepository
    var pantallaActual by remember { mutableStateOf(Pantalla.LOGIN) }
    var productoId by remember { mutableStateOf<String?>(null) }
    var huboSesion by remember { mutableStateOf(false) }
    var authEpoch by remember { mutableIntStateOf(0) }
    val currentUser by repository.currentUser.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        appContainer.marketplaceRepository.start()
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
                    viewModel = viewModel {
                        DashboardViewModel(
                            appContainer.marketplaceRepository,
                            appContainer.filterCatalogUseCase,
                        )
                    },
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
                    viewModel = viewModel(key = "login-$authEpoch") {
                        LoginViewModel(
                            appContainer.loginUseCase,
                            appContainer.registerUseCase,
                        )
                    },
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
                        viewModel = viewModel(key = id) {
                            ExchangeViewModel(id, appContainer.marketplaceRepository)
                        },
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
                    viewModel = viewModel { ChatsViewModel(appContainer.marketplaceRepository) },
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