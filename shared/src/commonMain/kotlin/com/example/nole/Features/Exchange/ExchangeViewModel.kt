package com.example.nole.features.exchange


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nole.domain.repository.MarketplaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


private data class ExchangeLocalState(
    val imageIndex: Int = 0,
    val draftMessage: String = "",
    val showPurchaseDialog: Boolean = false,
    val purchaseMessage: String? = null,
)


class ExchangeViewModel(
    private val productId: String,
    private val repository: MarketplaceRepository,
) : ViewModel() {


    private val local = MutableStateFlow(ExchangeLocalState())


    val uiState: StateFlow<ExchangeUiState> = combine(
        repository.items,
        repository.messages,
        local,
    ) { items, messages, ui ->
        val item = items.find { it.id == productId }
        if (item == null) {
            ExchangeUiState(productId = productId, isMissing = true)
        } else {
            val gallery = item.gallery
            val index = if (gallery.isEmpty()) 0 else ui.imageIndex.mod(gallery.size)
            ExchangeUiState(
                productId = item.id,
                title = item.title,
                description = item.description,
                priceLabel = item.priceLabel,
                subCategory = item.subCategory,
                condition = item.condition,
                sellerUsername = item.sellerName,
                section = item.section,
                imageName = item.imageName,
                images = gallery,
                imageIndex = index,
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
        shiftImage(-1)
    }


    fun onNextImage() {
        shiftImage(1)
    }


    fun onSelectImage(index: Int) {
        local.update { it.copy(imageIndex = index) }
    }


    private fun shiftImage(delta: Int) {
        val count = uiState.value.images.size
        if (count <= 1) return
        local.update { state ->
            val next = (state.imageIndex + delta).mod(count)
            state.copy(imageIndex = next)
        }
    }


    fun onBuyClick() {
        if (uiState.value.isSold) return
        local.update { it.copy(showPurchaseDialog = true) }
    }


    fun onDismissPurchase() {
        local.update { it.copy(showPurchaseDialog = false) }
    }


    fun onConfirmPurchase() {
        viewModelScope.launch {
            val sold = repository.markSold(productId)
            local.update {
                it.copy(
                    showPurchaseDialog = false,
                    purchaseMessage = if (sold) "Compra realizada con éxito" else "No se ha podido completar la compra",
                )
            }
        }
    }


    fun onPurchaseMessageConsumed() {
        local.update { it.copy(purchaseMessage = null) }
    }


    fun onDraftChanged(message: String) {
        local.update { it.copy(draftMessage = message) }
    }


    fun onAction(action: ExchangeAction) {
        when (action) {
            ExchangeAction.PreviousImage -> onPreviousImage()
            ExchangeAction.NextImage -> onNextImage()
            is ExchangeAction.SelectImage -> onSelectImage(action.index)
            ExchangeAction.BuyClick -> onBuyClick()
            ExchangeAction.DismissPurchase -> onDismissPurchase()
            ExchangeAction.ConfirmPurchase -> onConfirmPurchase()
            ExchangeAction.PurchaseMessageConsumed -> onPurchaseMessageConsumed()
            is ExchangeAction.DraftChanged -> onDraftChanged(action.message)
            ExchangeAction.SendMessage -> onSendMessage()
        }
    }

    fun onSendMessage() {
        val text = local.value.draftMessage
        if (text.isBlank()) return
        val loggedIn = repository.currentUser.value != null
        viewModelScope.launch {
            val sent = repository.sendMessage(productId, text)
            local.update {
                if (sent) {
                    it.copy(draftMessage = "")
                } else {
                    it.copy(
                        purchaseMessage = if (loggedIn) {
                            "No se ha podido enviar el mensaje"
                        } else {
                            "Inicia sesión para enviar mensajes"
                        },
                    )
                }
            }
        }
    }
}
