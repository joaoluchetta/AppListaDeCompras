package com.example.listadecompras

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.listadecompras.databinding.ActivityItemListaBinding

class ListaItemAdapter(private val onClickListener: (ListaItem) -> Unit,
                       private val onLongClickListener: (ListaItem) -> Unit) : RecyclerView.Adapter<ListaItemAdapter.ListaItemViewHolder>() {

    private val minhaLista = mutableListOf<ListaItem>()

    inner class ListaItemViewHolder(val binding: ActivityItemListaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaItemViewHolder {
        val binding = ActivityItemListaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListaItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListaItemViewHolder, position: Int) {
        val item = minhaLista[position]
        if (item.idImage != null) {
            try {
                // Tenta converter para Int (se for um drawable)
                val resId = item.idImage!!.toInt()
                holder.binding.itemImagem.setImageResource(resId)
            } catch (e: NumberFormatException) {
                // Se não for Int, é uma URI, então carrega a partir dela
                holder.binding.itemImagem.setImageURI(Uri.parse(item.idImage))
            }
        } else {
            // Caso a imagem seja nula, use o placeholder
            holder.binding.itemImagem.setImageResource(R.drawable.ic_lista_default)
        }
        holder.binding.itemTitulo.text = item.nomeLista

        holder.itemView.setOnClickListener {
            onClickListener(item)
        }

        holder.itemView.setOnLongClickListener {
            onLongClickListener(item)
            true
        }
    }

    fun adicionarItemDireto(item: ListaItem) {
        minhaLista.add(item)
        notifyItemInserted(minhaLista.size - 1)
    }

    fun setLista(novaLista: List<ListaItem>) {
        minhaLista.clear()
        minhaLista.addAll(novaLista)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int =  minhaLista.size;
}