package com.example.nutriragente.ui.avaliacao

import com.example.nutriragente.data.model.FormType
import com.example.nutriragente.ui.avaliacao.step.FormStep
import com.example.nutriragente.ui.avaliacao.step.StepFormFragment

/**
 * Formulário de consumo alimentar para crianças menores de 6 meses.
 *
 * Convertido de AppCompatActivity → Fragment para integrar corretamente
 * ao Navigation Component e ao StepFormFragment (wizard passo a passo).
 *
 * 9 etapas, todas de escolha única (Sim / Não / Não Sabe).
 * As respostas são persistidas no Firestore via FormViewModel conforme
 * o ACS avança — sem necessidade de salvar manualmente ao fim.
 */
class FormsFragment_sixmonth : StepFormFragment() {

    override fun getFormType() = FormType.UNDER_6M
    override fun getFormTitle() = "Consumo Alimentar"

    override fun buildSteps() = listOf(
        FormStep(
            key      = "leite_peito",
            question = "Ontem a criança tomou leite do peito?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key      = "mingau",
            question = "Ontem a criança consumiu mingau?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key      = "agua_cha",
            question = "Ontem a criança tomou água ou chá?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key      = "leite_vaca",
            question = "Ontem a criança tomou leite de vaca?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key      = "formula_infantil",
            question = "Ontem a criança tomou fórmula infantil?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key      = "suco_fruta",
            question = "Ontem a criança tomou suco de fruta?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key      = "fruta",
            question = "Ontem a criança comeu fruta?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key      = "comida_sal",
            question = "Ontem a criança comeu comida de sal?",
            options  = listOf("Sim", "Não", "Não Sabe")
        ),
        FormStep(
            key      = "outros_alimentos",
            question = "Ontem a criança consumiu outros alimentos?",
            options  = listOf("Sim", "Não", "Não Sabe")
        )
    )
}
