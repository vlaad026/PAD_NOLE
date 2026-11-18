package com.example.nole.domain.repository

import com.example.nole.domain.model.CatalogItem
import com.example.nole.domain.model.ChatMessage
import com.example.nole.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface MarketplaceRepository {
    val items: StateFlow<List<CatalogItem>>
    val messages: StateFlow<Map<String, List<ChatMessage>>>
    val currentUser: StateFlow<User?>

    fun start()
    suspend fun login(emailOrUsername: String, password: String): String?
    suspend fun register(username: String, email: String, password: String): String?
    fun logout()
    suspend fun markSold(productId: String): Boolean
    suspend fun publish(item: CatalogItem): Boolean
    suspend fun sendMessage(productId: String, text: String): Boolean
}
