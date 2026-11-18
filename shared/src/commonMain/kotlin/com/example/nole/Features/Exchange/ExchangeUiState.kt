package com.example.nole.features.exchange


import com.example.nole.domain.model.ChatMessage
import com.example.nole.domain.model.MarketplaceSection


data class ExchangeUiState(
    val productId: String = "",
    val title: String = "",
    val description: String = "",
    val priceLabel: String = "",
    val subCategory: String = "",
    val condition: String = "",
    val sellerUsername: String = "",
    val section: MarketplaceSection = MarketplaceSection.FUTBOL,
    val imageName: String = "",
    val images: List<String> = emptyList(),
    val imageIndex: Int = 0,
    val draftMessage: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val isSold: Boolean = false,
    val showPurchaseDialog: Boolean = false,
    val purchaseMessage: String? = null,
    val isMissing: Boolean = false,
)
