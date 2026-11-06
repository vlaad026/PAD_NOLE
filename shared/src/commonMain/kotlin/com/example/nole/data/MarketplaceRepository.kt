
package com.example.nole.data


import com.example.nole.model.CatalogItem
import com.example.nole.model.ChatMessage
import com.example.nole.model.MarketplaceSection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


object MarketplaceRepository {


    private val _items = MutableStateFlow(seedCatalog())
    val items: StateFlow<List<CatalogItem>> = _items.asStateFlow()


    private val _messages = MutableStateFlow(seedMessages(_items.value))
    val messages: StateFlow<Map<String, List<ChatMessage>>> = _messages.asStateFlow()


    fun markSold(productId: String): Boolean {
        val current = _items.value.find { it.id == productId } ?: return false
        if (current.isSold) return false
        _items.update { items ->
            items.map { item ->
                if (item.id == productId) item.copy(isSold = true) else item
            }
        }
        return true
    }


    fun addMessage(productId: String, text: String, fromSeller: Boolean) {
        val clean = text.trim()
        if (clean.isEmpty()) return
        _messages.update { current ->
            val existing = current[productId].orEmpty()
            val nextId = (existing.maxOfOrNull { it.id } ?: 0) + 1
            current + (productId to (existing + ChatMessage(nextId, clean, fromSeller)))
        }
    }


    private fun seedMessages(items: List<CatalogItem>): Map<String, List<ChatMessage>> {
        return items.associate { item ->
            item.id to listOf(
                ChatMessage(
                    id = 1,
                    text = "Hola, soy ${item.sellerUsername}. El artículo sigue disponible.",
                    fromSeller = true,
                )
            )
        }
    }


    private fun seedCatalog(): List<CatalogItem> {
        return listOf(
            CatalogItem(
                id = "camiseta-atletico-1996",
                title = "Camiseta vintage firmada Atlético de Madrid 1996",
                description = "Camiseta de la temporada 1995/96 con firma de la plantilla. Pieza de colección en muy buen estado.",
                priceEuros = 89,
                location = "Madrid",
                size = "Talla M",
                category = "Camisetas",
                condition = "Usado",
                sellerUsername = "Carlos_Collector",
                section = MarketplaceSection.FUTBOL,
            ),
            CatalogItem(
                id = "kit-united-1999",
                title = "Camiseta match-worn Manchester United 1999",
                description = "Equipación usada en partido, con certificado de procedencia y parche de la final.",
                priceEuros = 24,
                location = "Barcelona",
                size = "Talla L",
                category = "Camisetas",
                condition = "Usado",
                sellerUsername = "RetroKits_ES",
                section = MarketplaceSection.FUTBOL,
            ),
            CatalogItem(
                id = "balon-mundial-1986",
                title = "Balón histórico edición limitada Mundial 1986",
                description = "Balón oficial de colección, serie numerada, sin uso en juego.",
                priceEuros = 64,
                location = "Valencia",
                size = "Talla 5",
                category = "Balones",
                condition = "Nuevo",
                sellerUsername = "MatchBalls_BCN",
                section = MarketplaceSection.FUTBOL,
            ),
            CatalogItem(
                id = "carta-messi-rookie",
                title = "Carta Messi rookie, serie limitada",
                description = "Cromo de debut en edición limitada, encapsulado y numerado.",
                priceEuros = 18,
                location = "Sevilla",
                size = "Edición única",
                category = "Cartas",
                condition = "Nuevo",
                sellerUsername = "CardsVault_VLC",
                section = MarketplaceSection.FUTBOL,
            ),
            CatalogItem(
                id = "figura-firmada",
                title = "Figura de colección firmada, edición numerada",
                description = "Figura oficial firmada por el jugador, número 42 de 100.",
                priceEuros = 32,
                location = "Bilbao",
                size = "Escala 1:9",
                category = "Figuras",
                condition = "Nuevo",
                sellerUsername = "FigureArchive",
                section = MarketplaceSection.FUTBOL,
            ),
            CatalogItem(
                id = "botas-final-2009",
                title = "Botas match-worn Predator, final 2009",
                description = "Par usado en la final, con holograma de autenticidad.",
                priceEuros = 15,
                location = "Madrid",
                size = "Talla 42",
                category = "Otros",
                condition = "Usado",
                sellerUsername = "BootRoom_MAD",
                section = MarketplaceSection.FUTBOL,
            ),
            CatalogItem(
                id = "seat-600-118",
                title = "Coche a escala 1:18 SEAT 600 Vintage",
                description = "Die-cast con apertura de puertas, acabado de época y base de exposición.",
                priceEuros = 14,
                location = "Madrid",
                size = "Escala 1:18",
                category = "Miniaturas",
                condition = "Nuevo",
                sellerUsername = "DiecastGarage",
                section = MarketplaceSection.VEHICULOS,
            ),
            CatalogItem(
                id = "mustang-1967",
                title = "Miniatura Ford Mustang 1967",
                description = "Réplica a escala 1:18 del fastback, pintura Highland Green.",
                priceEuros = 18,
                location = "Barcelona",
                size = "Escala 1:18",
                category = "Miniaturas",
                condition = "Nuevo",
                sellerUsername = "MustangVault",
                section = MarketplaceSection.VEHICULOS,
            ),
            CatalogItem(
                id = "matricula-california-1966",
                title = "Matrícula Histórica California 1966",
                description = "Placa original de colección, con relieve y pintura de época.",
                priceEuros = 26,
                location = "Valencia",
                size = "Original",
                category = "Matrículas",
                condition = "Usado",
                sellerUsername = "PlateHunter",
                section = MarketplaceSection.VEHICULOS,
            ),
            CatalogItem(
                id = "manual-ferrari-250",
                title = "Manual Original Ferrari 250 GTO",
                description = "Manual de taller de época, completo y en muy buen estado.",
                priceEuros = 22,
                location = "Maranello",
                size = "Original",
                category = "Otros",
                condition = "Usado",
                sellerUsername = "RossoArchive",
                section = MarketplaceSection.VEHICULOS,
            ),
            CatalogItem(
                id = "porsche-911-1973",
                title = "Miniatura Porsche 911 Carrera RS 1973",
                description = "Die-cast 1:18 con alerón ducktail y caja original.",
                priceEuros = 21,
                location = "Sevilla",
                size = "Escala 1:18",
                category = "Miniaturas",
                condition = "Nuevo",
                sellerUsername = "DiecastGarage",
                section = MarketplaceSection.VEHICULOS,
            ),
            CatalogItem(
                id = "poster-ferrari-1965",
                title = "Póster original de paddock Ferrari 1965",
                description = "Impresión de época enmarcada, procedencia de circuito.",
                priceEuros = 48,
                location = "Madrid",
                size = "70x100",
                category = "Otros",
                condition = "Usado",
                sellerUsername = "RossoArchive",
                section = MarketplaceSection.VEHICULOS,
            ),
        )
    }
}
