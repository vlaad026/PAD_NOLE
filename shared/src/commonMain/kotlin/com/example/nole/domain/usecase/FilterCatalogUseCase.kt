package com.example.nole.domain.usecase

import com.example.nole.domain.model.CatalogItem
import com.example.nole.domain.model.MarketplaceSection

// CASO DE USO PARA APLICAR LAS REGLAS DEL FILTRO
class FilterCatalogUseCase {
    operator fun invoke(
        items: List<CatalogItem>,
        section: MarketplaceSection,
        searchQuery: String,
        selectedCategories: Set<String>,
        selectedConditions: Set<String>,
        priceFrom: Double?,
        priceTo: Double?,
    ): List<CatalogItem> {
        val query = searchQuery.trim()
        return items.filter { item ->
            if (item.isSold || item.section != section) return@filter false
            val matchesQuery = query.isEmpty() ||
                item.title.contains(query, ignoreCase = true) ||
                item.subCategory.contains(query, ignoreCase = true) ||
                item.category.contains(query, ignoreCase = true) ||
                item.description.contains(query, ignoreCase = true)
            val matchesCategory = selectedCategories.isEmpty() || item.subCategory in selectedCategories
            val matchesCondition = selectedConditions.isEmpty() || item.condition in selectedConditions
            val matchesFrom = priceFrom == null || item.price >= priceFrom
            val matchesTo = priceTo == null || item.price <= priceTo
            matchesQuery && matchesCategory && matchesCondition && matchesFrom && matchesTo
        }
    }
}
