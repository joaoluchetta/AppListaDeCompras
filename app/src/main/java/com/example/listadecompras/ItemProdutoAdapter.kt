package com.example.listadecompras

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.listadecompras.databinding.ActivityItemProdutoBinding

class ItemProdutoAdapter(private val onClickListener: (ItemProduto) -> Unit,
                         private val onSelectionChanged: (Int) -> Unit) : RecyclerView.Adapter<ItemProdutoAdapter.ItemProdutoViewHolder>() {
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

        holder.binding.checkBoxItem.setOnCheckedChangeListener { _, isChecked ->
            item.checkBoxItem = isChecked
            onSelectionChanged(getItensSelecionados().size) // Notifica a Activity
        }

        holder.itemView.setOnClickListener {
            onClickListener(item)
        }
    }

    fun adicionarItem(item: ItemProduto) {
        itensLista.add(item)
        notifyItemInserted(itensLista.size - 1)
        onSelectionChanged(getItensSelecionados().size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItens(novaLista: List<ItemProduto>) {
        itensLista.clear()
        itensLista.addAll(novaLista)
        notifyDataSetChanged()
    }

    fun getItensSelecionados(): List<ItemProduto> {
        return itensLista.filter { it.checkBoxItem }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removerItensSelecionados() {
        val itensParaRemover = getItensSelecionados()
        itensLista.removeAll(itensParaRemover)
        notifyDataSetChanged()
        onSelectionChanged(0) // Notifica a Activity que a seleção foi limpa
    }

    fun getItens(): List<ItemProduto> {
        return itensLista.toList()
    }

    override fun getItemCount(): Int = itensLista.size
}