package com.example.nole

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Login : Route

    @Serializable
    data object Dashboard : Route

    @Serializable
    data class Exchange(val productId: String) : Route

    @Serializable
    data object Chats : Route
}
