package com.example.nole.features.chats

sealed interface ChatsAction {
    data object OnBackClick : ChatsAction
    data object OnHomeClick : ChatsAction
    data class OnChatClick(val productId: String) : ChatsAction
}
