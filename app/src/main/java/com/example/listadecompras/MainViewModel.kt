package com.example.listadecompras

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.FirebaseNetworkException


sealed class MainUiState {
    object Idle : MainUiState()
    object Loading : MainUiState()
    object Success : MainUiState()
    data class Error(val message: String) : MainUiState()
}

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Idle)
    val uiState: StateFlow<MainUiState> = _uiState

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

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = MainUiState.Error("Formato de e-mail inválido")
            return
        }

        viewModelScope.launch {
            _uiState.value = MainUiState.Loading
            try {
                repository.login(email, senha)
                _uiState.value = MainUiState.Success
            } catch (e: FirebaseAuthInvalidUserException) {
                _uiState.value = MainUiState.Error("Conta não encontrada. Cadastre-se primeiro.")
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _uiState.value = MainUiState.Error("E-mail ou senha incorretos.")
            } catch (e: FirebaseNetworkException) {
                _uiState.value = MainUiState.Error("Sem conexão com a internet.")
            } catch (e: Exception) {
                _uiState.value = MainUiState.Error("Erro ao entrar. Tente novamente.")
            }
        }
    }

    fun recuperarSenha(email: String) {
        if (email.isEmpty()) {
            _uiState.value = MainUiState.Error("Digite seu e-mail para recuperar a senha.")
            return
        }
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading
            try {
                repository.recuperarSenha(email)
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