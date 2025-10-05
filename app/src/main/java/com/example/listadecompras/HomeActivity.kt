package com.example.listadecompras

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.listadecompras.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: ListaItemAdapter

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val nomeLista = result.data!!.getStringExtra("nomeLista") ?: return@registerForActivityResult
                val imagemLista = result.data!!.getIntExtra("imagemLista", R.drawable.ic_default_imagem)

                val novaLista = ListaItem(nomeLista, imagemLista)
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

        adapter = ListaItemAdapter()
        binding.recyclerview.adapter = adapter
        binding.recyclerview.layoutManager = GridLayoutManager(this, 2)

        adapter.adicionarItem()
        adapter.adicionarItem()
        adapter.adicionarItem()
        adapter.adicionarItem()

        binding.btnAddLista.setOnClickListener {
            val intentLista = Intent(this, ListaActivity::class.java)
            launcher.launch(intentLista)
        }
    }
}