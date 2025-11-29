package com.example.listadecompras

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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

    private val viewModel: ListaViewModel by viewModels {
        ListaViewModelFactory(ShoppingListRepository(this))
    }

    private var imagemUri: Uri? = null
    private var modoEdicao = false
    private var idListaPai: Long = -1L

    // 1. Launcher do Photo Picker (O padrão moderno recomendado)
    // Não precisa de permissão no manifesto para usar este método
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            // Garante que o app continue tendo acesso à foto mesmo se reiniciar
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            contentResolver.takePersistableUriPermission(uri, flag)

            binding.imageView.setImageURI(uri)
            imagemUri = uri
        } else {
            Toast.makeText(this, "Nenhuma imagem selecionada", Toast.LENGTH_SHORT).show()
        }
    }

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
        modoEdicao = intent.getBooleanExtra("modoEdicao", false)
        idListaPai = intent.getLongExtra("idListaPai", -1L)

        if (modoEdicao) {
            val nomeLista = intent.getStringExtra("nomeLista")
            val imagemLista = intent.getStringExtra("imagemLista")

            binding.editLista.setText(nomeLista)
            binding.btnAdicionar.text = "Salvar Alterações"

            if (imagemLista != null) {
                configurarImagem(imagemLista)
                imagemUri = try { Uri.parse(imagemLista) } catch (e: Exception) { null }
            }
        }
    }

    private fun configurarImagem(imagemString: String) {
        val imageResId = resources.getIdentifier(imagemString, "drawable", packageName)
        if (imageResId != 0) {
            binding.imageView.setImageResource(imageResId)
        } else {
            try {
                binding.imageView.setImageURI(Uri.parse(imagemString))
            } catch (e: Exception) {
                binding.imageView.setImageResource(R.drawable.ic_lista_default)
            }
        }
    }

    private fun setupListeners() {
        binding.btnAddImageLista.setOnClickListener {
            exibirDialogoSelecaoImagem()
        }

        binding.btnAdicionar.setOnClickListener {
            val nomeLista = binding.editLista.text.toString()
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
                        is ListaUiState.Loading -> {}
                        is ListaUiState.Success -> {
                            val resultIntent = Intent().apply {
                                putExtra("nomeLista", binding.editLista.text.toString())
                            }
                            setResult(RESULT_OK, resultIntent)
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
        val nomesImagens = arrayOf("Escolher da Galeria", "Shopping", "Supermercado", "Feira", "Posto de Gasolina", "Compra do mês", "Casa")
        val imagensPreDefinidas = arrayOf(0, R.drawable.ic_shopping, R.drawable.ic_supermercado, R.drawable.ic_feira, R.drawable.ic_posto_gasolina, R.drawable.ic_mensal, R.drawable.ic_casa)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Escolha uma imagem")
        builder.setItems(nomesImagens) { _, which ->
            if (which == 0) {
                // Chama o Photo Picker diretamente (sem checkPermission)
                // O sistema lida com a privacidade automaticamente
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                val imagemSelecionadaId = imagensPreDefinidas[which]
                binding.imageView.setImageResource(imagemSelecionadaId)
                val uriString = "android.resource://$packageName/${imagemSelecionadaId}"
                imagemUri = Uri.parse(uriString)
            }
        }
        builder.show()
    }
}