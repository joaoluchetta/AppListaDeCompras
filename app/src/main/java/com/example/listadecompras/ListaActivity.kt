package com.example.listadecompras

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.listadecompras.databinding.ActivityHomeBinding
import com.example.listadecompras.databinding.ActivityListaBinding

class ListaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityListaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                binding.imageView.setImageURI(uri)
            }
        }
        binding.btnAddImageLista.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.btnAdicionar.setOnClickListener {
            val nomeLista = binding.editLista.text.toString()
            val imagemLista = R.drawable.ic_default_imagem // por enquanto, fixa

            val intent = Intent()
            intent.putExtra("nomeLista", nomeLista)
            intent.putExtra("imagemLista", imagemLista)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}