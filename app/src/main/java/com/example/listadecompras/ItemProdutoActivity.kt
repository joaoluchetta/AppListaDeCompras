package com.example.listadecompras

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listadecompras.databinding.ActivityProdutoBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ItemProdutoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProdutoBinding
    private lateinit var adapter: ItemProdutoAdapter
    private var idListaPai: Long = -1L
    private val gson = Gson()

    private val cadastroLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                // Extraia todos os dados do Intent
                val nomeItem = result.data!!.getStringExtra("nomeItem") ?: return@registerForActivityResult
                val quantidadeItem = result.data!!.getIntExtra("quantidadeItem", 0)
                val unidadeItem = result.data!!.getStringExtra("unidadeItem") ?: ""
                val categoriaItem = result.data!!.getStringExtra("categoriaItem") ?: ""
                val idImage = result.data!!.getIntExtra("idImage", R.drawable.ic_lupa_24px)

                // Crie o objeto ItemProduto com todos os campos
                val novoProduto = ItemProduto(
                    nomeItem = nomeItem,
                    quantidadeItem = quantidadeItem,
                    unidadeItem = unidadeItem,
                    categoria = categoriaItem,
                    checkBoxItem = false,
                    idImage = idImage
                )

                adapter.adicionarItem(novoProduto)
                salvarProdutos()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProdutoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.produtoScreen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Receber o ID da lista da HomeActivity
        idListaPai = intent.getLongExtra("idListaPai", -1L)

        // Configurar o RecyclerView
        adapter = ItemProdutoAdapter { itemClicado ->
            Toast.makeText(this, "Item: ${itemClicado.nomeItem}", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this)

        carregarProdutos()

        binding.btnAddLista.setOnClickListener {
            val intentCadastroProduto = Intent(this, ItemCadastroActivity::class.java)
            cadastroLauncher.launch(intentCadastroProduto)
        }

        binding.btnEditar.setOnClickListener {
            val intentLista = Intent(this, ListaActivity::class.java).apply {
                putExtra("modoEdicao", true)
                putExtra("idListaPai", idListaPai)
            }
            startActivity(intentLista)
        }
    }

    private fun carregarProdutos() {
        if (idListaPai != -1L) {
            val sharedPref = getSharedPreferences("produtos_lista_${idListaPai}", Context.MODE_PRIVATE)
            val json = sharedPref.getString("produtos", null)
            val type = object : TypeToken<List<ItemProduto>>() {}.type
            val produtosSalvos: List<ItemProduto> = gson.fromJson(json, type) ?: emptyList()
            adapter.setItens(produtosSalvos)
        }
    }

    private fun salvarProdutos() {
        if (idListaPai != -1L) {
            val sharedPref = getSharedPreferences("produtos_lista_${idListaPai}", Context.MODE_PRIVATE)
            val json = gson.toJson(adapter.getItens())
            with(sharedPref.edit()) {
                putString("produtos", json)
                apply()
            }
        }
    }
}