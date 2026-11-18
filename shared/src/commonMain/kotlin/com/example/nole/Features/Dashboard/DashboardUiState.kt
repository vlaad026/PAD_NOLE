package com.example.nole.features.dashboard


import com.example.nole.model.CatalogItem
import com.example.nole.model.MarketplaceSection


val ProductConditions = listOf("Nuevo", "Como nuevo", "Buen estado", "Sin abrir")


data class DashboardUiState(
    val section: MarketplaceSection = MarketplaceSection.FUTBOL,
    val searchQuery: String = "",
    val selectedCategories: Set<String> = emptySet(),
    val selectedConditions: Set<String> = emptySet(),
    val priceFromInput: String = "",
    val priceToInput: String = "",
    val appliedPriceFrom: Int? = null,
    val appliedPriceTo: Int? = null,
    val visibleProducts: List<CatalogItem> = emptyList(),
    val filterError: String? = null,
    val currentUsername: String? = null,
)


