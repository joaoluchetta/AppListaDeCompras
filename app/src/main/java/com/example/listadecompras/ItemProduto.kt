package com.example.listadecompras

data class ItemProduto ( val id: Long = System.currentTimeMillis(),
                         val nomeItem: String,
                         val quantidadeItem: Int,
                         val unidadeItem: String, // Adicione essa propriedade
                         val categoria: String,  // Adicione a categoria
                         var checkBoxItem: Boolean,
                         val idImage: Int? = null)