package com.example.listadecompras

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.listadecompras.databinding.ActivityListaBinding
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

class ListaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListaBinding

    private var imagemUri: Uri? = null
    private var modoEdicao = false
    private var idListaPai: Long = -1L
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityListaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.listaScreen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Receber dados do Intent
        modoEdicao = intent.getBooleanExtra("modoEdicao", false)
        idListaPai = intent.getLongExtra("idListaPai", -1L)

        // Se estiver no modo de edição, preencher os campos com os dados existentes
        if (modoEdicao) {
            val nomeLista = intent.getStringExtra("nomeLista")
            val imagemLista = intent.getStringExtra("imagemLista")

            binding.editLista.setText(nomeLista)
            binding.btnAdicionar.text = "Salvar Alterações"

            if (imagemLista != null) {
                val imageResId = resources.getIdentifier(imagemLista, "drawable", packageName)
                if (imageResId != 0) {
                    binding.imageView.setImageResource(imageResId)
                } else {
                    imagemUri = Uri.parse(imagemLista)
                    binding.imageView.setImageURI(imagemUri)
                }
            }
        }


        binding.btnAddImageLista.setOnClickListener {
            exibirDialogoSelecaoImagem()
        }

        binding.btnAdicionar.setOnClickListener {
            val nomeLista = binding.editLista.text.toString()

            if (nomeLista.isEmpty()) {
                Toast.makeText(this, "Preencha o campo Nome da Lista", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (modoEdicao) {
                salvarAlteracoesLista(nomeLista, imagemUri?.toString())
            } else {
                val novaLista = ListaItem(nomeLista = nomeLista, idImage = imagemUri?.toString())
                salvarNovaLista(novaLista)
            }
            finish()
        }
    }

    private fun salvarNovaLista(novaLista: ListaItem) {
        val sharedPref = getSharedPreferences("listas_prefs", Context.MODE_PRIVATE)
        val json = sharedPref.getString("listas_compras", null)
        val tipo = object : TypeToken<MutableList<ListaItem>>() {}.type
        val listas = gson.fromJson<MutableList<ListaItem>>(json, tipo) ?: mutableListOf()

        listas.add(novaLista)

        val editor = sharedPref.edit()
        editor.putString("listas_compras", gson.toJson(listas))
        editor.apply()
    }


    private fun exibirDialogoSelecaoImagem() {
        val nomesImagens = arrayOf("Shopping", "Supermercado", "Feira", "Posto de Gasolina", "Compra do mês", "Casa") // Nomes descritivos
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

            // Salvar a imagem selecionada como um URI de recurso (se necessário)
            // Isso é útil para carregar depois, especialmente se a imagem for um drawable
            imagemUri = Uri.parse("android.resource://$packageName/${imagensPreDefinidas[which]}")
        }
        builder.show()
    }

    private fun salvarAlteracoesLista(nome: String, idImage: String?) {
        val sharedPref = getSharedPreferences("listas_prefs", Context.MODE_PRIVATE)
        val json = sharedPref.getString("listas_compras", null)
        val tipo = object : TypeToken<MutableList<ListaItem>>() {}.type
        val listas = gson.fromJson<MutableList<ListaItem>>(json, tipo) ?: mutableListOf()

        // Encontra a lista pelo ID
        val listaParaEditar = listas.find { it.id == idListaPai }

        if (listaParaEditar != null) {
            listaParaEditar.nomeLista = nome
            listaParaEditar.idImage = idImage
        }

        val editor = sharedPref.edit()
        editor.putString("listas_compras", gson.toJson(listas))
        editor.apply()
    }
}