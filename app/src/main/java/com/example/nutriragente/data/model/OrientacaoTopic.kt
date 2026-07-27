package com.example.nutriragente.data.model

import androidx.annotation.LayoutRes
import androidx.annotation.DrawableRes

/**
 * Representa um tópico de orientação nutricional (um "card" na tela
 * de Orientações), como "Benefícios da amamentação" ou "Grupos de alimentos".
 *
 * @param title texto exibido no card e no cabeçalho do detalhe.
 * @param iconRes drawable exibido no card.
 * @param bullets lista de recomendações/informações exibidas no detalhe.
 */
data class OrientacaoTopic(
    val title: String,
    @DrawableRes val iconRes: Int,
    @LayoutRes val layoutRes: Int
)
