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

        fun atualizarEstilo(isChecked: Boolean) {
            if (isChecked) {
                holder.binding.nomeItem.paintFlags = holder.binding.nomeItem.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                holder.binding.nomeItem.alpha = 0.5f
            } else {
                holder.binding.nomeItem.paintFlags = holder.binding.nomeItem.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
                holder.binding.nomeItem.alpha = 1.0f
            }
        }

        holder.binding.checkBoxItem.setOnCheckedChangeListener(null)

        holder.binding.checkBoxItem.isChecked = item.checkBoxItem
        atualizarEstilo(item.checkBoxItem)

        holder.binding.checkBoxItem.setOnCheckedChangeListener { _, isChecked ->
            item.checkBoxItem = isChecked

            atualizarEstilo(isChecked)

            onSelectionChanged(item, isChecked)
        }

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