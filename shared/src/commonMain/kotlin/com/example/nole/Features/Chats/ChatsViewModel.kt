package com.example.nole.Features.Chats


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nole.data.MarketplaceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


class ChatsViewModel : ViewModel() {


    val uiState: StateFlow<ChatsUiState> = MarketplaceRepository.items
        .map { items ->
            ChatsUiState(
                conversations = items.map { item ->
                    ChatConversation(
                        productId = item.id,
                        sellerUsername = item.sellerUsername,
                    )
                }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ChatsUiState(),
        )
}
