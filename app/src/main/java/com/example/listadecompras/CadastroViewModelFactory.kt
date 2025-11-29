package com.example.listadecompras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CadastroViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CadastroViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CadastroViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}