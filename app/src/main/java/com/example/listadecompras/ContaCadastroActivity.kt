package com.example.listadecompras

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
import com.example.listadecompras.databinding.ActivityContaCadastroBinding
import kotlinx.coroutines.launch

class ContaCadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContaCadastroBinding

    // Injeção da ViewModel
    private val viewModel: CadastroViewModel by viewModels {
        CadastroViewModelFactory(UserRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityContaCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupListeners()
        setupObservers()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.contaCadastroScreen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupListeners() {
        binding.btnCadastrar.setOnClickListener {
            val nome = binding.inputNome.text.toString()
            val email = binding.inputEmail.text.toString()
            val senha = binding.editSenha.text.toString()
            val confirmarSenha = binding.inputSenha.text.toString()

            // Passa a responsabilidade para a ViewModel
            viewModel.cadastrar(nome, email, senha, confirmarSenha)
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when(state) {
                        is CadastroUiState.Idle -> {}
                        is CadastroUiState.Loading -> {
                            // Opcional: Travar botão ou mostrar loading
                        }
                        is CadastroUiState.Success -> {
                            Toast.makeText(this@ContaCadastroActivity, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                            finish() // Encerra a tela e volta para o Login
                        }
                        is CadastroUiState.Error -> {
                            Toast.makeText(this@ContaCadastroActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}