package com.example.listadecompras

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.listadecompras.databinding.ActivityHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import android.text.TextWatcher
import android.text.Editable


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: ListaItemAdapter
    private var listasSalvas: List<ListaItem> = emptyList()

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val nomeLista = result.data!!.getStringExtra("nomeLista") ?: return@registerForActivityResult
                val imagemListaString = result.data!!.getStringExtra("imagemLista")

                val novaLista = ListaItem(nomeLista = nomeLista, idImage = imagemListaString )
                adapter.adicionarItemDireto(novaLista)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.homeScreen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = ListaItemAdapter(
            onClickListener = { itemClicado ->
                val intentProdutoLista = Intent(this, ItemProdutoActivity::class.java).apply {
                    putExtra("idListaPai", itemClicado.id)
                }
                startActivity(intentProdutoLista)
            },
            onLongClickListener = { itemClicado ->
                AlertDialog.Builder(this)
                    .setTitle("Excluir Lista")
                    .setMessage("Tem certeza que deseja excluir '${itemClicado.nomeLista}' e todos os seus itens?")
                    .setPositiveButton("Sim") { _, _ ->
                        removerLista(itemClicado)
                    }
                    .setNegativeButton("Não", null)
                    .show()
                true
            }
        )

        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = GridLayoutManager(this, 2)

        carregarListas()

        binding.btnAddLista.setOnClickListener {
            val intentLista = Intent(this, ListaActivity::class.java)
            launcher.launch(intentLista)
        }

        binding.btnLogout.setOnClickListener {
            finish()
        }

        binding.inputBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Filtra a lista sempre que o texto muda
                filterList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }

    private fun carregarListas() {
        val gson = Gson()
        val sharedPref = getSharedPreferences("listas_prefs", Context.MODE_PRIVATE)
        val json = sharedPref.getString("listas_compras", null)
        val tipo = object : TypeToken<List<ListaItem>>() {}.type
        listasSalvas = gson.fromJson(json, tipo) ?: emptyList()
        (adapter as ListaItemAdapter).setLista(listasSalvas)
    }

    private fun filterList(query: String) {
        val filteredList = listasSalvas.filter {
            it.nomeLista.contains(query, ignoreCase = true)
        }
        (adapter as ListaItemAdapter).setLista(filteredList)
    }

    private fun removerLista(item: ListaItem) {
        val gson = Gson()
        val sharedPref = getSharedPreferences("listas_prefs", Context.MODE_PRIVATE)
        val json = sharedPref.getString("listas_compras", null)
        val tipo = object : TypeToken<MutableList<ListaItem>>() {}.type
        val listas = gson.fromJson<MutableList<ListaItem>>(json, tipo) ?: mutableListOf()

        val listaParaRemover = listas.find { it.id == item.id }
        if (listaParaRemover != null) {
            listas.remove(listaParaRemover)

            // Salvar a lista atualizada
            val editor = sharedPref.edit()
            editor.putString("listas_compras", gson.toJson(listas))
            editor.apply()

            // Remover os itens associados a essa lista
            val produtosSharedPref = getSharedPreferences("produtos_lista_${item.id}", Context.MODE_PRIVATE)
            produtosSharedPref.edit().clear().apply()

            // Notificar o adapter sobre a remoção
            adapter.setLista(listas)

            Snackbar.make(binding.root, "Lista removida!", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        carregarListas()
    }
}