package com.example.listadecompras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ListaViewModelFactory(private val repository: ShoppingListRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}