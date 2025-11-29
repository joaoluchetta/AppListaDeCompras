package com.example.listadecompras

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Estados possíveis da tela
sealed class MainUiState {
    object Idle : MainUiState()
    object Loading : MainUiState()
    object Success : MainUiState()
    data class Error(val message: String) : MainUiState()
}

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Idle)
    val uiState: StateFlow<MainUiState> = _uiState

    fun login(email: String, senha: String) {
        // 1. Validações de entrada
        if (email.isEmpty() || senha.isEmpty()) {
            _uiState.value = MainUiState.Error("Preencha todos os campos")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = MainUiState.Error("Formato de e-mail inválido")
            return
        }

        // 2. Processo de Login
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading

            // Simulando um pequeno delay para ver o loading (opcional)
            // delay(500)

            val isSuccess = repository.loginLocal(email, senha)

            if (isSuccess) {
                _uiState.value = MainUiState.Success
            } else {
                _uiState.value = MainUiState.Error("E-mail ou senha incorretos")
            }
        }
    }

    // Método para resetar o estado se voltar para a tela (evita navegar sozinho novamente)
    fun resetState() {
        _uiState.value = MainUiState.Idle
    }
}