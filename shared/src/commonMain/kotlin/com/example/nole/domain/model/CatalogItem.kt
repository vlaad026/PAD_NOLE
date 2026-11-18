package com.example.nole.domain.model

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
    COCHES(
        title = "Coches",
        searchHint = "Buscar en coches...",
        categories = listOf("Miniaturas", "Matrículas", "Otros"),
    ),
}

// PRODUCTO
data class CatalogItem(
    val id: String,
    val sellerId: String,
    val sellerName: String,
    val title: String,
    val description: String = "",
    val price: Double,
    val category: String,
    val subCategory: String,
    val condition: String,
    val imageName: String,
    val images: List<String> = emptyList(),
    val isSold: Boolean = false,
) {
    val sellerUsername: String
        get() = sellerName

    val section: MarketplaceSection
        get() = category.toMarketplaceSection()

    val gallery: List<String>
        get() {
            val named = images.filter { it.isNotBlank() }
            return named.ifEmpty { listOf(imageName).filter { it.isNotBlank() } }
        }

    val priceLabel: String
        get() = price.toEuroLabel()
}

// MENSAJE
data class ChatMessage(
    val id: Int,
    val text: String,
    val fromSeller: Boolean,
    val sentAtMillis: Long = 0L,
    val remoteId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
)

fun String.toMarketplaceSection(): MarketplaceSection {
    return when (trim().lowercase()) {
        "coches", "vehículos", "vehiculos" -> MarketplaceSection.COCHES
        else -> MarketplaceSection.FUTBOL
    }
}

// FORMATEAR PRECIO
fun Double.toEuroLabel(): String {
    val cents = kotlin.math.round(this * 100).toLong()
    val euros = cents / 100
    val remainder = (cents % 100).toInt()
    val grouped = euros.toString()
        .reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()
    return if (remainder == 0) {
        "$grouped €"
    } else {
        "$grouped,${remainder.toString().padStart(2, '0')} €"
    }
}
