package com.example.listadecompras

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.listadecompras.databinding.ActivityItemProdutoBinding

class ItemProdutoAdapter(private val onClickListener: (ItemProduto) -> Unit) : RecyclerView.Adapter<ItemProdutoAdapter.ItemProdutoViewHolder>() {
    private val itensLista = mutableListOf<ItemProduto>()

    inner class ItemProdutoViewHolder(val binding: ActivityItemProdutoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemProdutoViewHolder {
        val binding = ActivityItemProdutoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemProdutoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemProdutoViewHolder, position: Int) {
        val item = itensLista[position]
        holder.binding.itemImagem.setImageResource(item.idImage ?: R.drawable.ic_outros_24px)
        holder.binding.nomeItem.text = "${item.nomeItem} (${item.categoria})"
        holder.binding.quantidadeItem.text = "${item.quantidadeItem} ${item.unidadeItem}" // Exibe quantidade e unidade

        // Clicar para marcar/desmarcar o item
        holder.binding.checkBoxItem.isChecked = item.checkBoxItem

        holder.itemView.setOnClickListener {
            onClickListener(item)
        }
    }

    fun adicionarItem(item: ItemProduto) {
        itensLista.add(item)
        notifyItemInserted(itensLista.size - 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItens(novaLista: List<ItemProduto>) {
        itensLista.clear()
        itensLista.addAll(novaLista)
        notifyDataSetChanged()
    }

    fun getItens(): List<ItemProduto> {
        return itensLista.toList()
    }

    override fun getItemCount(): Int = itensLista.size
}