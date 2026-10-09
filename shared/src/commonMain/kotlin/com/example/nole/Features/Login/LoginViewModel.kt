package com.example.nole.Features.Login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // email -> contraseña. Está en memoria: se pierde al cerrar la app.
    private val users = mutableMapOf("test@nole.com" to "1234")

    fun onEmailChanged(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, errorMessage = null) }
    }

    fun onPasswordChanged(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, errorMessage = null) }
    }

    fun onConfirmPasswordChanged(newConfirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = newConfirmPassword, errorMessage = null) }
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

        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor, rellena todos los campos") }
            return
        }

        if (state.isRegisterMode) {
            if (state.password != state.confirmPassword) {
                _uiState.update { it.copy(errorMessage = "Las contraseñas no coinciden") }
                return
            }
            if (users.containsKey(state.email)) {
                _uiState.update { it.copy(errorMessage = "Ese correo ya está registrado") }
                return
            }
            users[state.email] = state.password
            _uiState.update { it.copy(isSuccess = true) }
        } else {
            if (users[state.email] != state.password) {
                _uiState.update { it.copy(errorMessage = "Correo o contraseña incorrectos") }
                return
            }
            _uiState.update { it.copy(isSuccess = true) }
        }
    }

    // La pantalla lo llama cuando ya ha navegado, para que el éxito no se quede guardado
    fun onSuccessHandled() {
        _uiState.update { it.copy(isSuccess = false) }
    }
}