package com.example.nutriragente.data.repository

import com.example.nutriragente.R
import com.example.nutriragente.data.model.OrientacaoType
import com.example.nutriragente.data.model.OrientacaoTopic

object OrientacoesContentProvider {

    fun subtituloPara(orientacaoType: OrientacaoType): String = when (orientacaoType) {
        OrientacaoType.UNDER_6M -> "0 a 6 meses"
        OrientacaoType.SEVEN_TO_11M -> "7 a 11 meses"
        OrientacaoType.TWELVEM_TWO_YEARS_PLUS -> "12 meses a 2 anos ou mais"
    }

    fun topicosPara(orientacaoType: OrientacaoType): List<OrientacaoTopic> = when (orientacaoType) {
        OrientacaoType.UNDER_6M -> topicosAleitamento
        OrientacaoType.SEVEN_TO_11M -> topicosAlimentacaoComplementar
        OrientacaoType.TWELVEM_TWO_YEARS_PLUS -> topicosPrimeiraInfancia
    }

    // ─────────────────────────────────────────────────────────────────
    // 0 a 6 meses — aleitamento materno exclusivo
    // ─────────────────────────────────────────────────────────────────
    private val topicosAleitamento = listOf(
        OrientacaoTopic(
            title = "Benefícios para a criança",
            iconRes = R.drawable.beneficios_crianca_,
            layoutRes = R.layout.activity_beneficios_crianca
        ),
        OrientacaoTopic(
            title = "Benefícios para a mãe",
            iconRes = R.drawable.beneficios_para_mae,
            layoutRes = R.layout.activity_beneficios_mae
        ),
        OrientacaoTopic(
            title = "Técnica inadequada",
            iconRes = R.drawable.mae_com_dor,
            layoutRes = R.layout.inadequate_technique
        ),
        OrientacaoTopic(
            title = "Técnica de amamentação",
            iconRes = R.drawable.mae_forma_c,
            layoutRes = R.layout.proper_technique
        ),
        OrientacaoTopic(
            title = "Aspecto do leite",
            iconRes = R.drawable.mae_e_bebe,
            layoutRes = R.layout.aspecto_leite
        ),
        OrientacaoTopic(
            title = "Número de mamadas ao dia",
            iconRes = R.drawable.enfermeira_1_1,
            layoutRes = R.layout.n_breastfeeding
        ),
        OrientacaoTopic(
            title = "Água, chá e outros líquidos",
            iconRes = R.drawable.copo_de_agua,
            layoutRes = R.layout.agua_cha
        ),
        OrientacaoTopic(
            title = "Alimentação da mãe",
            iconRes = R.drawable.mae,
            layoutRes = R.layout.mother_diet
        ),
        OrientacaoTopic(
            title = "Calendário de Puericultura",
            iconRes = R.drawable.enfermeira_1,
            layoutRes = R.layout.calendario
        ),
        OrientacaoTopic(
            title = "Esquema Alimentar",
            iconRes = R.drawable.pure_batata,
            layoutRes = R.layout.dietary_plan6
        )
    )

    // ─────────────────────────────────────────────────────────────────
    // 7 a 11 meses — alimentação complementar
    // ─────────────────────────────────────────────────────────────────
    private val topicosAlimentacaoComplementar = listOf(
        OrientacaoTopic(
            title = "Alimentação Complementar",
            iconRes = R.drawable.bebe_com_fruta_inteira,
            layoutRes = R.layout.complementary_feeding
        ),
        OrientacaoTopic(
            title = "Esquema alimentar",
            iconRes = R.drawable.bebe_comendo,
            layoutRes = R.layout.dietary_plan7
        ),
        OrientacaoTopic(
            title = "Grupos de alimentos",
            iconRes = R.drawable.vegetables_two,
            layoutRes = R.layout.grupo_alimentos
        ),
        OrientacaoTopic(
            title = "Consistência do alimento",
            iconRes = R.drawable.pure_batata,
            layoutRes = R.layout.activity_consistencia_alimento
        ),
        OrientacaoTopic(
            title = "Diversidade de alimentos",
            iconRes = R.drawable.boiled_egg,
            layoutRes = R.layout.activity_diversidade_alimentos
        ),
        OrientacaoTopic(
            title = "Horários e gratificações",
            iconRes = R.drawable.ic_calendar,
            layoutRes = R.layout.activity_horarios_ambiente
        ),
        OrientacaoTopic(
            title = "Calendário de puericultura",
            iconRes = R.drawable.ic_calendar,
            layoutRes = R.layout.calendario1
        ),
        OrientacaoTopic(
            title = "Sinais de fome e saciedade",
            iconRes = R.drawable.bebe_se_negando,
            layoutRes = R.layout.screen_sinais
        ),
        OrientacaoTopic(
            title = "Alimentos que devem ser evitados",
            iconRes = R.drawable.sorvete,
            layoutRes = R.layout.screen_evitar
        )
    )

    // ─────────────────────────────────────────────────────────────────
    // 12 meses a 2 anos ou mais — primeira infância
    // ─────────────────────────────────────────────────────────────────
    private val topicosPrimeiraInfancia = listOf(
        OrientacaoTopic(
            title = "Esquema alimentar",
            iconRes = R.drawable.bebe_andando,
            layoutRes = R.layout.dietary_plan12
        ),
        OrientacaoTopic(
            title = "Alimentos que devem ser evitados",
            iconRes = R.drawable.sorvete,
            layoutRes = R.layout.screen_evitar12meses
        ),
        OrientacaoTopic(
            title = "Deficiência de Ferro e Vitaminas",
            iconRes = R.drawable.vitamina_a,
            layoutRes = R.layout.deficiencia_ferro_12_meses
        ),
        OrientacaoTopic(
            title = "Sinais de fome e de saciedade",
            iconRes = R.drawable.bebe_chorando,
            layoutRes = R.layout.screen_sinais_12meses
        ),
        OrientacaoTopic(
            title = "Calendário de puericultura",
            iconRes = R.drawable.ic_calendar,
            layoutRes = R.layout.calendario2
        ),
        OrientacaoTopic(
            title = "Alimentos ricos em ferro e vitaminas",
            iconRes = R.drawable.bebe_com_fruta,
            layoutRes = R.layout.vitamin_rich_foods
        )
    )
}
