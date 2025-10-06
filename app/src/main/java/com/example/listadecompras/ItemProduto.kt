package com.example.listadecompras

data class ItemProduto ( val id: Long = System.currentTimeMillis(),
                         val nomeItem: String,
                         val quantidadeItem: Int,
                         val unidadeItem: String,
                         val categoria: String,
                         var checkBoxItem: Boolean,
                         val idImage: Int? = null)