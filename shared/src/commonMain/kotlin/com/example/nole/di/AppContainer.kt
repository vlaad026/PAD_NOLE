package com.example.nole.di

import com.example.nole.core.network.SupabaseProvider
import com.example.nole.data.repository.MarketplaceRepositoryImpl
import com.example.nole.domain.repository.MarketplaceRepository
import com.example.nole.domain.usecase.FilterCatalogUseCase
import com.example.nole.domain.usecase.LoginUseCase
import com.example.nole.domain.usecase.RegisterUseCase

// DEPENDENCIAS COMPARTIDAS EN TODA LA APP
interface AppContainer {
    val marketplaceRepository: MarketplaceRepository
    val loginUseCase: LoginUseCase
    val registerUseCase: RegisterUseCase
    val filterCatalogUseCase: FilterCatalogUseCase
}

class DefaultAppContainer : AppContainer {
    private val supabaseProvider by lazy { SupabaseProvider() }
    override val marketplaceRepository: MarketplaceRepository by lazy {
        MarketplaceRepositoryImpl(supabaseProvider)
    }
    override val loginUseCase: LoginUseCase by lazy { LoginUseCase(marketplaceRepository) }
    override val registerUseCase: RegisterUseCase by lazy { RegisterUseCase(marketplaceRepository) }
    override val filterCatalogUseCase: FilterCatalogUseCase by lazy { FilterCatalogUseCase() }
}
