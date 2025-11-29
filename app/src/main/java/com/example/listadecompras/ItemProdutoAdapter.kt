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

        // 1. Configura dados básicos
        holder.binding.itemImagem.setImageResource(item.idImage ?: R.drawable.ic_outros_24px)
        holder.binding.nomeItem.text = "${item.nomeItem} (${item.categoria})"
        holder.binding.quantidadeItem.text = "${item.quantidadeItem} ${item.unidadeItem}"

        // 2. Função auxiliar para alterar o visual do texto (Riscado/Normal)
        fun atualizarEstilo(isChecked: Boolean) {
            if (isChecked) {
                // Adiciona o risco (StrikeThrough) e diminui a opacidade
                holder.binding.nomeItem.paintFlags = holder.binding.nomeItem.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                holder.binding.nomeItem.alpha = 0.5f
            } else {
                // Remove o risco e restaura a opacidade total
                holder.binding.nomeItem.paintFlags = holder.binding.nomeItem.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
                holder.binding.nomeItem.alpha = 1.0f
            }
        }

        // 3. Remove listener anterior (importante para RecyclerView não bugar na rolagem)
        holder.binding.checkBoxItem.setOnCheckedChangeListener(null)

        // 4. Define o estado inicial
        holder.binding.checkBoxItem.isChecked = item.checkBoxItem
        atualizarEstilo(item.checkBoxItem) // <--- Aplica o estilo visual na hora que a lista carrega

        // 5. Configura o listener de clique no checkbox
        holder.binding.checkBoxItem.setOnCheckedChangeListener { _, isChecked ->
            item.checkBoxItem = isChecked

            // Atualiza o visual na hora
            atualizarEstilo(isChecked)

            // Avisa a Activity/ViewModel para salvar no banco
            onSelectionChanged(item, isChecked)
        }

        // 6. Clique no corpo do item (para detalhes, se houver)
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