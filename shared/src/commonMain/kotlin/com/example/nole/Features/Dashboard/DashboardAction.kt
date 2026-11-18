package com.example.nole.features.dashboard

import com.example.nole.domain.model.MarketplaceSection

sealed interface DashboardAction {
    data class SectionSelected(val section: MarketplaceSection) : DashboardAction
    data class SearchChanged(val query: String) : DashboardAction
    data class CategoryChecked(val category: String, val checked: Boolean) : DashboardAction
    data class ConditionChecked(val condition: String, val checked: Boolean) : DashboardAction
    data class PriceFromChanged(val value: String) : DashboardAction
    data class PriceToChanged(val value: String) : DashboardAction
    data object ApplyFilters : DashboardAction
    data object Logout : DashboardAction
    data object OnNavigateToLogin : DashboardAction
    data object OnNavigateToChats : DashboardAction
    data class OnProductClick(val productId: String) : DashboardAction
}
