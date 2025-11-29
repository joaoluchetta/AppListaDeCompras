package com.example.listadecompras

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.listadecompras.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // Instancia a ViewModel usando a Factory
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(UserRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupListeners()
        setupObservers()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupListeners() {
        binding.btnAcessar.setOnClickListener {
            val emailLogin = binding.editLogin.text.toString()
            val senhaLogin = binding.editSenha.text.toString()

            // Passa a responsabilidade para a ViewModel
            viewModel.login(emailLogin, senhaLogin)
        }

        binding.btnCadastro.setOnClickListener {
            val intentContaCadastro = Intent(this, ContaCadastroActivity::class.java)
            startActivity(intentContaCadastro)
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is MainUiState.Idle -> {
                            // Estado inicial, nada a fazer
                        }
                        is MainUiState.Loading -> {
                            // Aqui você pode mostrar um ProgressBar se tiver no layout
                        }
                        is MainUiState.Success -> {
                            navigateToHome()
                            viewModel.resetState() // Reseta para não navegar de novo ao rotacionar
                        }
                        is MainUiState.Error -> {
                            Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun navigateToHome() {
        val intentHome = Intent(this, HomeActivity::class.java)
        startActivity(intentHome)

        // Limpa os campos de texto visualmente
        binding.editLogin.text.clear()
        binding.editSenha.text.clear()
    }
}