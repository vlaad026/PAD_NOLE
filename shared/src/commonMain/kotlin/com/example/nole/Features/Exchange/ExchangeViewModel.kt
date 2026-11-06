package com.example.nole.Features.Exchange


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nole.data.MarketplaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update


private const val ImageCount = 2


private data class ExchangeLocalState(
    val imageIndex: Int = 0,
    val draftMessage: String = "",
    val showPurchaseDialog: Boolean = false,
    val purchaseMessage: String? = null,
)


class ExchangeViewModel(
    private val productId: String,
) : ViewModel() {


    private val local = MutableStateFlow(ExchangeLocalState())


    val uiState: StateFlow<ExchangeUiState> = combine(
        MarketplaceRepository.items,
        MarketplaceRepository.messages,
        local,
    ) { items, messages, ui ->
        val item = items.find { it.id == productId }
        if (item == null) {
            ExchangeUiState(productId = productId, isMissing = true)
        } else {
            ExchangeUiState(
                productId = item.id,
                title = item.title,
                description = item.description,
                priceLabel = item.priceLabel,
                location = item.location,
                size = item.size,
                sellerUsername = item.sellerUsername,
                section = item.section,
                imageIndex = ui.imageIndex,
                draftMessage = ui.draftMessage,
                messages = messages[item.id].orEmpty(),
                isSold = item.isSold,
                showPurchaseDialog = ui.showPurchaseDialog && !item.isSold,
                purchaseMessage = ui.purchaseMessage,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ExchangeUiState(productId = productId),
    )


    fun onPreviousImage() {
        local.update { it.copy(imageIndex = (it.imageIndex + ImageCount - 1) % ImageCount) }
    }


    fun onNextImage() {
        local.update { it.copy(imageIndex = (it.imageIndex + 1) % ImageCount) }
    }


    fun onBuyClick() {
        if (uiState.value.isSold) return
        local.update { it.copy(showPurchaseDialog = true) }
    }


    fun onDismissPurchase() {
        local.update { it.copy(showPurchaseDialog = false) }
    }


    fun onConfirmPurchase() {
        val sold = MarketplaceRepository.markSold(productId)
        local.update {
            it.copy(
                showPurchaseDialog = false,
                purchaseMessage = if (sold) "Compra realizada con éxito" else null,
            )
        }
    }


    fun onPurchaseMessageConsumed() {
        local.update { it.copy(purchaseMessage = null) }
    }


    fun onDraftChanged(message: String) {
        local.update { it.copy(draftMessage = message) }
    }


    fun onSendMessage() {
        val text = local.value.draftMessage
        if (text.isBlank()) return
        MarketplaceRepository.addMessage(productId, text, fromSeller = false)
        local.update { it.copy(draftMessage = "") }
    }
}
