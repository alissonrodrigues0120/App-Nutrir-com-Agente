package com.example.nutriragente.ui.home

import com.example.nutriragente.R
import androidx.core.content.ContextCompat
import android.content.Context 




sealed class ClassificacaoNutricional {
    object Adequado : ClassificacaoNutricional()
    object Magreza : ClassificacaoNutricional()
    object ExcessoPeso : ClassificacaoNutricional()
    
    fun getTextColor(context: Context): Int {
        return when (this) {
            is Adequado -> ContextCompat.getColor(context, R.color.green)
            is Magreza -> ContextCompat.getColor(context, R.color.status_alert)
            is ExcessoPeso -> ContextCompat.getColor(context, R.color.red)
        }
    }
    
    fun getBackgroundColor(): Int {
        return when (this) {
            is Adequado -> R.color.green_light
            is Magreza -> R.color.light_orange
            is ExcessoPeso -> R.color.light_red
        }
    }
}

// Função de conversão
fun String.toClassificacao(): ClassificacaoNutricional {
    return when (this) {
        "Peso Adequado" -> ClassificacaoNutricional.Adequado
        in listOf("Magreza", "Magreza Acentuada") -> ClassificacaoNutricional.Magreza
        in listOf("Sobrepeso", "Obesidade", "Obesidade Grave") -> ClassificacaoNutricional.ExcessoPeso
        else -> ClassificacaoNutricional.Adequado
    }
}
