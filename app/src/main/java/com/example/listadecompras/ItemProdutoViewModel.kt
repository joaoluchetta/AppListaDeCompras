package com.example.listadecompras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProdutoUiState {
    object Loading : ProdutoUiState()
    object Empty : ProdutoUiState()
    data class Success(val produtos: List<ItemProduto>) : ProdutoUiState()
    data class Error(val message: String) : ProdutoUiState()
}

class ItemProdutoViewModel(private val repository: ProdutoRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<ProdutoUiState>(ProdutoUiState.Loading)
    val uiState: StateFlow<ProdutoUiState> = _uiState

    private var listaCompletaCache: List<ItemProduto> = emptyList()
    private var currentListaId: Long = -1L

    fun carregarProdutos(listaId: Long) {
        currentListaId = listaId
        viewModelScope.launch {
            _uiState.value = ProdutoUiState.Loading
            try {
                val produtos = repository.getProdutos(listaId)
                listaCompletaCache = produtos // Atualiza cache
                atualizarUi(produtos)
            } catch (e: Exception) {
                _uiState.value = ProdutoUiState.Error("Erro ao carregar: ${e.message}")
            }
        }
    }

    fun adicionarProduto(produto: ItemProduto) {
        if (currentListaId == -1L) return

        viewModelScope.launch {
            try {
                // Salva no banco
                repository.salvarProduto(currentListaId, produto)
                listaCompletaCache = listaCompletaCache + produto
                atualizarUi(listaCompletaCache)
            } catch (e: Exception) {
                _uiState.value = ProdutoUiState.Error("Erro ao adicionar: ${e.message}")
            }
        }
    }

    fun removerProdutosSelecionados() {
        if (currentListaId == -1L) return

        viewModelScope.launch {
            try {
                val paraRemover = listaCompletaCache.filter { it.checkBoxItem }

                paraRemover.forEach { item ->
                    repository.deletarProduto(currentListaId, item)
                }

                listaCompletaCache = listaCompletaCache.filterNot { it.checkBoxItem }
                atualizarUi(listaCompletaCache)
            } catch (e: Exception) {
                _uiState.value = ProdutoUiState.Error("Erro ao remover: ${e.message}")
            }
        }
    }

    fun atualizarCheckbox(item: ItemProduto, isChecked: Boolean) {
        if (currentListaId == -1L) return

        val itemAtualizado = item.copy(checkBoxItem = isChecked)

        listaCompletaCache = listaCompletaCache.map {
            if (it.id == item.id) itemAtualizado else it
        }

        viewModelScope.launch {
            repository.salvarProduto(currentListaId, itemAtualizado)
        }
    }

    fun filtrar(query: String) {
        if (query.isEmpty()) {
            atualizarUi(listaCompletaCache)
        } else {
            val filtrados = listaCompletaCache.filter {
                it.nomeItem.contains(query, ignoreCase = true)
            }
            _uiState.value = ProdutoUiState.Success(filtrados)
        }
    }

    private fun atualizarUi(lista: List<ItemProduto>) {
        if (lista.isEmpty()) {
            _uiState.value = ProdutoUiState.Empty
        } else {
            _uiState.value = ProdutoUiState.Success(lista)
        }
    }
}