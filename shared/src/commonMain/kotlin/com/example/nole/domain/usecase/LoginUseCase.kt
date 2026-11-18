package com.example.nole.domain.usecase

import com.example.nole.domain.repository.MarketplaceRepository

class LoginUseCase(
    private val repository: MarketplaceRepository,
) {
    suspend operator fun invoke(emailOrUsername: String, password: String): String? {
        if (emailOrUsername.isBlank() || password.isBlank()) {
            return "Por favor, rellena todos los campos"
        }
        return repository.login(emailOrUsername.trim(), password)
    }
}
