package com.example.listadecompras

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
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
import com.example.listadecompras.databinding.ActivityListaBinding
import kotlinx.coroutines.launch

class ListaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListaBinding

    // Injeção da ViewModel
    private val viewModel: ListaViewModel by viewModels {
        ListaViewModelFactory(ShoppingListRepository(this))
    }

    private var imagemUri: Uri? = null
    private var modoEdicao = false
    private var idListaPai: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityListaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupUI()
        setupListeners()
        setupObservers()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.listaScreen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupUI() {
        // Receber dados do Intent
        modoEdicao = intent.getBooleanExtra("modoEdicao", false)
        idListaPai = intent.getLongExtra("idListaPai", -1L)

        if (modoEdicao) {
            val nomeLista = intent.getStringExtra("nomeLista")
            val imagemLista = intent.getStringExtra("imagemLista")

            binding.editLista.setText(nomeLista)
            binding.btnAdicionar.text = "Salvar Alterações"

            if (imagemLista != null) {
                configurarImagem(imagemLista)
                // Mantém o valor atual caso o usuário não troque a imagem
                imagemUri = try { Uri.parse(imagemLista) } catch (e: Exception) { null }
            }
        }
    }

    private fun configurarImagem(imagemString: String) {
        // Tenta carregar como Resource ID (Inteiro) primeiro
        val imageResId = resources.getIdentifier(imagemString, "drawable", packageName)
        if (imageResId != 0) {
            binding.imageView.setImageResource(imageResId)
        } else {
            // Se não for ID, tenta como URI
            try {
                binding.imageView.setImageURI(Uri.parse(imagemString))
            } catch (e: Exception) {
                binding.imageView.setImageResource(R.drawable.ic_lista_default) // Imagem padrão caso erro
            }
        }
    }

    private fun setupListeners() {
        binding.btnAddImageLista.setOnClickListener {
            exibirDialogoSelecaoImagem()
        }

        binding.btnAdicionar.setOnClickListener {
            val nomeLista = binding.editLista.text.toString()
            // Passa para a ViewModel processar (a string da imagem pode ser null ou o toString da URI)
            val imagemString = imagemUri?.toString() ?: intent.getStringExtra("imagemLista")

            viewModel.salvarLista(idListaPai, nomeLista, imagemString)
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is ListaUiState.Idle -> {}
                        is ListaUiState.Loading -> {
                            // Bloquear botão se quiser
                        }
                        is ListaUiState.Success -> {
                            // Sucesso! Retorna para a tela anterior
                            setResult(RESULT_OK)
                            finish()
                        }
                        is ListaUiState.Error -> {
                            Toast.makeText(this@ListaActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun exibirDialogoSelecaoImagem() {
        val nomesImagens = arrayOf("Shopping", "Supermercado", "Feira", "Posto de Gasolina", "Compra do mês", "Casa")
        val imagensPreDefinidas = arrayOf(
            R.drawable.ic_shopping,
            R.drawable.ic_supermercado,
            R.drawable.ic_feira,
            R.drawable.ic_posto_gasolina,
            R.drawable.ic_mensal,
            R.drawable.ic_casa
        )

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Escolha uma imagem")
        builder.setItems(nomesImagens) { _, which ->
            val imagemSelecionadaId = imagensPreDefinidas[which]
            binding.imageView.setImageResource(imagemSelecionadaId)

            // Salva o caminho como resource android
            val uriString = "android.resource://$packageName/${imagensPreDefinidas[which]}"
            imagemUri = Uri.parse(uriString)
        }
        builder.show()
    }
}