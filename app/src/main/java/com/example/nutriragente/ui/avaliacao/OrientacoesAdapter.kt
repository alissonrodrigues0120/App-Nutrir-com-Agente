package com.example.nutriragente.ui.avaliacao

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nutriragente.R
import com.example.nutriragente.data.model.OrientacaoTopic

class OrientacoesAdapter(
    private val items: List<OrientacaoTopic>,
    private val onItemClick: (OrientacaoTopic) -> Unit
) : RecyclerView.Adapter<OrientacoesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcon: ImageView = view.findViewById(R.id.iv_orientacao_icon)
        val tvTitle: TextView = view.findViewById(R.id.tv_orientacao_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_orientacao_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val topic = items[position]
        holder.ivIcon.setImageResource(topic.iconRes)
        holder.tvTitle.text = topic.title
        holder.itemView.setOnClickListener { onItemClick(topic) }
    }

    override fun getItemCount() = items.size
}
