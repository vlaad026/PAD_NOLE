package com.example.nole.features.exchange

sealed interface ExchangeAction {
    data object OnBackClick : ExchangeAction
    data object OnNavigateToChats : ExchangeAction
    data object OnNavigateToLogin : ExchangeAction
    data object PreviousImage : ExchangeAction
    data object NextImage : ExchangeAction
    data class SelectImage(val index: Int) : ExchangeAction
    data object BuyClick : ExchangeAction
    data object DismissPurchase : ExchangeAction
    data object ConfirmPurchase : ExchangeAction
    data object PurchaseMessageConsumed : ExchangeAction
    data class DraftChanged(val message: String) : ExchangeAction
    data object SendMessage : ExchangeAction
}
