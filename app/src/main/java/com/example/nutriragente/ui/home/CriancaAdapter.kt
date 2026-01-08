package com.example.nutriragente.ui.home


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nutriragente.R
import com.example.nutriragente.data.database.entities.Crianca


class CriancaAdapter : ListAdapter<Crianca, CriancaAdapter.ViewHolder>(Diff) {


object Diff : DiffUtil.ItemCallback<Crianca>() {
        override fun areItemsTheSame(old: Crianca, newItem: Crianca) = old.id == newItem.id
        override fun areContentsTheSame(old: Crianca, newItem: Crianca) = old == newItem
    }


class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome = view.findViewById<TextView>(R.id.itemNome)
        val idade = view.findViewById<TextView>(R.id.itemIdade)
        val peso = view.findViewById<TextView>(R.id.itemPeso)
        val altura = view.findViewById<TextView>(R.id.itemAltura)
    }


override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.item_crianca, parent, false)
        return ViewHolder(view)
    }


override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.nome.text = item.nome
        holder.idade.text = "${item.idadeMeses} meses"
        holder.peso.text = "${item.peso} kg"
        holder.altura.text = "${item.altura} cm"
    }
}
