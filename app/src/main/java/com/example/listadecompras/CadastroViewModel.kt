package com.example.listadecompras

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CadastroUiState {
    object Idle : CadastroUiState()
    object Loading : CadastroUiState()
    object Success : CadastroUiState()
    data class Error(val message: String) : CadastroUiState()
}

class CadastroViewModel(private val repository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<CadastroUiState>(CadastroUiState.Idle)
    val uiState: StateFlow<CadastroUiState> = _uiState

    fun cadastrar(nome: String, email: String, senha: String, confirmarSenha: String) {
        // Validações locais
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
            _uiState.value = CadastroUiState.Error("Preencha todos os campos")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = CadastroUiState.Error("E-mail inválido")
            return
        }

        if (senha != confirmarSenha) {
            _uiState.value = CadastroUiState.Error("As senhas não coincidem")
            return
        }

        if (senha.length < 6) {
            _uiState.value = CadastroUiState.Error("A senha deve ter pelo menos 6 caracteres")
            return
        }

        // Chamada ao Firebase
        viewModelScope.launch {
            _uiState.value = CadastroUiState.Loading
            try {
                // Cria Auth + Salva no Firestore
                repository.cadastrar(nome, email, senha)
                _uiState.value = CadastroUiState.Success
            } catch (e: Exception) {
                _uiState.value = CadastroUiState.Error("Erro ao cadastrar: ${e.message}")
            }
        }
    }
}