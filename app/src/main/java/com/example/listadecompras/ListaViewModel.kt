package com.example.listadecompras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ListaUiState {
    object Idle : ListaUiState()
    object Loading : ListaUiState()
    object Success : ListaUiState() // Indica que salvou e pode fechar
    data class Error(val message: String) : ListaUiState()
}

class ListaViewModel(private val repository: ShoppingListRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<ListaUiState>(ListaUiState.Idle)
    val uiState: StateFlow<ListaUiState> = _uiState

    fun salvarLista(idExistente: Long, nomeLista: String, imagemUri: String?) {
        if (nomeLista.isEmpty()) {
            _uiState.value = ListaUiState.Error("Preencha o campo Nome da Lista")
            return
        }

        viewModelScope.launch {
            _uiState.value = ListaUiState.Loading
            try {
                if (idExistente == -1L) {
                    // Modo CRIAÇÃO
                    val novaLista = ListaItem(nomeLista = nomeLista, idImage = imagemUri)
                    repository.adicionarLista(novaLista)
                } else {
                    // Modo EDIÇÃO
                    repository.atualizarLista(idExistente, nomeLista, imagemUri)
                }
                _uiState.value = ListaUiState.Success
            } catch (e: Exception) {
                _uiState.value = ListaUiState.Error("Erro ao salvar lista: ${e.message}")
            }
        }
    }
}