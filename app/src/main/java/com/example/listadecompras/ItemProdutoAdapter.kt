package com.example.listadecompras

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.listadecompras.databinding.ActivityItemProdutoBinding

class ItemProdutoAdapter(
    private val onClickListener: (ItemProduto) -> Unit,
    private val onSelectionChanged: (ItemProduto, Boolean) -> Unit // Passa o item e o estado
) : RecyclerView.Adapter<ItemProdutoAdapter.ItemProdutoViewHolder>() {

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
        holder.binding.quantidadeItem.text = "${item.quantidadeItem} ${item.unidadeItem}"

        // Remove listener anterior para evitar bugs de reciclagem
        holder.binding.checkBoxItem.setOnCheckedChangeListener(null)

        holder.binding.checkBoxItem.isChecked = item.checkBoxItem

        holder.binding.checkBoxItem.setOnCheckedChangeListener { _, isChecked ->
            item.checkBoxItem = isChecked
            // Chama o callback passando o item e o novo valor
            onSelectionChanged(item, isChecked)
        }

        holder.itemView.setOnClickListener {
            onClickListener(item)
        }
    }

    // Nota: Em MVVM puro, geralmente usamos apenas o setItens, mas mantive este
    // caso você ainda use pontualmente. Removi a chamada do listener que causaria erro.
    fun adicionarItem(item: ItemProduto) {
        itensLista.add(item)
        notifyItemInserted(itensLista.size - 1)
        // Removido: onSelectionChanged(...) pois a assinatura mudou e a Activity já observa a lista
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItens(novaLista: List<ItemProduto>) {
        itensLista.clear()
        itensLista.addAll(novaLista)
        notifyDataSetChanged()
    }

    // Helper útil para a ViewModel ou Activity saberem quem está marcado
    fun getItens(): List<ItemProduto> {
        return itensLista.toList()
    }

    // Métodos como removerItensSelecionados e getItensSelecionados podem ser removidos
    // se toda a lógica de exclusão estiver na ViewModel, mas se mantiver, não chame o listener.

    override fun getItemCount(): Int = itensLista.size
}