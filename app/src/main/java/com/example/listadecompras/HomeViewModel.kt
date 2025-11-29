package com.example.listadecompras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Estados da UI da Home
sealed class HomeUiState {
    object Loading : HomeUiState()
    object Empty : HomeUiState()
    data class Success(val listas: List<ListaItem>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(private val repository: ShoppingListRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    // Mantemos uma cópia da lista completa para poder filtrar sem ir no "banco" toda hora
    private var listaCompletaCache: List<ListaItem> = emptyList()

    fun carregarListas() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val listas = repository.getListas()
                listaCompletaCache = listas // Atualiza o cache

                if (listas.isEmpty()) {
                    _uiState.value = HomeUiState.Empty
                } else {
                    _uiState.value = HomeUiState.Success(listas)
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Erro ao carregar listas")
            }
        }
    }

    fun filtrarListas(query: String) {
        if (query.isEmpty()) {
            // Se a busca estiver vazia, mostra a lista completa que temos no cache
            if (listaCompletaCache.isEmpty()) {
                _uiState.value = HomeUiState.Empty
            } else {
                _uiState.value = HomeUiState.Success(listaCompletaCache)
            }
        } else {
            // Filtra sobre o cache
            val filtradas = listaCompletaCache.filter {
                it.nomeLista.contains(query, ignoreCase = true)
            }
            // Não mudamos o estado para Empty aqui para não confundir com "sem listas cadastradas"
            // Apenas retornamos a lista filtrada (que pode ser vazia)
            _uiState.value = HomeUiState.Success(filtradas)
        }
    }

    fun excluirLista(item: ListaItem) {
        viewModelScope.launch {
            repository.removerLista(item)
            carregarListas() // Recarrega os dados após excluir
        }
    }
}