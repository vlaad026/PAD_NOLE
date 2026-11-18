package com.example.nole.di

import com.example.nole.core.network.SupabaseProvider
import com.example.nole.data.repository.MarketplaceRepositoryImpl
import com.example.nole.domain.repository.MarketplaceRepository

interface AppContainer {
    val marketplaceRepository: MarketplaceRepository
}

class DefaultAppContainer : AppContainer {
    private val supabaseProvider by lazy { SupabaseProvider() }
    override val marketplaceRepository: MarketplaceRepository by lazy {
        MarketplaceRepositoryImpl(supabaseProvider)
    }
}
