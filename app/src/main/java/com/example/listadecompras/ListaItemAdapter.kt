package com.example.listadecompras

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.listadecompras.databinding.ActivityItemListaBinding

class ListaItemAdapter : RecyclerView.Adapter<ListaItemAdapter.ListaItemViewHolder>() {

    private val minhaLista = mutableListOf<ListaItem>()
    // Lista de IDs de imagens que você tem no seu projeto
    private val imagens = listOf(R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground, R.drawable.ic_lupa_24px, R.drawable.ic_launcher_foreground)

    private var contadorCriado = 0

    inner class ListaItemViewHolder(val binding: ActivityItemListaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaItemViewHolder {
        val binding = ActivityItemListaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListaItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListaItemViewHolder, position: Int) {
        val item = minhaLista[position]
        holder.binding.itemImagem.setImageResource(item.idImage ?: R.drawable.ic_launcher_foreground)
        holder.binding.itemTitulo.text = item.nomeLista
    }

    fun adicionarItem() {
        contadorCriado++
        val novaImagemId = imagens[minhaLista.size % imagens.size]
        val novoItem = ListaItem(nomeLista = "Item $contadorCriado", idImage = novaImagemId)
        minhaLista.add(novoItem)
        notifyItemInserted(minhaLista.size - 1)
    }

    fun adicionarItemDireto(item: ListaItem) {
        minhaLista.add(item)
        notifyItemInserted(minhaLista.size - 1)
    }


    override fun getItemCount(): Int =  minhaLista.size;
}