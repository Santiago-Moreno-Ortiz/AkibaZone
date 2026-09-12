package com.example.akibazone.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    object Unauthenticated : AuthUiState
    object Loading : AuthUiState
    object Registering : AuthUiState
    data class Authenticated(val userName: String, val email: String) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Unauthenticated)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val users = mutableMapOf<String, String>() // Base de datos temporal

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            delay(1000)
            if (users.containsKey(email) && users[email] == password) {
                _uiState.value = AuthUiState.Authenticated(
                    userName = email.substringBefore("@"),
                    email = email
                )
            } else if (!email.contains("@") || password.length < 6) {
                _uiState.value = AuthUiState.Error("Formato de correo o contraseña inválido.")
            } else {
                _uiState.value = AuthUiState.Error("Usuario no encontrado o contraseña incorrecta.")
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            delay(1500)
            if (email.contains("@") && password.length >= 6) {
                if (users.containsKey(email)) {
                    _uiState.value = AuthUiState.Error("Este correo ya está registrado.")
                } else {
                    users[email] = password
                    _uiState.value = AuthUiState.Authenticated(
                        userName = email.substringBefore("@"),
                        email = email
                    )
                }
            } else {
                _uiState.value = AuthUiState.Error("Usa un correo válido y una contraseña de min. 6 caracteres.")
            }
        }
    }

    fun logout() {
        _uiState.value = AuthUiState.Unauthenticated
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Unauthenticated
        }
    }
}
