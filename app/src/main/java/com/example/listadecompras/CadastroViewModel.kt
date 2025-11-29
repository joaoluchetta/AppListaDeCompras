package com.example.listadecompras

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Estados da tela de cadastro
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
        // 1. Validações
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
            _uiState.value = CadastroUiState.Error("Preencha todos os campos")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = CadastroUiState.Error("E-mail inválido, por favor digite um e-mail real")
            return
        }

        if (senha != confirmarSenha) {
            _uiState.value = CadastroUiState.Error("As senhas não coincidem")
            return
        }

        // 2. Salvar dados
        viewModelScope.launch {
            _uiState.value = CadastroUiState.Loading
            try {
                repository.salvarUsuario(nome, email, senha)
                _uiState.value = CadastroUiState.Success
            } catch (e: Exception) {
                _uiState.value = CadastroUiState.Error("Erro ao salvar usuário")
            }
        }
    }
}