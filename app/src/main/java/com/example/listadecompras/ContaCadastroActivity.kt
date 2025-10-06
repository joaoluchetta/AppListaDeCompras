package com.example.listadecompras

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.listadecompras.databinding.ActivityContaCadastroBinding

class ContaCadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContaCadastroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityContaCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.contaCadastroScreen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnCadastrar.setOnClickListener {
            val nome = binding.inputNome.text.toString()
            val email = binding.inputEmail.text.toString()
            val senha = binding.editSenha.text.toString()
            val confirmarSenha = binding.inputSenha.text.toString()

            // Lógica de validação
            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "E-mail inválido, por favor digite um e-mail real", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (senha != confirmarSenha) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("user_email", email)
                putString("user_password", senha)
                apply()
            }

            // Notifique o usuário e finalize a tela
            Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
            finish();
        }

    }
}
