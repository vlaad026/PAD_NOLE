package com.example.nole.features.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nole.data.repository.MarketplaceRepositoryImpl
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ChatsViewModel : ViewModel() {

    val uiState: StateFlow<ChatsUiState> = combine(
        MarketplaceRepositoryImpl.items,
        MarketplaceRepositoryImpl.messages,
        MarketplaceRepositoryImpl.currentUser,
    ) { items, messages, user ->
        if (user == null) {
            ChatsUiState()
        } else {
            val conversations = items.mapNotNull { item ->
                val thread = messages[item.id].orEmpty().filter { message ->
                    message.senderId == user.id || message.receiverId == user.id
                }
                if (thread.isEmpty()) {
                    null
                } else {
                    val last = thread.maxBy { it.sentAtMillis }
                    ChatConversation(
                        productId = item.id,
                        sellerUsername = item.sellerUsername,
                        lastMessage = last.text,
                        lastActivityAt = last.sentAtMillis,
                    )
                }
            }.sortedByDescending { it.lastActivityAt }
            ChatsUiState(conversations = conversations)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ChatsUiState(),
    )
}
