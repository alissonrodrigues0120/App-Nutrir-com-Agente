package com.example.nutriragente.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.nutriragente.ui.home.CriancaAdapter 
import com.example.nutriragente.R
import com.example.nutriragente.data.model.Crianca


class CriancaAdapter(
    private val items: List<Crianca>,
    val onItemClick: (Crianca) -> Unit,
    val onDeleteClick:(Crianca) -> Unit
) : RecyclerView.Adapter<CriancaAdapter.ViewHolder>() {



    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.itemNome)
        val idade: TextView = view.findViewById(R.id.itemIdade)
        val peso: TextView = view.findViewById(R.id.itemPeso)
        val altura: TextView = view.findViewById(R.id.itemAltura)
        val status: TextView = view.findViewById(R.id.itemStatus)
        val btnApagar: ImageButton = view.findViewById(R.id.btn_apagar)
        val root: CardView = view.findViewById(R.id.item_crianca)


        private fun formatarIdade(meses: Int): String {
                val anos = meses / 12
                val mesesRestantes = meses % 12
                return when {
                    anos == 0 -> "$mesesRestantes meses"
                    mesesRestantes == 0 -> "$anos ano${if (anos > 1) "s" else ""}"
                    else -> "$anos ano${if (anos > 1) "s" else ""} e $mesesRestantes meses"
                }
        }




        fun bind(crianca: Crianca) {
            nome.text = crianca.nome
            status.text = crianca.statusNutricional
            peso.text = String.format("%.2f kg", crianca.peso)
            altura.text = String.format("%.2f m", crianca.altura)
            idade.text = formatarIdade(crianca.idadeMeses) 

             
            val classificacao = crianca.statusNutricional.toClassificacao()
    
            status.setTextColor(classificacao.getTextColor(itemView.context))
            status.setBackgroundResource(classificacao.getBackgroundColor())
        }


    }


    override fun getItemCount(): Int = items.size



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_crianca, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         holder.bind(items[position])

        // Click on item
        holder.root.setOnClickListener { 
            onItemClick(items[position]) 
        }

        // Click on delete button
        holder.btnApagar.setOnClickListener {
            onDeleteClick(items[position])
        }

    }



}
