package com.example.listadecompras

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProdutoRepository(private val context: Context) {
    private val gson = Gson()

    // Helper para pegar o SharedPreferences correto baseado no ID da lista pai
    private fun getPrefs(listaId: Long) =
        context.getSharedPreferences("produtos_lista_$listaId", Context.MODE_PRIVATE)

    fun getProdutos(listaId: Long): List<ItemProduto> {
        val json = getPrefs(listaId).getString("produtos", null)
        val type = object : TypeToken<List<ItemProduto>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun salvarProdutos(listaId: Long, produtos: List<ItemProduto>) {
        val json = gson.toJson(produtos)
        getPrefs(listaId).edit().putString("produtos", json).apply()
    }
}