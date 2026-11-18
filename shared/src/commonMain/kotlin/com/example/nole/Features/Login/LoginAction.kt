package com.example.nole.features.login

sealed interface LoginAction {
    data class EmailChanged(val email: String) : LoginAction
    data class PasswordChanged(val password: String) : LoginAction
    data class ConfirmPasswordChanged(val confirmPassword: String) : LoginAction
    data object ToggleMode : LoginAction
    data object Submit : LoginAction
    data object SuccessHandled : LoginAction
    data object ResetToLogin : LoginAction
}
