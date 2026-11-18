package com.example.nole

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()

internal actual val ioDispatcher: CoroutineDispatcher = Dispatchers.Default