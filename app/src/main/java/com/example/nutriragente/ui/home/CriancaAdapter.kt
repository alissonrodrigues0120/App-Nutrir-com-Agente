package com.example.nutriragente.ui.home


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nutriragente.R
import com.example.nutriragente.data.model.Crianca


class CriancaAdapter(private val UserList:MutableList<Crianca>) : RecyclerView.Adapter<CriancaAdapter.ViewHolder>() {


class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome = view.findViewById<TextView>(R.id.itemNome)
        val idade = view.findViewById<TextView>(R.id.itemidade)
        val peso = view.findViewById<TextView>(R.id.itemPeso)
        val altura = view.findViewById<TextView>(R.id.itemAltura)
        val status = view.findViewById<TextView>(R.id.itemStatus)

    }


    override fun getItemCount(): Int {
        return UserList.size
    }


override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_crianca, parent, false)
        return ViewHolder(view)
    }


override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item : Crianca = UserList[position]
        holder.nome.text = item.nome
        holder.idade.text = "${item.idadeMeses} meses"
        holder.peso.text = "${item.peso} kg"
        holder.altura.text = "${item.altura} m"
        holder.status.text = "${item.statusNutricional}"
    }
}
