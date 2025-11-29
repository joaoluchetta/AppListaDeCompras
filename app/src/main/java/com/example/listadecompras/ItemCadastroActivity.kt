package com.example.listadecompras

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.listadecompras.databinding.ActivityItemCadastroBinding
import kotlinx.coroutines.flow.collectLatest // Importante para StateFlow

class ItemCadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemCadastroBinding

    // ViewModel simples sem Factory (pois não tem dependências no construtor)
    private val viewModel: ItemCadastroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityItemCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupWindowInsets()

        configurarSpinner()
        setupListeners()
        setupObservers()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.itemCadastroScreen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun configurarSpinner() {
        val categorias = resources.getStringArray(R.array.categorias_array)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, categorias)
        binding.dropdownCategoria.setAdapter(arrayAdapter)
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            viewModel.errorState.collectLatest { erro ->
                if (erro != null) {
                    Toast.makeText(this@ItemCadastroActivity, erro, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnAdicionar.setOnClickListener {
            val nomeItem = binding.inputNome.text.toString().trim()
            val quantidadeItemStr = binding.inputQuantidade.text.toString().trim()
            val unidadeItem = binding.inputUnidade.text.toString().trim()
            val categoriaItem = binding.dropdownCategoria.text.toString().trim()

            // Validação via ViewModel
            if (viewModel.validarEntrada(nomeItem, quantidadeItemStr)) {

                val quantidadeItem = quantidadeItemStr.toIntOrNull() ?: 0
                val idImage = selecionarImagem(categoriaItem)

                val intent = Intent().apply {
                    putExtra("nomeItem", nomeItem)
                    putExtra("quantidadeItem", quantidadeItem)
                    putExtra("unidadeItem", unidadeItem)
                    putExtra("categoriaItem", categoriaItem)
                    putExtra("idImage", idImage)
                }

                setResult(RESULT_OK, intent)
                finish()
            }
        }

        binding.btnCancelar.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    private fun selecionarImagem(categoria: String): Int {
        return when (categoria) {
            "Carne" -> R.drawable.ic_carne_24px
            "Fruta" -> R.drawable.ic_frutas_24px
            "Verdura" -> R.drawable.ic_verduras_24px
            "Legume" -> R.drawable.ic_legume_24px
            "Snacks" -> R.drawable.ic_snack_24px
            "Sapato" -> R.drawable.ic_sapatos_24px
            "Roupa" -> R.drawable.ic_camiseta_24px
            else -> R.drawable.ic_outro // Verifique se este nome está correto no seu projeto
        }
    }
}