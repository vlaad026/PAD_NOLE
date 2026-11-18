package com.example.nole

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.nole.core.ui.NoleTheme
import com.example.nole.di.AppContainer
import com.example.nole.di.DefaultAppContainer
import com.example.nole.features.chats.ChatsScreenRoot
import com.example.nole.features.chats.ChatsViewModel
import com.example.nole.features.dashboard.DashboardScreenRoot
import com.example.nole.features.dashboard.DashboardViewModel
import com.example.nole.features.exchange.ExchangeScreenRoot
import com.example.nole.features.exchange.ExchangeViewModel
import com.example.nole.features.login.LoginScreenRoot
import com.example.nole.features.login.LoginViewModel


// CONFIGURACIÓN DEL TEMA, CARGA INICIAL, CONTROL DE SESIÓN Y LA NAVEGACIÓN POR LA APP
@Composable
@Preview
fun App(appContainer: AppContainer = DefaultAppContainer()) {
    val repository = appContainer.marketplaceRepository
    val navController = rememberNavController()
    var huboSesion by remember { mutableStateOf(false) }
    val currentUser by repository.currentUser.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        appContainer.marketplaceRepository.start()
    }

    LaunchedEffect(currentUser?.id) {
        if (currentUser != null) {
            huboSesion = true
        } else if (huboSesion) {
            navController.navigate(Route.Login) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NoleTheme {
        NavHost(
            navController = navController,
            startDestination = Route.Login,
        ) {
            // IR A DASHBOARD TRAS REGISTRARTE O INICIAR SESIÓN
            composable<Route.Login> {
                LoginScreenRoot(
                    viewModel = viewModel {
                        LoginViewModel(
                            appContainer.loginUseCase,
                            appContainer.registerUseCase,
                        )
                    },
                    onBackClick = {},
                    onLoginSuccess = {
                        navController.navigate(Route.Dashboard) {
                            popUpTo<Route.Login> { inclusive = true }
                        }
                    },
                    showBack = false,
                )
            }
            // PERMITE IR A EXCHANGE, CHATS O LOGIN
            composable<Route.Dashboard> {
                DashboardScreenRoot(
                    viewModel = viewModel {
                        DashboardViewModel(
                            appContainer.marketplaceRepository,
                            appContainer.filterCatalogUseCase,
                        )
                    },
                    onNavigateToLogin = {
                        navController.navigate(Route.Login)
                    },
                    onNavigateToChats = {
                        navController.navigate(Route.Chats)
                    },
                    onProductClick = { id ->
                        navController.navigate(Route.Exchange(id))
                    },
                )
            }
            // PERMITE VOLVER AL DASHBOARD, ESTANDO EN EXCHANGE, PERMITIENDO IR A CHATS Y LOGIN
            composable<Route.Exchange> { backStackEntry ->
                val route = backStackEntry.toRoute<Route.Exchange>()
                ExchangeScreenRoot(
                    viewModel = viewModel {
                        ExchangeViewModel(route.productId, appContainer.marketplaceRepository)
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onNavigateToChats = {
                        navController.navigate(Route.Chats)
                    },
                    onNavigateToLogin = {
                        navController.navigate(Route.Login)
                    },
                )
            }

            // DESDE CHATS PERMITE VOLVER A ATRÁS, AL DASHBOARD O AL EXCHANGE
            composable<Route.Chats> {
                ChatsScreenRoot(
                    viewModel = viewModel { ChatsViewModel(appContainer.marketplaceRepository) },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onHomeClick = {
                        navController.navigate(Route.Dashboard) {
                            popUpTo<Route.Dashboard> { inclusive = false }
                        }
                    },
                    onChatClick = { productId ->
                        navController.navigate(Route.Exchange(productId))
                    },
                )
            }
        }
    }
}
