package com.example.nutriragente.ui.avaliacao

import com.example.nutriragente.data.model.FormType
import com.example.nutriragente.ui.avaliacao.step.FormStep
import com.example.nutriragente.ui.avaliacao.step.StepFormFragment
import com.example.nutriragente.ui.avaliacao.step.StepType

/**
 * Formulário de consumo alimentar para crianças a partir de 2 anos.
 *
 * 9 etapas:
 *  - Etapa 2 (refeicoes) usa [StepType.MULTI_CHOICE]: o ACS seleciona
 *    todas as refeições do dia antes de avançar com "Próxima".
 *  - Demais etapas são de escolha única (Sim / Não / Não Sabe).
 */
class FormsFragment_twoyears : StepFormFragment() {

    override fun getFormType() = FormType.TWO_YEARS_PLUS
    override fun getFormTitle() = "Consumo Alimentar"

    override fun buildSteps() = listOf(

        // ── Hábitos alimentares ────────────────────────────────────────────
        FormStep(
            key      = "tela_refeicao",
            question = "A criança tem costume de fazer refeições assistindo TV, computador ou celular?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key      = "refeicoes",
            question = "Quais refeições a criança faz ao longo do dia?\n(Selecione todas que se aplicam)",
            options  = listOf(
                "Café da manhã",
                "Lanche da manhã",
                "Almoço",
                "Lanche da tarde",
                "Jantar",
                "Ceia"
            ),
            type = StepType.MULTI_CHOICE
        ),

        // ── Alimentos saudáveis ────────────────────────────────────────────
        FormStep(
            key      = "feijao",
            question = "Ontem a criança comeu feijão?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key      = "frutas_frescas",
            question = "Ontem a criança comeu frutas frescas?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key      = "verduras_legumes",
            question = "Ontem a criança comeu verduras ou legumes?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),

        // ── Ultraprocessados ───────────────────────────────────────────────
        FormStep(
            key      = "hamburguer",
            question = "Ontem a criança comeu hambúrguer ou embutidos (salsicha, linguiça, nuggets)?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key      = "bebidas_adocadas",
            question = "Ontem a criança bebeu bebidas adoçadas (refrigerante, suco de caixinha, achocolatado)?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key      = "macarrao_instantaneo",
            question = "Ontem a criança comeu macarrão instantâneo, salgadinhos ou chips de pacote?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key      = "biscoito_recheado",
            question = "Ontem a criança comeu biscoito recheado, doces ou guloseimas?",
            options  = listOf("Sim", "Não", "Não Sabe")
        )
    )
}
