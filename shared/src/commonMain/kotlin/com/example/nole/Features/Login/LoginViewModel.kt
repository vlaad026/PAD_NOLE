package com.example.nole.features.login


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nole.domain.usecase.LoginUseCase
import com.example.nole.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {


    private val _uiState = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            LoginAction.OnBackClick -> Unit
            is LoginAction.EmailChanged -> {
                _uiState.update { it.copy(email = action.email, errorMessage = null) }
            }
            is LoginAction.PasswordChanged -> {
                _uiState.update { it.copy(password = action.password, errorMessage = null) }
            }
            is LoginAction.ConfirmPasswordChanged -> {
                _uiState.update { it.copy(confirmPassword = action.confirmPassword, errorMessage = null) }
            }
            LoginAction.ToggleMode -> {
                _uiState.update {
                    it.copy(
                        isRegisterMode = !it.isRegisterMode,
                        errorMessage = null,
                        confirmPassword = ""
                    )
                }
            }
            LoginAction.Submit -> submit()
            LoginAction.SuccessHandled -> {
                _uiState.update { it.copy(isSuccess = false) }
            }
            LoginAction.ResetToLogin -> {
                _uiState.value = LoginUiState()
            }
        }
    }

    private fun submit() {
        val state = _uiState.value
        if (state.isLoading) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val error = if (state.isRegisterMode) {
                registerUseCase(state.email, state.password, state.confirmPassword)
            } else {
                loginUseCase(state.email, state.password)
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
}
