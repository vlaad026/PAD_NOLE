package com.example.nole.data

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import nole.shared.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
internal class AutoReplyMatcher {
    private val json = Json { ignoreUnknownKeys = true }
    private val loadMutex = Mutex()
    private var cachedRules: List<AutoReplyRule>? = null


    //Busca si el mensaje está en el JSON para poder responder
    suspend fun findReplies(messageText: String): List<String> {
        val rules = loadRules()
        val match = rules.firstOrNull { rule ->
            rule.trigger.isNotBlank() && messageText.contains(rule.trigger, ignoreCase = true)
        }
        return match?.replies.orEmpty().take(2)
    }

    //Usamos un mutex para cargar el JSON una sola vez
    private suspend fun loadRules(): List<AutoReplyRule> {
        cachedRules?.let { return it }
        return loadMutex.withLock {
            cachedRules?.let { return@withLock it }
            val loaded = try {
                val bytes = Res.readBytes("files/auto_replies.json")
                json.decodeFromString<List<AutoReplyRule>>(bytes.decodeToString())
            } catch (error: Exception) {
                println("NOLE autoReply: ${error.message}")
                emptyList()
            }
            cachedRules = loaded
            loaded
        }
    }
}

@Serializable
private data class AutoReplyRule(
    val trigger: String = "",
    val replies: List<String> = emptyList(),
)
