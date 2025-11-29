package com.example.listadecompras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// (ListaUiState permanece igual)
sealed class ListaUiState {
    object Idle : ListaUiState()
    object Loading : ListaUiState()
    object Success : ListaUiState()
    object Deleted : ListaUiState()
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
                // Se idExistente for -1, cria um novo ID baseado no tempo
                // Se for um ID válido, usamos ele para manter o mesmo documento
                val idFinal = if (idExistente == -1L) System.currentTimeMillis() else idExistente

                val listaParaSalvar = ListaItem(
                    id = idFinal,
                    nomeLista = nomeLista,
                    idImage = imagemUri
                    // userId será preenchido pelo repositório
                )

                repository.salvarLista(listaParaSalvar)
                _uiState.value = ListaUiState.Success
            } catch (e: Exception) {
                _uiState.value = ListaUiState.Error("Erro ao salvar: ${e.message}")
            }
        }
    }

    fun excluirLista(id: Long) {
        viewModelScope.launch {
            _uiState.value = ListaUiState.Loading
            try {
                // Cria objeto dummy só com ID para deletar
                val item = ListaItem(id = id)
                repository.removerLista(item)
                _uiState.value = ListaUiState.Deleted
            } catch (e: Exception) {
                _uiState.value = ListaUiState.Error("Erro ao excluir: ${e.message}")
            }
        }
    }
}