package com.example.nole.features.chats

sealed interface ChatsAction {
    data object BackClick : ChatsAction
    data object HomeClick : ChatsAction
    data class ChatClick(val productId: String) : ChatsAction
}
