package com.example.listadecompras

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.listadecompras.databinding.ActivityHomeBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: ListaItemAdapter

    // Configuração do MVVM
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(ShoppingListRepository(this))
    }

    // Launcher simples apenas para saber quando voltar da criação
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Ao voltar da criação, pedimos para a ViewModel recarregar
                viewModel.carregarListas()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupWindowInsets()

        setupRecyclerView()
        setupListeners()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        // Garante que a lista esteja atualizada sempre que a tela aparecer
        viewModel.carregarListas()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homeScreen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupRecyclerView() {
        adapter = ListaItemAdapter(
            onClickListener = { itemClicado ->
                val intentProdutoLista = Intent(this, ItemProdutoActivity::class.java).apply {
                    putExtra("idListaPai", itemClicado.id)
                    putExtra("nomeLista", itemClicado.nomeLista)
                    putExtra("imagemLista", itemClicado.idImage)
                }
                startActivity(intentProdutoLista)
            },
            onLongClickListener = { itemClicado ->
                exibirDialogoExclusao(itemClicado)
            }
        )

        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = GridLayoutManager(this, 2)
    }

    private fun setupListeners() {
        binding.btnAddLista.setOnClickListener {
            val intentLista = Intent(this, ListaActivity::class.java)
            launcher.launch(intentLista)
        }

        binding.btnLogout.setOnClickListener {
            finish()
        }

        binding.inputBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Passa o texto para a ViewModel filtrar
                viewModel.filtrarListas(s.toString())
            }
        })
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is HomeUiState.Loading -> {
                            // Opcional: Mostrar ProgressBar
                        }
                        is HomeUiState.Empty -> {
                            adapter.setLista(emptyList())
                            // Opcional: Mostrar aviso de "Nenhuma lista encontrada"
                        }
                        is HomeUiState.Success -> {
                            adapter.setLista(state.listas)
                        }
                        is HomeUiState.Error -> {
                            Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun exibirDialogoExclusao(item: ListaItem) {
        AlertDialog.Builder(this)
            .setTitle("Excluir Lista")
            .setMessage("Tem certeza que deseja excluir '${item.nomeLista}' e todos os seus itens?")
            .setPositiveButton("Sim") { _, _ ->
                viewModel.excluirLista(item)
                Snackbar.make(binding.root, "Lista removida!", Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton("Não", null)
            .show()
    }
}