package com.example.nutriragente.ui.avaliacao

import com.example.nutriragente.data.model.FormType
import com.example.nutriragente.ui.avaliacao.step.FormStep
import com.example.nutriragente.ui.avaliacao.step.StepFormFragment

/**
 * Formulário de consumo alimentar para crianças de 6 a 23 meses.
 *
 * 20 etapas, sendo 3 condicionais:
 *  - "fruta_vezes"       → exibida apenas se fruta == "Sim"
 *  - "comida_sal_vezes"  → exibida apenas se comida_sal == "Sim"
 *  - "comida_oferecida"  → exibida apenas se comida_sal == "Sim"
 *
 * A lógica condicional é declarativa via [FormStep.condition]:
 * o [StepFormFragment] filtra a lista de etapas a cada resposta
 * sem nenhum show/hide manual de views.
 */
class FormsFragment_sixtotwentythree : StepFormFragment() {

    override fun getFormType() = FormType.SIX_TO_23M
    override fun getFormTitle() = "Consumo Alimentar"

    override fun buildSteps() = listOf(

        // ── Leite materno ──────────────────────────────────────────────────
        FormStep(
            key      = "leite_peito",
            question = "Ontem a criança tomou leite do peito?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),

        // ── Fruta + condicional ────────────────────────────────────────────
        FormStep(
            key      = "fruta",
            question = "Ontem a criança comeu fruta inteira, em pedaço ou amassada?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key       = "fruta_vezes",
            question  = "Quantas vezes a criança comeu fruta?",
            options   = listOf("1 vez", "2 vezes", "3 ou mais", "Não Sabe"),
            condition = { it["fruta"] == "Sim" }
        ),

        // ── Comida de sal + condicionais ───────────────────────────────────
        FormStep(
            key      = "comida_sal",
            question = "Ontem a criança comeu comida de sal?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key       = "comida_sal_vezes",
            question  = "Quantas vezes a criança comeu comida de sal?",
            options   = listOf("1 vez", "2 vezes", "3 ou mais", "Não Sabe"),
            condition = { it["comida_sal"] == "Sim" }
        ),
        FormStep(
            key       = "comida_oferecida",
            question  = "Como a comida de sal foi oferecida à criança?",
            options   = listOf(
                "Em pedaços",
                "Amassada",
                "Passada na peneira",
                "No liquidificador",
                "Só o caldo",
                "Não Sabe"
            ),
            condition = { it["comida_sal"] == "Sim" }
        ),

        // ── Outros lácteos e cereais ───────────────────────────────────────
        FormStep(
            key      = "outro_leite",
            question = "Ontem a criança tomou outro tipo de leite?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key      = "mingau_leite",
            question = "Ontem a criança comeu mingau com leite?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key      = "iogurte",
            question = "Ontem a criança comeu iogurte?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),

        // ── Verduras e legumes ─────────────────────────────────────────────
        FormStep(
            key      = "legumes",
            question = "Ontem a criança comeu legumes (cenoura, chuchu, abobrinha…)?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key      = "vegetal_alaranjado",
            question = "Ontem a criança comeu vegetal ou fruta de cor alaranjada (mamão, manga, cenoura…)?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key      = "verdura_folha",
            question = "Ontem a criança comeu verdura de folha (alface, couve, espinafre…)?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),

        // ── Proteínas ──────────────────────────────────────────────────────
        FormStep(
            key      = "carne_ovo",
            question = "Ontem a criança comeu carne ou ovo?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key      = "figado",
            question = "Ontem a criança comeu fígado?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),

        // ── Carboidratos e leguminosas ─────────────────────────────────────
        FormStep(
            key      = "feijao",
            question = "Ontem a criança comeu feijão?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key      = "arroz_batata",
            question = "Ontem a criança comeu arroz, batata, inhame ou macarrão?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),

        // ── Ultraprocessados ───────────────────────────────────────────────
        FormStep(
            key      = "hamburguer",
            question = "Ontem a criança comeu hambúrguer ou embutidos (salsicha, linguiça, mortadela)?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key      = "bebidas_adocadas",
            question = "Ontem a criança bebeu bebidas adoçadas (refrigerante, suco de caixinha, refresco)?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key      = "macarrao_instantaneo",
            question = "Ontem a criança comeu macarrão instantâneo ou salgadinhos de pacote?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key      = "biscoito_recheado",
            question = "Ontem a criança comeu biscoito recheado, doces ou guloseimas?",
            options  = listOf("Sim", "Não", "Não Sabe")
        )
    )
}
