package com.example.listadecompras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class HomeUiState {
    object Loading : HomeUiState()
    object Empty : HomeUiState()
    data class Success(val listas: List<ListaItem>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(private val repository: ShoppingListRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    private var listaCompletaCache: List<ListaItem> = emptyList()

    fun carregarListas() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                // Chama o Firestore
                val listas = repository.getListas()
                listaCompletaCache = listas

                if (listas.isEmpty()) {
                    _uiState.value = HomeUiState.Empty
                } else {
                    _uiState.value = HomeUiState.Success(listas)
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Erro ao carregar: ${e.message}")
            }
        }
    }

    fun filtrarListas(query: String) {
        if (query.isEmpty()) {
            if (listaCompletaCache.isEmpty()) {
                _uiState.value = HomeUiState.Empty
            } else {
                _uiState.value = HomeUiState.Success(listaCompletaCache)
            }
        } else {
            val filtradas = listaCompletaCache.filter {
                it.nomeLista.contains(query, ignoreCase = true)
            }
            _uiState.value = HomeUiState.Success(filtradas)
        }
    }

    fun excluirLista(item: ListaItem) {
        viewModelScope.launch {
            try {
                repository.removerLista(item)
                carregarListas()
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Erro ao excluir: ${e.message}")
            }
        }
    }

    fun logout() {
        repository.logout()
    }
}