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


    // La pantalla lo llama cuando ya ha navegado, para que el éxito no se quede guardado;

    fun onSuccessHandled() {
        _uiState.update { it.copy(isSuccess = false) }
    }

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.EmailChanged -> onEmailChanged(action.email)
            is LoginAction.PasswordChanged -> onPasswordChanged(action.password)
            is LoginAction.ConfirmPasswordChanged -> onConfirmPasswordChanged(action.confirmPassword)
            LoginAction.ToggleMode -> onToggleMode()
            LoginAction.Submit -> onSubmitClick()
            LoginAction.SuccessHandled -> onSuccessHandled()
            LoginAction.ResetToLogin -> resetToLogin()
        }
    }
}
