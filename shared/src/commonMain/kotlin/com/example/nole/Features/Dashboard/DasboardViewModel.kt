package com.example.nole.Features.Dashboard


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nole.data.MarketplaceRepository
import com.example.nole.model.CatalogItem
import com.example.nole.model.MarketplaceSection
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.MutableStateFlow


class DashboardViewModel : ViewModel() {


    private val filters = MutableStateFlow(DashboardUiState())


    val uiState: StateFlow<DashboardUiState> = combine(
        filters,
        MarketplaceRepository.items,
    ) { current, items ->
        current.copy(visibleProducts = items.filter { it.matches(current) })
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = DashboardUiState(),
    )


    fun onSectionSelected(section: MarketplaceSection) {
        filters.update { state ->
            if (state.section == section) return@update state
            state.copy(
                section = section,
                searchQuery = "",
                selectedCategories = emptySet(),
                selectedConditions = emptySet(),
                priceFromInput = "",
                priceToInput = "",
                appliedPriceFrom = null,
                appliedPriceTo = null,
                filterError = null,
            )
        }
    }


    fun onSearchChanged(query: String) {
        filters.update { it.copy(searchQuery = query) }
    }


    fun onCategoryChecked(category: String, checked: Boolean) {
        filters.update { state ->
            val updated = state.selectedCategories.toMutableSet().apply {
                if (checked) add(category) else remove(category)
            }
            state.copy(selectedCategories = updated)
        }
    }


    fun onConditionChecked(condition: String, checked: Boolean) {
        filters.update { state ->
            val updated = state.selectedConditions.toMutableSet().apply {
                if (checked) add(condition) else remove(condition)
            }
            state.copy(selectedConditions = updated)
        }
    }


    fun onPriceFromChanged(value: String) {
        filters.update {
            it.copy(priceFromInput = value.filter(Char::isDigit).take(7), filterError = null)
        }
    }


    fun onPriceToChanged(value: String) {
        filters.update {
            it.copy(priceToInput = value.filter(Char::isDigit).take(7), filterError = null)
        }
    }


    fun onApplyFilters() {
        filters.update { state ->
            val from = state.priceFromInput.toIntOrNull()
            val to = state.priceToInput.toIntOrNull()
            if (from != null && to != null && from > to) {
                state.copy(filterError = "El precio desde no puede ser mayor que hasta")
            } else {
                state.copy(
                    appliedPriceFrom = from,
                    appliedPriceTo = to,
                    filterError = null,
                )
            }
        }
    }
}


private fun CatalogItem.matches(state: DashboardUiState): Boolean {
    if (isSold || section != state.section) return false
    val query = state.searchQuery.trim()
    val matchesQuery = query.isEmpty() ||
            title.contains(query, ignoreCase = true) ||
            category.contains(query, ignoreCase = true) ||
            description.contains(query, ignoreCase = true)
    val matchesCategory = state.selectedCategories.isEmpty() || category in state.selectedCategories
    val matchesCondition = state.selectedConditions.isEmpty() || condition in state.selectedConditions
    val matchesFrom = state.appliedPriceFrom == null || priceEuros >= state.appliedPriceFrom
    val matchesTo = state.appliedPriceTo == null || priceEuros <= state.appliedPriceTo
    return matchesQuery && matchesCategory && matchesCondition && matchesFrom && matchesTo
}

