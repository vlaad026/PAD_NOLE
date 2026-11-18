package com.example.nole.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


// DTO QUE REPRESENTA UN USUARIO EN LA TABLA users
@Serializable
data class UserDto(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    @SerialName("password_hash") val passwordHash: String = "",
)

// DTO QUE REPRESENTA UN PRODUCTO EN LA TABLA catalog_items
@Serializable
data class CatalogItemDto(
    val id: String = "",
    @SerialName("seller_id") val sellerId: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    @SerialName("sub_category") val subCategory: String = "",
    val condition: String = "",
    @SerialName("image_name") val imageName: String = "",
    val images: List<String> = emptyList(),
    @SerialName("is_sold") val isSold: Boolean = false,
)

// DTO QUE REPRESENTA UN MENSAJE EN LA TABLA chat_message
@Serializable
data class ChatMessageDto(
    val id: String = "",
    @SerialName("product_id") val productId: String = "",
    @SerialName("sender_id") val senderId: String = "",
    @SerialName("receiver_id") val receiverId: String = "",
    val text: String = "",
    @SerialName("created_at") val createdAt: String = "",
)

// DTO PARA ACTUALIZAR EL ESTADO DE UN PRODUCTO VENDIDO
@Serializable
internal data class SoldPatch(
    @SerialName("is_sold") val isSold: Boolean,
)

// DTO PARA OBTENER EL ID Y NOMBRE DE UN USUARIO
@Serializable
internal data class UserNameDto(
    val id: String = "",
    val username: String = "",
)
