package com.example.nole

import kotlinx.coroutines.CoroutineDispatcher

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

internal expect val ioDispatcher: CoroutineDispatcher