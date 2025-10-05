package com.example.listadecompras

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.listadecompras.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.btnAcessar.setOnClickListener {
            val emailLogin = binding.editLogin.text.toString()
            val senhaLogin = binding.editSenha.text.toString()

            if (emailLogin.isEmpty() || senhaLogin.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailLogin).matches()) {
                Toast.makeText(this, "Formato de e-mail inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
            val emailSalvo = sharedPref.getString("user_email", null)
            val senhaSalva = sharedPref.getString("user_password", null)

            if (emailLogin == emailSalvo && senhaLogin == senhaSalva) {
                val intentHome = Intent(this, HomeActivity::class.java)
                startActivity(intentHome)
                binding.editLogin.text.clear()
                binding.editSenha.text.clear()
            } else {
                Toast.makeText(this, "E-mail ou senha incorretos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCadastro.setOnClickListener {
            val intentContaCadastro = Intent(this, ContaCadastroActivity::class.java)
            startActivity(intentContaCadastro)
        }
    }
}