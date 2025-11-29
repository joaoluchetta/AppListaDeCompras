package com.example.listadecompras

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ShoppingListRepository(private val context: Context) {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Leitura: Busca apenas as listas do usuário logado
    suspend fun getListas(): List<ListaItem> {
        val userId = auth.currentUser?.uid ?: return emptyList()

        val snapshot = db.collection("lists")
            .whereEqualTo("userId", userId) // Filtro: Só minhas listas
            .get()
            .await()

        // Converte os documentos do banco em objetos ListaItem
        return snapshot.toObjects(ListaItem::class.java)
    }

    // Criar ou Atualizar (Como passamos o ID, o .set funciona para ambos)
    suspend fun salvarLista(lista: ListaItem) {
        val userId = auth.currentUser?.uid ?: return

        // Garante que a lista tenha o ID do dono
        val listaComDono = lista.copy(userId = userId)

        // Salva no documento com o ID da lista
        db.collection("lists")
            .document(lista.id.toString())
            .set(listaComDono)
            .await()
    }

    // Remover
    suspend fun removerLista(item: ListaItem) {
        db.collection("lists")
            .document(item.id.toString())
            .delete()
            .await()

        // TODO: Futuramente, aqui deletaremos também os itens da subcoleção
    }

    // Em ShoppingListRepository.kt
    fun logout() {
        auth.signOut()
    }
}