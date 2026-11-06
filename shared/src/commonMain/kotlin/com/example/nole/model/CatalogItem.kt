package com.example.nole.model

enum class MarketplaceSection(
    val title: String,
    val searchHint: String,
    val categories: List<String>,
) {
    FUTBOL(
        title = "Fútbol",
        searchHint = "Buscar en fútbol...",
        categories = listOf("Cartas", "Figuras", "Camisetas", "Balones", "Otros"),
    ),
    VEHICULOS(
        title = "Vehículos",
        searchHint = "Buscar en vehículos...",
        categories = listOf("Miniaturas", "Matrículas", "Otros"),
    ),
}


data class CatalogItem(
    val id: String,
    val title: String,
    val description: String,
    val priceEuros: Int,
    val location: String,
    val size: String,
    val category: String,
    val condition: String,
    val sellerUsername: String,
    val section: MarketplaceSection,
    val isSold: Boolean = false,
) {
    val priceLabel: String
        get() = priceEuros.toEuroLabel()
}


data class ChatMessage(
    val id: Int,
    val text: String,
    val fromSeller: Boolean,
)


fun Int.toEuroLabel(): String {
    val grouped = toString()
        .reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()
    return "$grouped €"
}
