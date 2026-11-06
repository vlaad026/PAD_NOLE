package com.example.nole.Features.Chats


data class ChatConversation(
    val productId: String,
    val sellerUsername: String,
)


data class ChatsUiState(
    val conversations: List<ChatConversation> = emptyList(),
)
