package com.example.nole.data.repository

import com.example.nole.core.network.SupabaseProvider
import com.example.nole.data.AutoReplyMatcher
import com.example.nole.ioDispatcher
import com.example.nole.core.util.sha256Hex
import com.example.nole.data.dto.CatalogItemDto
import com.example.nole.data.dto.ChatMessageDto
import com.example.nole.data.dto.SoldPatch
import com.example.nole.data.dto.UserDto
import com.example.nole.data.dto.UserNameDto
import com.example.nole.domain.model.CatalogItem
import com.example.nole.domain.model.ChatMessage
import com.example.nole.domain.model.User
import com.example.nole.domain.repository.MarketplaceRepository
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlin.uuid.Uuid
import io.github.jan.supabase.realtime.RealtimeChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.Instant

class MarketplaceRepositoryImpl(
    private val supabaseProvider: SupabaseProvider,
) : MarketplaceRepository {

    private val client = supabaseProvider.client
    private val autoReplyMatcher = AutoReplyMatcher()
    private val scope = CoroutineScope(ioDispatcher + SupervisorJob())
    private var started = false
    private var sessionGeneration = 0
    private var messagesJob: Job? = null
    private var messagesChannel: RealtimeChannel? = null
    private var channelSeq = 0

    private val _items = MutableStateFlow<List<CatalogItem>>(emptyList())
    override val items: StateFlow<List<CatalogItem>> = _items.asStateFlow()

    private val _messages = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    override val messages: StateFlow<Map<String, List<ChatMessage>>> = _messages.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // CIERRA LA SESIÓN ACTUAL, Y DETIENE LOS CHATS
    override fun logout() {
        sessionGeneration += 1
        stopMessageSession()
        _messages.value = emptyMap()
        _currentUser.value = null
    }

    // INICIA EL CATALOGO DE PRODUCTOS
    override fun start() {
        if (started) return
        started = true
        scope.launch {
            try {
                refreshCatalog()
            } catch (error: Exception) {
                println("NOLE supabase: ${error.message}")
            }
        }
    }

    // LÓGICA DE LOGIN
    override suspend fun login(emailOrUsername: String, password: String): String? = withContext(ioDispatcher) {
        val key = emailOrUsername.trim()
        if (key.isEmpty() || password.isEmpty()) return@withContext "Por favor, rellena todos los campos"
        try {
            val hash = sha256Hex(password)
            val rows = client.from("users").select(Columns.list("id", "username", "email", "password_hash")) {
                filter {
                    or {
                        eq("email", key)
                        eq("username", key)
                    }
                }
            }.decodeList<UserDto>()
            val match = rows.firstOrNull { it.passwordHash == hash }
                ?: return@withContext "Correo o contraseña incorrectos"
            beginSession(User(id = match.id, username = match.username, email = match.email))
            null
        } catch (error: Exception) {
            val detail = error.supabaseDetail()
            println("NOLE login: $detail")
            error.printStackTrace()
            detail
        }
    }

    // LÓGICA DE REGISTRO
    override suspend fun register(username: String, email: String, password: String): String? = withContext(ioDispatcher) {
        val cleanEmail = email.trim()
        if (cleanEmail.isEmpty() || password.isEmpty()) {
            return@withContext "Por favor, rellena todos los campos"
        }
        try {
            val chosenUsername = availableUsername(username, cleanEmail)
            val row = UserDto(
                id = newId(),
                username = chosenUsername,
                email = cleanEmail,
                passwordHash = sha256Hex(password),
            )
            client.from("users").insert(row)
            beginSession(User(id = row.id, username = row.username, email = row.email))
            null
        } catch (error: Exception) {
            val detail = error.supabaseDetail()
            println("NOLE register: $detail")
            error.printStackTrace()
            detail
        }
    }

    // MARCA COMO VENDIDO UN PRODUCTO Y ACTUALIZA LA LISTA DE PRODUCTOS
    override suspend fun markSold(productId: String): Boolean = withContext(ioDispatcher) {
        val current = _items.value.find { it.id == productId } ?: return@withContext false
        if (current.isSold) return@withContext false
        try {
            client.from("catalog_items").update(SoldPatch(isSold = true)) {
                filter { eq("id", productId) }
            }
            _items.update { list ->
                list.map { item ->
                    if (item.id == productId) item.copy(isSold = true) else item
                }
            }
            true
        } catch (error: Exception) {
            println("NOLE markSold: ${error.message}")
            false
        }
    }

    // INSERTAR NUEVO PRODUCTO EN LA BBDD Y ACTUALUZAR CATALOGO
    override suspend fun publish(item: CatalogItem): Boolean = withContext(ioDispatcher) {
        val sellerId = item.sellerId.ifBlank { _currentUser.value?.id.orEmpty() }
        if (sellerId.isBlank()) return@withContext false
        val row = item.toDto(sellerId)
        try {
            client.from("catalog_items").insert(row)
            refreshCatalog()
            true
        } catch (error: Exception) {
            println("NOLE publish: ${error.message}")
            false
        }
    }

    // GUARDAR MENSAJE Y ASOCIARLO AL PRODUCTO
    override suspend fun sendMessage(productId: String, text: String): Boolean = withContext(ioDispatcher) {
        val clean = text.trim()
        if (clean.isEmpty()) return@withContext false
        val sender = _currentUser.value ?: return@withContext false
        val item = _items.value.find { it.id == productId } ?: return@withContext false
        val receiverId = resolveReceiver(productId, sender.id, item.sellerId) ?: return@withContext false
        val row = ChatMessageDto(
            id = newId(),
            productId = productId,
            senderId = sender.id,
            receiverId = receiverId,
            text = clean,
            createdAt = Clock.System.now().toString(),
        )
        try {
            client.from("chat_messages").insert(row)
            append(row)
            scheduleAutoReplies(
                productId = productId,
                text = clean,
                buyerId = sender.id,
                sellerId = item.sellerId,
            )
            true
        } catch (error: Exception) {
            println("NOLE sendMessage: ${error.message}")
            false
        }
    }

    private suspend fun scheduleAutoReplies(
        productId: String,
        text: String,
        buyerId: String,
        sellerId: String,
    ) {
        if (sellerId.isBlank() || sellerId == buyerId) return
        val thread = _messages.value[productId].orEmpty()
        if (thread.any { it.senderId == sellerId }) return
        val replies = autoReplyMatcher.findReplies(text)
        if (replies.isEmpty()) return
        val generation = sessionGeneration
        scope.launch {
            for (reply in replies) {
                delay(1000L)
                if (generation != sessionGeneration) return@launch
                val row = ChatMessageDto(
                    id = newId(),
                    productId = productId,
                    senderId = sellerId,
                    receiverId = buyerId,
                    text = reply,
                    createdAt = Clock.System.now().toString(),
                )
                try {
                    client.from("chat_messages").insert(row)
                    append(row)
                } catch (error: Exception) {
                    println("NOLE autoReply: ${error.message}")
                    return@launch
                }
            }
        }
    }

    // ACTUALIZAR CATÁLOGO
    private suspend fun refreshCatalog() {
        val rows = client.from("catalog_items").select().decodeList<CatalogItemDto>()
        val names = client.from("users")
            .select(Columns.list("id", "username"))
            .decodeList<UserNameDto>()
            .associate { it.id to it.username }
        _items.value = rows.map { dto -> dto.toItem(names) }
    }

    // CARGAR USUARIO Y SUS MENSAJES
    // DESCARGA HISTORIAL ANTIGUO Y ABRE EL REALTIME
    private suspend fun beginSession(user: User) {
        sessionGeneration += 1
        val generation = sessionGeneration
        stopMessageSession()
        _messages.value = emptyMap()
        _currentUser.value = user
        try {
            refreshMessages(user.id, generation)
        } catch (error: Exception) {
            println("NOLE messages: ${error.message}")
            if (generation == sessionGeneration) _messages.value = emptyMap()
        }
        if (generation != sessionGeneration) return
        try {
            subscribeToMessages(user.id, generation)
        } catch (error: Exception) {
            println("NOLE realtime: ${error.message}")
        }
    }

    // CUANDO CIERRAS SESIÓN, CANCELA LA CORRUTINA DE ESCUCHA DE MENSAJES EN DIRECTO
    // CIERRA EL REALTIME EN LA BBDD
    private fun stopMessageSession() {
        messagesJob?.cancel()
        messagesJob = null
        val channel = messagesChannel
        messagesChannel = null
        if (channel != null) {
            scope.launch {
                try {
                    channel.unsubscribe()
                } catch (error: Exception) {
                    println("NOLE realtime: ${error.message}")
                }
            }
        }
    }

    // CARGAR MENSAJES QUE ENVÍAS O RECIBES (ORDENADAMENTE)
    // LOS AGRUPA POR PRODUCTO Y SE CONVIERTEN EN DTO PARA PINTARLOS
    private suspend fun refreshMessages(userId: String, generation: Int) {
        val rows = client.from("chat_messages").select {
            filter {
                or {
                    eq("sender_id", userId)
                    eq("receiver_id", userId)
                }
            }
            order(column = "created_at", order = Order.ASCENDING)
        }.decodeList<ChatMessageDto>()
        if (generation != sessionGeneration || _currentUser.value?.id != userId) return
        val sellers = _items.value.associate { it.id to it.sellerId }
        _messages.value = rows
            .filter { it.involves(userId) }
            .groupBy { it.productId }
            .mapValues { (productId, list) ->
                list.map { dto -> dto.toMessage(sellers[productId].orEmpty()) }
            }
    }

    // ABRE READTIME EN LA BBDD PARA RECIBIR MENSAJES EN DIRECTO
    private suspend fun subscribeToMessages(userId: String, generation: Int) {
        if (generation != sessionGeneration) return
        val channel = client.channel("chat-messages-$userId-${++channelSeq}")
        messagesChannel = channel
        val inserts = channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
            table = "chat_messages"
        }
        messagesJob = scope.launch {
            inserts.collect { action ->
                if (generation != sessionGeneration) return@collect
                val dto = action.decodeRecord<ChatMessageDto>()
                if (!dto.involves(userId)) return@collect
                append(dto)
            }
        }
        channel.subscribe()
    }

    // AÑADE UN MENSAJE NUEVO
    private fun append(dto: ChatMessageDto) {
        val userId = _currentUser.value?.id ?: return
        if (!dto.involves(userId)) return
        if (dto.productId.isBlank() || dto.text.isBlank()) return
        val sellerId = _items.value.find { it.id == dto.productId }?.sellerId.orEmpty()
        val message = dto.toMessage(sellerId)
        _messages.update { current ->
            val existing = current[dto.productId].orEmpty()
            if (message.remoteId.isNotEmpty() && existing.any { it.remoteId == message.remoteId }) {
                return@update current
            }
            current + (dto.productId to (existing + message))
        }
    }

    // ID DEL DESTINATARIO DEL MENSAJE
    private fun resolveReceiver(productId: String, senderId: String, sellerId: String): String? {
        if (sellerId.isBlank()) return null
        if (senderId != sellerId) return sellerId
        val thread = _messages.value[productId].orEmpty()
        return thread.firstNotNullOfOrNull { message ->
            when {
                message.senderId.isNotEmpty() && message.senderId != senderId -> message.senderId
                message.receiverId.isNotEmpty() && message.receiverId != senderId -> message.receiverId
                else -> null
            }
        }
    }

    // COMPRUEBA EL NOMBRE DE USUARIO AL REGISTRARSE
    // ASEGURA QUE CADA USUARIO TENGA NOMBRE ÚNICO
    private suspend fun availableUsername(requested: String, email: String): String {
        val base = requested.trim().ifBlank { email.substringBefore("@").trim() }.ifBlank { "user" }
        var candidate = base
        repeat(5) {
            val taken = client.from("users").select(Columns.list("id", "username")) {
                filter { eq("username", candidate) }
            }.decodeList<UserNameDto>().isNotEmpty()
            if (!taken) return candidate
            candidate = base + Uuid.random().toString().substring(0, 4)
        }
        return candidate
    }

    private fun newId(): String = Uuid.random().toString()

    // CONVIERTE CatalogItem A CatalogItemDto PARA BBDD
    private fun CatalogItem.toDto(sellerId: String) = CatalogItemDto(
        id = id.ifBlank { newId() },
        sellerId = sellerId,
        title = title,
        description = description,
        price = price,
        category = category,
        subCategory = subCategory,
        condition = condition,
        imageName = imageName,
        images = gallery,
        isSold = isSold,
    )

    // CONVIERTE CatalogItemDto DE BBDD A CatalogItemDto
    private fun CatalogItemDto.toItem(usernames: Map<String, String>) = CatalogItem(
        id = id,
        sellerId = sellerId,
        sellerName = usernames[sellerId] ?: "Vendedor",
        title = title,
        description = description,
        price = price,
        category = category,
        subCategory = subCategory,
        condition = condition,
        imageName = imageName,
        images = images.ifEmpty { listOf(imageName).filter { it.isNotBlank() } },
        isSold = isSold,
    )

    // CONVIERTE ChatMessageDto DE BBDD A ChatMessage
    private fun ChatMessageDto.toMessage(sellerId: String) = ChatMessage(
        id = remoteIdHash(id),
        text = text,
        fromSeller = senderId.isNotEmpty() && senderId == sellerId,
        sentAtMillis = parseCreatedAt(createdAt),
        remoteId = id,
        senderId = senderId,
        receiverId = receiverId,
    )
}

private fun parseCreatedAt(raw: String): Long {
    if (raw.isBlank()) return 0L
    val withT = raw.trim().replace(' ', 'T')
    val normalized = Regex("""(\.\d+)(.*)""").replace(withT) { match ->
        match.groupValues[1].take(4) + match.groupValues[2]
    }
    return try {
        Instant.parse(normalized).toEpochMilliseconds()
    } catch (_: IllegalArgumentException) {
        0L
    }
}

// EL USUARIO EMITE O RECIBE EL MENSAJE
private fun ChatMessageDto.involves(userId: String): Boolean {
    return senderId == userId || receiverId == userId
}

private fun Exception.supabaseDetail(): String {
    val direct = message?.trim().orEmpty()
    if (direct.isNotEmpty()) return direct
    val causeMessage = cause?.message?.trim().orEmpty()
    if (causeMessage.isNotEmpty()) return causeMessage
    return toString()
}

private fun remoteIdHash(remoteId: String): Int {
    val hash = remoteId.hashCode()
    return if (hash == Int.MIN_VALUE) 0 else kotlin.math.abs(hash)
}
