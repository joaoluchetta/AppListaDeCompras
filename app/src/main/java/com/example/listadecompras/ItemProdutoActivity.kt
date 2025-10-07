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
import android.text.TextWatcher
import android.text.Editable
import android.view.View
import com.google.android.material.snackbar.Snackbar

class ItemProdutoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProdutoBinding
    private lateinit var adapter: ItemProdutoAdapter
    private var idListaPai: Long = -1L
    private val gson = Gson()
    private var listaCompletaProdutos: List<ItemProduto> = emptyList()

    private val cadastroLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val nomeItem = result.data!!.getStringExtra("nomeItem") ?: return@registerForActivityResult
                val quantidadeItem = result.data!!.getIntExtra("quantidadeItem", 0)
                val unidadeItem = result.data!!.getStringExtra("unidadeItem") ?: ""
                val categoriaItem = result.data!!.getStringExtra("categoriaItem") ?: ""
                val idImage = result.data!!.getIntExtra("idImage", R.drawable.ic_outros_24px)

                val novoProduto = ItemProduto(
                    nomeItem = nomeItem,
                    quantidadeItem = quantidadeItem,
                    unidadeItem = unidadeItem,
                    categoria = categoriaItem,
                    checkBoxItem = false,
                    idImage = idImage
                )

                // Adiciona o novo produto à lista completa
                listaCompletaProdutos = listaCompletaProdutos + novoProduto

                // Atualiza o adaptador
                adapter.setItens(listaCompletaProdutos)

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
        adapter = ItemProdutoAdapter(
            onClickListener = { itemClicado ->
                Toast.makeText(this, "Item: ${itemClicado.nomeItem}", Toast.LENGTH_SHORT).show()
            },
            onSelectionChanged = { selectedCount ->
                if (selectedCount > 0) {
                    binding.btnDelete.visibility = View.VISIBLE
                } else {
                    binding.btnDelete.visibility = View.GONE
                }
            }
        )
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this)

        carregarProdutos()

        binding.btnAddLista.setOnClickListener {
            val intentCadastroProduto = Intent(this, ItemCadastroActivity::class.java)
            cadastroLauncher.launch(intentCadastroProduto)
        }

        binding.btnEditar.setOnClickListener {

            val listaParaEditar = carregarDadosListaPai()
            if (listaParaEditar != null) {
                val intentLista = Intent(this, ListaActivity::class.java).apply {
                    putExtra("modoEdicao", true)
                    putExtra("idListaPai", idListaPai)
                    putExtra("nomeLista", listaParaEditar.nomeLista)
                    putExtra("imagemLista", listaParaEditar.idImage)
                }
                startActivity(intentLista)
            } else {
                Toast.makeText(this, "Erro: Lista não encontrada", Toast.LENGTH_SHORT).show()
            }
        }

        binding.inputBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Não é necessário para essa implementação
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                // Não é necessário para essa implementação
            }
        })

        binding.btnDelete.setOnClickListener {
            removerItens()
        }
    }

    private fun carregarProdutos() {
        if (idListaPai != -1L) {
            val sharedPref = getSharedPreferences("produtos_lista_${idListaPai}", Context.MODE_PRIVATE)
            val json = sharedPref.getString("produtos", null)
            val type = object : TypeToken<List<ItemProduto>>() {}.type

            listaCompletaProdutos = gson.fromJson(json, type) ?: emptyList()

            adapter.setItens(listaCompletaProdutos)
        }
    }

    private fun filterList(query: String) {
        val filteredList = listaCompletaProdutos.filter {
            it.nomeItem.contains(query, ignoreCase = true)
        }
        adapter.setItens(filteredList)
    }

    private fun salvarProdutos() {
        if (idListaPai != -1L) {
            val sharedPref = getSharedPreferences("produtos_lista_${idListaPai}", Context.MODE_PRIVATE)
            val json = gson.toJson(listaCompletaProdutos)

            with(sharedPref.edit()) {
                putString("produtos", json)
                apply()
            }
        }
    }

    private fun removerItens() {
        val itensParaRemover = adapter.getItensSelecionados()

        listaCompletaProdutos = listaCompletaProdutos.filterNot { it in itensParaRemover }

        adapter.setItens(listaCompletaProdutos)

        salvarProdutos()
        Snackbar.make(binding.root, "Itens removidos!", Snackbar.LENGTH_SHORT).show()
    }

    private fun carregarDadosListaPai(): ListaItem? {
        val sharedPref = getSharedPreferences("listas_prefs", Context.MODE_PRIVATE)
        val json = sharedPref.getString("listas_compras", null)
        val type = object : TypeToken<List<ListaItem>>() {}.type
        val listasSalvas: List<ListaItem> = gson.fromJson(json, type) ?: emptyList()

        // Busca a lista pelo ID
        return listasSalvas.find { it.id == idListaPai }
    }
}