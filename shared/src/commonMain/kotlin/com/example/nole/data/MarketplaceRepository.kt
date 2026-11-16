package com.example.nole.data

import com.example.nole.data.dto.CatalogItemDto
import com.example.nole.data.dto.ChatMessageDto
import com.example.nole.data.dto.SoldPatch
import com.example.nole.data.dto.UserDto
import com.example.nole.data.dto.UserNameDto
import com.example.nole.model.CatalogItem
import com.example.nole.model.ChatMessage
import com.example.nole.model.MarketplaceSection
import com.example.nole.model.User
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.Instant

object MarketplaceRepository {

    private val client = SupabaseProvider.client
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var started = false
    private var sessionGeneration = 0
    private var messagesJob: Job? = null
    private var messagesChannel: RealtimeChannel? = null
    private var channelSeq = 0

    private val _items = MutableStateFlow<List<CatalogItem>>(emptyList())
    val items: StateFlow<List<CatalogItem>> = _items.asStateFlow()

    private val _messages = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    val messages: StateFlow<Map<String, List<ChatMessage>>> = _messages.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun logout() {
        sessionGeneration += 1
        stopMessageSession()
        _messages.value = emptyMap()
        _currentUser.value = null
    }

    fun start() {
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

    suspend fun login(emailOrUsername: String, password: String): String? = withContext(Dispatchers.IO) {
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

    suspend fun register(username: String, email: String, password: String): String? = withContext(Dispatchers.IO) {
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

    suspend fun markSold(productId: String): Boolean = withContext(Dispatchers.IO) {
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

    suspend fun publish(item: CatalogItem): Boolean = withContext(Dispatchers.IO) {
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

    suspend fun sendMessage(productId: String, text: String): Boolean = withContext(Dispatchers.IO) {
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
            true
        } catch (error: Exception) {
            println("NOLE sendMessage: ${error.message}")
            false
        }
    }

    suspend fun seedIfEmpty() {
        withContext(Dispatchers.IO){
            val existing = client.from("catalog_items").select().decodeList<CatalogItemDto>()
            if (existing.isNotEmpty()) return@withContext
            val sellers = mutableMapOf<String, String>()
            for (name in seedCatalog().map { it.sellerUsername }.distinct()) {
                sellers[name] = ensureSeller(name)
            }
            val rows = seedCatalog().mapNotNull { item ->
                val sellerId = sellers[item.sellerUsername] ?: return@mapNotNull null
                item.copy(id = newId()).toDto(sellerId)
            }
            if (rows.isNotEmpty()) {
                client.from("catalog_items").insert(rows)
            }
        }
    }

    private suspend fun refreshCatalog() {
        val remote = client.from("catalog_items").select().decodeList<CatalogItemDto>()
        if (remote.isEmpty()) {
            seedIfEmpty()
        }
        val rows = if (remote.isEmpty()) {
            client.from("catalog_items").select().decodeList<CatalogItemDto>()
        } else {
            remote
        }
        val names = client.from("users")
            .select(Columns.list("id", "username"))
            .decodeList<UserNameDto>()
            .associate { it.id to it.username }
        val places = seedCatalog().associate { it.title to (it.location to it.size) }
        _items.value = rows.map { dto -> dto.toItem(names, places[dto.title]) }
    }

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

    private suspend fun ensureSeller(username: String): String {
        val email = "${username.lowercase()}@nole.com"
        val existing = client.from("users").select(Columns.list("id", "username", "email", "password_hash")) {
            filter { eq("username", username) }
        }.decodeList<UserDto>().firstOrNull()
        if (existing != null) return existing.id
        val created = UserDto(
            id = newId(),
            username = username,
            email = email,
            passwordHash = sha256Hex("1234"),
        )
        client.from("users").insert(created)
        return created.id
    }

    private fun newId(): String = Uuid.random().toString()

    private fun CatalogItem.toDto(sellerId: String) = CatalogItemDto(
        id = id.ifBlank { newId() },
        sellerId = sellerId,
        title = title,
        description = description,
        category = category,
        condition = condition,
        section = section.name,
        priceEuros = priceEuros,
        imageName = if (section == MarketplaceSection.FUTBOL) "nole_azul" else "nole_blanco",
        isSold = isSold,
    )

    private fun CatalogItemDto.toItem(
        usernames: Map<String, String>,
        place: Pair<String, String>?,
    ) = CatalogItem(
        id = id,
        title = title,
        description = description,
        priceEuros = priceEuros,
        location = place?.first.orEmpty(),
        size = place?.second.orEmpty(),
        category = category,
        condition = condition,
        sellerUsername = usernames[sellerId] ?: "Vendedor",
        section = section.toSection(),
        isSold = isSold,
        sellerId = sellerId,
    )

    private fun ChatMessageDto.toMessage(sellerId: String) = ChatMessage(
        id = remoteIdHash(id),
        text = text,
        fromSeller = senderId.isNotEmpty() && senderId == sellerId,
        sentAtMillis = parseCreatedAt(createdAt),
        remoteId = id,
        senderId = senderId,
        receiverId = receiverId,
    )

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

private fun String.toSection(): MarketplaceSection {
    return when (uppercase()) {
        "VEHICULOS", "VEHÍCULOS" -> MarketplaceSection.VEHICULOS
        else -> MarketplaceSection.FUTBOL
    }
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
