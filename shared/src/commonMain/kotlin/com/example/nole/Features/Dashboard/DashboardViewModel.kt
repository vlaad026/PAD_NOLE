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


    val state: StateFlow<DashboardUiState> = combine(
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


    fun onAction(action: DashboardAction) {
        when (action) {
            is DashboardAction.SectionSelected -> {
                filters.update { state ->
                    if (state.section == action.section) return@update state
                    state.copy(
                        section = action.section,
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
            is DashboardAction.SearchChanged -> {
                filters.update { it.copy(searchQuery = action.query) }
            }
            is DashboardAction.CategoryChecked -> {
                filters.update { state ->
                    val updated = state.selectedCategories.toMutableSet().apply {
                        if (action.checked) add(action.category) else remove(action.category)
                    }
                    state.copy(selectedCategories = updated)
                }
            }
            is DashboardAction.ConditionChecked -> {
                filters.update { state ->
                    val updated = state.selectedConditions.toMutableSet().apply {
                        if (action.checked) add(action.condition) else remove(action.condition)
                    }
                    state.copy(selectedConditions = updated)
                }
            }
            is DashboardAction.PriceFromChanged -> {
                filters.update {
                    it.copy(priceFromInput = action.value.filter(Char::isDigit).take(7), filterError = null)
                }
            }
            is DashboardAction.PriceToChanged -> {
                filters.update {
                    it.copy(priceToInput = action.value.filter(Char::isDigit).take(7), filterError = null)
                }
            }
            DashboardAction.ApplyFilters -> {
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
            DashboardAction.Logout -> repository.logout()
            DashboardAction.OnNavigateToLogin,
            DashboardAction.OnNavigateToChats,
            is DashboardAction.OnProductClick -> Unit
        }
    }
}



