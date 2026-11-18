package com.example.nole.features.chats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nole.domain.repository.MarketplaceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ChatsViewModel(
    private val repository: MarketplaceRepository,
) : ViewModel() {

    val uiState: StateFlow<ChatsUiState> = combine(
        repository.items,
        repository.messages,
        repository.currentUser,
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

    fun onAction(action: ChatsAction) {
        when (action) {
            ChatsAction.BackClick,
            ChatsAction.HomeClick,
            is ChatsAction.ChatClick -> Unit
        }
    }
}
