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

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(UserRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.verificarLoginAutomatico()

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

            viewModel.login(emailLogin, senhaLogin)
        }

        binding.btnEsqueciSenha.setOnClickListener {
            val email = binding.editLogin.text.toString()
            viewModel.recuperarSenha(email)
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
                    binding.loginProgressBar.visibility = android.view.View.GONE
                    binding.btnAcessar.isEnabled = true

                    when (state) {
                        is MainUiState.Idle -> {}
                        is MainUiState.Loading -> {
                            binding.loginProgressBar.visibility = android.view.View.VISIBLE
                            binding.btnAcessar.isEnabled = false
                        }
                        is MainUiState.Success -> {
                            navigateToHome()
                            viewModel.resetState()
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