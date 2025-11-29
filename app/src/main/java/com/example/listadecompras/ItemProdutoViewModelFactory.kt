package com.example.listadecompras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ItemProdutoViewModelFactory(private val repository: ProdutoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemProdutoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemProdutoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}