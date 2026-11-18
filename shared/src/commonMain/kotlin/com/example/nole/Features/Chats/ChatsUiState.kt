package com.example.nole.features.chats

data class ChatConversation(
    val productId: String,
    val sellerUsername: String,
    val lastMessage: String,
    val lastActivityAt: Long,
)

data class ChatsUiState(
    val conversations: List<ChatConversation> = emptyList(),
)
