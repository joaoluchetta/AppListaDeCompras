package com.example.listadecompras

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listadecompras.databinding.ActivityProdutoBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ItemProdutoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProdutoBinding
    private lateinit var adapter: ItemProdutoAdapter

    // Injeção da ViewModel
    private val viewModel: ItemProdutoViewModel by viewModels {
        ItemProdutoViewModelFactory(ProdutoRepository(this))
    }

    private var idListaPai: Long = -1L

    // Launcher para receber o novo produto da tela de cadastro
    private val cadastroLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val data = result.data!!
                val nomeItem = data.getStringExtra("nomeItem") ?: return@registerForActivityResult

                val novoProduto = ItemProduto(
                    nomeItem = nomeItem,
                    quantidadeItem = data.getIntExtra("quantidadeItem", 0),
                    unidadeItem = data.getStringExtra("unidadeItem") ?: "",
                    categoria = data.getStringExtra("categoriaItem") ?: "",
                    checkBoxItem = false,
                    idImage = data.getIntExtra("idImage", R.drawable.ic_outros_24px)
                )

                viewModel.adicionarProduto(novoProduto)
                Snackbar.make(binding.root, "Produto adicionado!", Snackbar.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProdutoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()

        idListaPai = intent.getLongExtra("idListaPai", -1L)
        if (idListaPai == -1L) {
            Toast.makeText(this, "Erro ao carregar lista", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val nomeDaLista = intent.getStringExtra("nomeLista") ?: "Lista de Compras"
        binding.textTituloHome.text = nomeDaLista

        setupRecyclerView()
        setupListeners()
        setupObservers()

        // Carrega os dados iniciais
        viewModel.carregarProdutos(idListaPai)
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.produtoScreen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupRecyclerView() {
        adapter = ItemProdutoAdapter(
            onClickListener = { item ->
                // Ação de clique simples (ex: mostrar detalhes)
                Toast.makeText(this, "Item: ${item.nomeItem}", Toast.LENGTH_SHORT).show()
            },
            onSelectionChanged = { item, isChecked ->
                // Atualiza na ViewModel para salvar
                viewModel.atualizarCheckbox(item, isChecked)
                atualizarBotaoDelete()
            }
        )
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
    }

    private fun setupListeners() {
        binding.btnAddLista.setOnClickListener {
            val intentCadastro = Intent(this, ItemCadastroActivity::class.java)
            cadastroLauncher.launch(intentCadastro)
        }

        binding.btnDelete.setOnClickListener {
            viewModel.removerProdutosSelecionados()
            Snackbar.make(binding.root, "Itens removidos!", Snackbar.LENGTH_SHORT).show()
            binding.btnDelete.visibility = View.GONE
        }

        binding.inputBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filtrar(s.toString())
            }
        })

        // Botão Editar (levava para editar a lista pai)
        binding.btnEditar.setOnClickListener {
            // Lógica para editar a lista pai (mantida simplificada aqui)
            // O ideal seria ter um método no ShoppingListRepository para buscar uma única lista
            // Mas como já temos o ID, podemos apenas reabrir a ListaActivity com os dados atuais
            // ou implementar uma busca no repo.
            Toast.makeText(this, "Edição da lista pai não implementada neste passo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when(state) {
                        is ProdutoUiState.Loading -> { } // Loading
                        is ProdutoUiState.Empty -> {
                            adapter.setItens(emptyList())
                            atualizarBotaoDelete()
                        }
                        is ProdutoUiState.Success -> {
                            adapter.setItens(state.produtos)
                            atualizarBotaoDelete()
                        }
                        is ProdutoUiState.Error -> {
                            Toast.makeText(this@ItemProdutoActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun atualizarBotaoDelete() {
        // Verifica no adapter ou na lista atual se tem alguém marcado
        // Como o adapter tem a lista visual, podemos perguntar a ele ou calcular na lista recebida
        val temSelecionados = adapter.getItens().any { it.checkBoxItem }
        binding.btnDelete.visibility = if (temSelecionados) View.VISIBLE else View.GONE
    }
}