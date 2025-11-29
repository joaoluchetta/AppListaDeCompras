package com.example.listadecompras

import android.content.Context

class UserRepository(private val context: Context) {

    fun loginLocal(email: String, senha: String): Boolean {
        // Acesso aos dados isolado aqui
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val emailSalvo = sharedPref.getString("user_email", null)
        val senhaSalva = sharedPref.getString("user_password", null)

        return email == emailSalvo && senha == senhaSalva
    }

    fun salvarUsuario(nome: String, email: String, senha: String) {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("user_name", nome) // Adicionei o nome também, pode ser útil
            putString("user_email", email)
            putString("user_password", senha)
            apply()
        }
    }
}