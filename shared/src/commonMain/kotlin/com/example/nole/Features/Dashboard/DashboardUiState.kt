package com.example.nole.features.dashboard


import com.example.nole.domain.model.CatalogItem
import com.example.nole.domain.model.MarketplaceSection


val ProductConditions = listOf("Nuevo", "Como nuevo", "Buen estado", "Sin abrir")


data class DashboardUiState(
    val section: MarketplaceSection? = null,
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
) {
    val searchHint: String
        get() = section?.searchHint ?: "Buscar productos..."

    val filterCategories: List<String>
        get() = section?.categories
            ?: (MarketplaceSection.FUTBOL.categories + MarketplaceSection.COCHES.categories).distinct()
}


