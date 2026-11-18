package com.example.nole.features.login


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nole.domain.repository.MarketplaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class LoginViewModel(
    private val repository: MarketplaceRepository,
) : ViewModel() {


    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()


    fun onEmailChanged(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, errorMessage = null) }
    }


    fun onPasswordChanged(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, errorMessage = null) }
    }


    fun onConfirmPasswordChanged(newConfirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = newConfirmPassword, errorMessage = null) }
    }


    fun resetToLogin() {
        _uiState.value = LoginUiState()
    }


    fun onToggleMode() {
        _uiState.update {
            it.copy(
                isRegisterMode = !it.isRegisterMode,
                errorMessage = null,
                confirmPassword = ""
            )
        }
    }


    fun onSubmitClick() {
        val state = _uiState.value
        if (state.isLoading) return

        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor, rellena todos los campos") }
            return
        }

        if (state.isRegisterMode) {
            if (!state.email.contains("@")) {
                _uiState.update { it.copy(errorMessage = "Escribe un correo válido") }
                return
            }
            if (state.password != state.confirmPassword) {
                _uiState.update { it.copy(errorMessage = "Las contraseñas no coinciden") }
                return
            }
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val error = if (state.isRegisterMode) {
                repository.register(
                    username = state.email.substringBefore("@").trim(),
                    email = state.email.trim(),
                    password = state.password,
                )
            } else {
                repository.login(state.email.trim(), state.password)
            }
            if (error != null) println("NOLE auth: $error")
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage = error,
                    isSuccess = error == null,
                )
            }
        }
    }


    // La pantalla lo llama cuando ya ha navegado, para que el éxito no se quede guardado;

    fun onSuccessHandled() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}
