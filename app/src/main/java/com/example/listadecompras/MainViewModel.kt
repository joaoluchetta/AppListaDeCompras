package com.example.listadecompras

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MainUiState {
    object Idle : MainUiState()
    object Loading : MainUiState()
    object Success : MainUiState()
    data class Error(val message: String) : MainUiState()
}

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Idle)
    val uiState: StateFlow<MainUiState> = _uiState

    // NOVO: Verifica se já tem alguém logado ao abrir o app
    fun verificarLoginAutomatico() {
        if (repository.isUsuarioLogado()) {
            _uiState.value = MainUiState.Success
        }
    }

    fun login(email: String, senha: String) {
        if (email.isEmpty() || senha.isEmpty()) {
            _uiState.value = MainUiState.Error("Preencha todos os campos")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = MainUiState.Error("Formato de e-mail inválido")
            return
        }

        viewModelScope.launch {
            _uiState.value = MainUiState.Loading
            try {
                // Chama o login do Firebase
                repository.login(email, senha)
                // Se não cair no catch, deu certo!
                _uiState.value = MainUiState.Success
            } catch (e: Exception) {
                // Pega a mensagem de erro do Firebase (ex: senha errada)
                _uiState.value = MainUiState.Error("Falha no login: ${e.message}")
            }
        }
    }

    // NOVO: Recuperação de Senha (RF001)
    fun recuperarSenha(email: String) {
        if (email.isEmpty()) {
            _uiState.value = MainUiState.Error("Digite seu e-mail para recuperar a senha.")
            return
        }
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading
            try {
                repository.recuperarSenha(email)
                // Usamos o estado de erro apenas para mostrar o Toast, mas é um sucesso
                _uiState.value = MainUiState.Error("E-mail de recuperação enviado! Verifique sua caixa de entrada.")
            } catch (e: Exception) {
                _uiState.value = MainUiState.Error("Erro: ${e.message}")
            }
        }
    }

    fun resetState() {
        _uiState.value = MainUiState.Idle
    }
}