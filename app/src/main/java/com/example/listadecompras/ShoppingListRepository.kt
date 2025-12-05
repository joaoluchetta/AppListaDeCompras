package com.example.listadecompras

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ShoppingListRepository(private val context: Context) {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getListas(): List<ListaItem> {
        val userId = auth.currentUser?.uid ?: return emptyList()

        val snapshot = db.collection("lists")
            .whereEqualTo("userId", userId) // Filtro: Só minhas listas
            .get()
            .await()

        return snapshot.toObjects(ListaItem::class.java)
    }

    suspend fun salvarLista(lista: ListaItem) {
        val userId = auth.currentUser?.uid ?: return

        val listaComDono = lista.copy(userId = userId)

        db.collection("lists")
            .document(lista.id.toString())
            .set(listaComDono)
            .await()
    }

    suspend fun removerLista(item: ListaItem) {
        db.collection("lists")
            .document(item.id.toString())
            .delete()
            .await()
    }

    fun logout() {
        auth.signOut()
    }
}