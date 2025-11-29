package com.example.listadecompras

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ItemCadastroViewModel : ViewModel() {

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

    fun validarEntrada(nome: String, quantidadeStr: String): Boolean {
        if (nome.isEmpty() || quantidadeStr.isEmpty()) {
            _errorState.value = "Preencha todos os campos"
            return false
        }
        _errorState.value = null
        return true
    }
}