package com.example.listadecompras

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.listadecompras.databinding.ActivityHomeBinding
import com.example.listadecompras.databinding.ActivityItemProdutoBinding

class ItemProdutoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemProdutoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityItemProdutoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(R.layout.activity_item_produto)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnEditar.setOnClickListener {
            val intentLista = Intent(this, ListaActivity::class.java)
            startActivity(intentLista)
        }
    }
}