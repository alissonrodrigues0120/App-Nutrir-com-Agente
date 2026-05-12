package com.example.nutriragente.ui.avaliacao

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nutriragente.R

class FormResumoAdapter(private val items: List<Pair<String, String>>) : 
    RecyclerView.Adapter<FormResumoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvQuestion: TextView = view.findViewById(R.id.tv_question)
        val tvAnswer: TextView = view.findViewById(R.id.tv_answer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_consumo_resumo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (question, answer) = items[position]
        holder.tvQuestion.text = formatKey(question)
        holder.tvAnswer.text = answer
    }

    override fun getItemCount() = items.size

    private fun formatKey(key: String): String {
        return key.replace("_", " ")
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}
