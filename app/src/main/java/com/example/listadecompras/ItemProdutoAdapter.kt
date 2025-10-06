package com.example.listadecompras
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.listadecompras.databinding.ActivityItemListaBinding

class ItemProdutoAdapter {
    private val itensLista = mutableListOf<ItemProduto>()
    // Lista de IDs de imagens que você tem no seu projeto
    private val imagens = listOf(R.drawable.ic_default_imagem)

    private var contadorCriado = 0

    inner class ItemProdutoViewHolder(val binding: ActivityItemProdutoaAdapterBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemProdutoAdapterViewHolder {
        val binding = ActivityItemListaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemProdutoAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemProdutoAdapterViewHolder, position: Int) {
        val item = itensLista[position]
        holder.binding.itemImagem.setImageResource(item.idImage ?: R.drawable.ic_launcher_foreground)
        holder.binding.itemTitulo.text = item.nomeLista

        holder.itemView.setOnClickListener {
            onClickListener(item)
        }
    }
}
