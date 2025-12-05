package com.example.listadecompras

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProdutoRepository(private val context: Context) {

    private val db = FirebaseFirestore.getInstance()

    private fun getItemsCollection(listaId: Long) =
        db.collection("lists").document(listaId.toString()).collection("items")

    suspend fun getProdutos(listaId: Long): List<ItemProduto> {
        val snapshot = getItemsCollection(listaId).get().await()
        return snapshot.toObjects(ItemProduto::class.java)
    }

    suspend fun salvarProduto(listaId: Long, item: ItemProduto) {
        getItemsCollection(listaId)
            .document(item.id.toString())
            .set(item)
            .await()
    }

    suspend fun deletarProduto(listaId: Long, item: ItemProduto) {
        getItemsCollection(listaId)
            .document(item.id.toString())
            .delete()
            .await()
    }
}