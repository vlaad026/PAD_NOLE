package com.example.nole.features.dashboard


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nole.domain.model.MarketplaceSection
import com.example.nole.domain.repository.MarketplaceRepository
import com.example.nole.domain.usecase.FilterCatalogUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.MutableStateFlow


class DashboardViewModel(
    private val repository: MarketplaceRepository,
    private val filterCatalogUseCase: FilterCatalogUseCase,
) : ViewModel() {


    private val filters = MutableStateFlow(DashboardUiState())


    val uiState: StateFlow<DashboardUiState> = combine(
        filters,
        repository.items,
        repository.currentUser,
    ) { current, items, user ->
        current.copy(
            visibleProducts = filterCatalogUseCase(
                items = items,
                section = current.section,
                searchQuery = current.searchQuery,
                selectedCategories = current.selectedCategories,
                selectedConditions = current.selectedConditions,
                priceFrom = current.appliedPriceFrom?.toDouble(),
                priceTo = current.appliedPriceTo?.toDouble(),
            ),
            currentUsername = user?.username,
        )
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


    fun logout() {
        repository.logout()
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



