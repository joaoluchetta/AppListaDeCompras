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

    // Cache da lista completa para filtrar localmente
    private var listaCompletaCache: List<ItemProduto> = emptyList()

    // Armazena o ID da lista atual
    private var currentListaId: Long = -1L

    fun carregarProdutos(listaId: Long) {
        currentListaId = listaId
        viewModelScope.launch {
            _uiState.value = ProdutoUiState.Loading
            try {
                val produtos = repository.getProdutos(listaId)
                listaCompletaCache = produtos
                atualizarUi(produtos)
            } catch (e: Exception) {
                _uiState.value = ProdutoUiState.Error("Erro ao carregar produtos")
            }
        }
    }

    fun adicionarProduto(produto: ItemProduto) {
        if (currentListaId == -1L) return

        val novaLista = listaCompletaCache + produto
        salvarEAtualizar(novaLista)
    }

    fun removerProdutosSelecionados() {
        if (currentListaId == -1L) return

        // Mantém apenas os itens que NÃO estão marcados (checkBoxItem == false)
        val novaLista = listaCompletaCache.filter { !it.checkBoxItem }
        salvarEAtualizar(novaLista)
    }

    // Atualiza o estado do checkbox e salva imediatamente (melhoria em relação ao original)
    fun atualizarCheckbox(item: ItemProduto, isChecked: Boolean) {
        if (currentListaId == -1L) return

        val novaLista = listaCompletaCache.map {
            if (it.id == item.id) it.copy(checkBoxItem = isChecked) else it
        }
        salvarEAtualizar(novaLista)
    }

    fun filtrar(query: String) {
        if (query.isEmpty()) {
            atualizarUi(listaCompletaCache)
        } else {
            val filtrados = listaCompletaCache.filter {
                it.nomeItem.contains(query, ignoreCase = true)
            }
            // Apenas atualiza a UI, não o cache
            _uiState.value = ProdutoUiState.Success(filtrados)
        }
    }

    private fun salvarEAtualizar(novaLista: List<ItemProduto>) {
        listaCompletaCache = novaLista
        viewModelScope.launch {
            repository.salvarProdutos(currentListaId, novaLista)
            atualizarUi(novaLista)
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