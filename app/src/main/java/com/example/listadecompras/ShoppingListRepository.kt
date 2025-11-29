package com.example.listadecompras

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

class ShoppingListRepository(private val context: Context) {
    private val gson = Gson()
    private val sharedPref = context.getSharedPreferences("listas_prefs", Context.MODE_PRIVATE)

    // Leitura (já existia)
    fun getListas(): List<ListaItem> {
        val json = sharedPref.getString("listas_compras", null)
        val tipo = object : TypeToken<List<ListaItem>>() {}.type
        return gson.fromJson(json, tipo) ?: emptyList()
    }

    // Método auxiliar privado para pegar a lista mutável (facilita a edição)
    private fun getListasMutable(): MutableList<ListaItem> {
        val json = sharedPref.getString("listas_compras", null)
        val tipo = object : TypeToken<MutableList<ListaItem>>() {}.type
        return gson.fromJson(json, tipo) ?: mutableListOf()
    }

    private fun salvarNoDisco(listas: List<ListaItem>) {
        val editor = sharedPref.edit()
        editor.putString("listas_compras", gson.toJson(listas))
        editor.apply()
    }

    // NOVA: Criar Lista
    fun adicionarLista(novaLista: ListaItem) {
        val listas = getListasMutable()
        listas.add(novaLista)
        salvarNoDisco(listas)
    }

    // NOVA: Editar Lista
    fun atualizarLista(id: Long, novoNome: String, novaImagem: String?) {
        val listas = getListasMutable()
        val itemIndex = listas.indexOfFirst { it.id == id }

        if (itemIndex != -1) {
            val itemAntigo = listas[itemIndex]
            // Cria uma cópia atualizada (assumindo que ListaItem é data class)
            val itemAtualizado = itemAntigo.copy(nomeLista = novoNome, idImage = novaImagem)
            listas[itemIndex] = itemAtualizado
            salvarNoDisco(listas)
        }
    }

    // Remoção (Refatorada para usar o auxiliar salvarNoDisco)
    fun removerLista(item: ListaItem) {
        val listas = getListasMutable()
        val removido = listas.removeIf { it.id == item.id }

        if (removido) {
            salvarNoDisco(listas)
            // Limpa os produtos associados
            context.getSharedPreferences("produtos_lista_${item.id}", Context.MODE_PRIVATE)
                .edit { clear() }
        }
    }
}