package com.example.nole.domain.usecase

import com.example.nole.domain.repository.MarketplaceRepository

class RegisterUseCase(
    private val repository: MarketplaceRepository,
) {
    suspend operator fun invoke(email: String, password: String, confirmPassword: String): String? {
        if (email.isBlank() || password.isBlank()) {
            return "Por favor, rellena todos los campos"
        }
        if (!email.contains("@")) {
            return "Escribe un correo válido"
        }
        if (password != confirmPassword) {
            return "Las contraseñas no coinciden"
        }
        val username = email.substringBefore("@").trim()
        return repository.register(
            username = username,
            email = email.trim(),
            password = password,
        )
    }
}
