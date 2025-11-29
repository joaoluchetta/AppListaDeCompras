package com.example.listadecompras

data class ItemProduto(
    val id: Long = System.currentTimeMillis(),
    val nomeItem: String = "",
    val quantidadeItem: Int = 0,
    val unidadeItem: String = "",
    val categoria: String = "",
    var checkBoxItem: Boolean = false,
    val idImage: Int? = null
)