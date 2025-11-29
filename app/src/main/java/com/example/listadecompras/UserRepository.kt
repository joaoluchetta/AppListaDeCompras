package com.example.listadecompras

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(private val context: Context) {

    // Inicializa as ferramentas do Firebase
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Verifica se já tem alguém logado (útil para não pedir login toda vez)
    fun isUsuarioLogado(): Boolean {
        return auth.currentUser != null
    }

    // Retorna o ID do usuário (vamos usar isso para filtrar as listas depois)
    fun getUserId(): String? {
        return auth.currentUser?.uid
    }

    // LOGIN: Autentica com e-mail e senha
    suspend fun login(email: String, senha: String) {
        // O .await() transforma a chamada do Firebase em algo simples de ler (suspende até terminar)
        auth.signInWithEmailAndPassword(email, senha).await()
    }

    // CADASTRO: Cria a conta E salva o nome no Firestore
    suspend fun cadastrar(nome: String, email: String, senha: String) {
        // 1. Cria o usuário no sistema de Autenticação
        val resultadoAuth = auth.createUserWithEmailAndPassword(email, senha).await()
        val uid = resultadoAuth.user?.uid

        // 2. Salva o nome do usuário no Banco de Dados (Firestore)
        if (uid != null) {
            val usuarioMap = hashMapOf(
                "nome" to nome,
                "email" to email
            )
            // Cria um documento na coleção "users" com o mesmo ID da autenticação
            db.collection("users").document(uid).set(usuarioMap).await()
        }
    }

    // LOGOUT
    fun logout() {
        auth.signOut()
    }

    // RECUPERAÇÃO DE SENHA
    suspend fun recuperarSenha(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }
}