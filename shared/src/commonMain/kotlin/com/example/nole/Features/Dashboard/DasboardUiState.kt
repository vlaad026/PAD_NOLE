package com.example.nole.Features.Dashboard


import com.example.nole.model.CatalogItem
import com.example.nole.model.MarketplaceSection


val ProductConditions = listOf("Nuevo", "Usado")


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
)


