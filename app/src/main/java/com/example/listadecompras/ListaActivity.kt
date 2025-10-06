package com.example.listadecompras

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

        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                binding.imageView.setImageURI(uri)
                imagemUri = uri // 2. Salva a URI na variável
            }
        }
        binding.btnAddImageLista.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnAdicionar.setOnClickListener {
            val nomeLista = binding.editLista.text.toString()

            if (nomeLista.isEmpty()) {
                Toast.makeText(this, "Preencha o campo Nome da Lista", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val novaLista = ListaItem(nomeLista = nomeLista, idImage = imagemUri?.toString())

            salvarNovaLista(novaLista)
            finish()
        }
    }

    private fun salvarNovaLista(novaLista: ListaItem) {
        val gson = Gson()
        val sharedPref = getSharedPreferences("listas_prefs", Context.MODE_PRIVATE)
        val json = sharedPref.getString("listas_compras", null)
        val tipo = object : TypeToken<MutableList<ListaItem>>() {}.type
        val listas = gson.fromJson<MutableList<ListaItem>>(json, tipo) ?: mutableListOf()

        listas.add(novaLista)

        val editor = sharedPref.edit()
        editor.putString("listas_compras", gson.toJson(listas))
        editor.apply()
    }
}