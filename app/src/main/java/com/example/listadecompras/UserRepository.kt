package com.example.listadecompras

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(private val context: Context) {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun isUsuarioLogado(): Boolean {
        return auth.currentUser != null
    }

    fun getUserId(): String? {
        return auth.currentUser?.uid
    }

    suspend fun login(email: String, senha: String) {
        auth.signInWithEmailAndPassword(email, senha).await()
    }

    suspend fun cadastrar(nome: String, email: String, senha: String) {
        val resultadoAuth = auth.createUserWithEmailAndPassword(email, senha).await()
        val uid = resultadoAuth.user?.uid

        if (uid != null) {
            val usuarioMap = hashMapOf(
                "nome" to nome,
                "email" to email
            )
            db.collection("users").document(uid).set(usuarioMap).await()
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun recuperarSenha(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }
}