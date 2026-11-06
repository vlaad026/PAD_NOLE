package com.example.nole.Features.Exchange


import com.example.nole.model.ChatMessage
import com.example.nole.model.MarketplaceSection


data class ExchangeUiState(
    val productId: String = "",
    val title: String = "",
    val description: String = "",
    val priceLabel: String = "",
    val location: String = "",
    val size: String = "",
    val sellerUsername: String = "",
    val section: MarketplaceSection = MarketplaceSection.FUTBOL,
    val imageIndex: Int = 0,
    val draftMessage: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val isSold: Boolean = false,
    val showPurchaseDialog: Boolean = false,
    val purchaseMessage: String? = null,
    val isMissing: Boolean = false,
)
