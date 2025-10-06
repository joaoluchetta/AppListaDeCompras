package com.example.listadecompras

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.listadecompras.databinding.ActivityItemCadastroBinding

class ItemCadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemCadastroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityItemCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.itemCadastroScreen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val categorias = resources.getStringArray(R.array.categorias_array)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, categorias)
        binding.dropdownCategoria.setAdapter(arrayAdapter)

        binding.btnAdicionar.setOnClickListener {
            val nomeItem = binding.inputNome.text.toString().trim()
            val quantidadeItemStr = binding.inputQuantidade.text.toString().trim()
            val unidadeItem = binding.inputUnidade.text.toString().trim()
            val categoriaItem = binding.dropdownCategoria.text.toString().trim()

            if (nomeItem.isEmpty() || quantidadeItemStr.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val quantidadeItem = quantidadeItemStr.toIntOrNull() ?: 0

            val intent = Intent().apply {
                putExtra("nomeItem", nomeItem)
                putExtra("quantidadeItem", quantidadeItem)
                putExtra("unidadeItem", unidadeItem)
                putExtra("categoriaItem", categoriaItem)

                when (categoriaItem) {
                    "Fruta" -> putExtra("idImage", R.drawable.ic_frutas_24px)
                    "Carne" -> putExtra("idImage", R.drawable.ic_carne_24px)
                    "Verdura" -> putExtra("idImage", R.drawable.ic_verduras_24px)
                    "Snacks" -> putExtra("idImage", R.drawable.ic_snack_24px)
                    else -> {
                        putExtra("idImage", R.drawable.ic_outros_24px)
                    }
                }

            }

            setResult(RESULT_OK, intent)
            finish()
        }

        binding.btnCancelar.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}