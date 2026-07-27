package com.example.nutriragente.ui.avaliacao // ou o pacote que preferir

import com.example.nutriragente.data.model.FormType

object FormDictionary {

    fun getQuestion(key: String, formType: String): String {
        // Direciona para o dicionário correto com base no tipo do formulário
        return when (formType) {
            FormType.UNDER_6M.name -> getUnder6MonthsQuestion(key)
            FormType.SIX_TO_23M.name -> get6To23MonthsQuestion(key)
            FormType.TWO_YEARS_PLUS.name -> get2YearsPlusQuestion(key)
            else -> key
        }
    }

    private fun getUnder6MonthsQuestion(key: String): String = when (key) {
        "leite_peito" -> "Ontem a criança tomou leite do peito?"
        "mingau" -> "Ontem a criança consumiu mingau?"
        "agua_cha" -> "Ontem a criança tomou água ou chá?"
        "leite_vaca" -> "Ontem a criança tomou leite de vaca?"
        "formula_infantil" -> "Ontem a criança tomou fórmula infantil?"
        "suco_fruta" -> "Ontem a criança tomou suco de fruta?"
        "fruta" -> "Ontem a criança comeu fruta?"
        "comida_sal" -> "Ontem a criança comeu comida de sal?"
        "outros_alimentos" -> "Ontem a criança consumiu outros alimentos?"
        else -> key
    }

    private fun get6To23MonthsQuestion(key: String): String = when (key) {
        "leite_peito" -> "Ontem a criança tomou leite do peito?"
        "fruta" -> "Ontem a criança comeu fruta inteira, em pedaço ou amassada?"
        "fruta_vezes" -> "Quantas vezes a criança comeu fruta?"
        "comida_sal" -> "Ontem a criança comeu comida de sal?"
        "comida_sal_vezes" -> "Quantas vezes a criança comeu comida de sal?"
        "comida_oferecida" -> "Como a comida de sal foi oferecida à criança?"
        "outro_leite" -> "Ontem a criança tomou outro tipo de leite?"
        "mingau_leite" -> "Ontem a criança comeu mingau com leite?"
        "iogurte" -> "Ontem a criança comeu iogurte?"
        "legumes" -> "Ontem a criança comeu legumes (cenoura, chuchu, abobrinha...)?"
        "vegetal_alaranjado" -> "Ontem a criança comeu vegetal ou fruta de cor alaranjada (mamão, manga, cenoura...)?"
        "verdura_folha" -> "Ontem a criança comeu verdura de folha (alface, couve, espinafre...)?"
        "carne_ovo" -> "Ontem a criança comeu carne ou ovo?"
        "figado" -> "Ontem a criança comeu fígado?"
        "feijao" -> "Ontem a criança comeu feijão?"
        "arroz_batata" -> "Ontem a criança comeu arroz, batata, inhame ou macarrão?"
        "hamburguer" -> "Ontem a criança comeu hambúrguer ou embutidos (salsicha, linguiça, mortadela)?"
        "bebidas_adocadas" -> "Ontem a criança bebeu bebidas adoçadas (refrigerante, suco de caixinha, refresco)?"
        "macarrao_instantaneo" -> "Ontem a criança comeu macarrão instantâneo ou salgadinhos de pacote?"
        "biscoito_recheado" -> "Ontem a criança comeu biscoito recheado, doces ou guloseimas?"
        else -> key
    }

    private fun get2YearsPlusQuestion(key: String): String = when (key) {
        "tela_refeicao" -> "A criança tem costume de fazer refeições assistindo TV, computador ou celular?"
        "refeicoes" -> "Quais refeições a criança faz ao longo do dia?\n(Selecione todas que se aplicam)"
        "feijao" -> "Ontem a criança comeu feijão?"
        "frutas_frescas" -> "Ontem a criança comeu frutas frescas?"
        "verduras_legumes" -> "Ontem a criança comeu verduras ou legumes?"
        "hamburguer" -> "Ontem a criança comeu hambúrguer ou embutidos (salsicha, linguiça, nuggets)?"
        "bebidas_adocadas" -> "Ontem a criança bebeu bebidas adoçadas (refrigerante, suco de caixinha, achocolatado)?"
        "macarrao_instantaneo" -> "Ontem a criança comeu macarrão instantâneo, salgadinhos ou chips de pacote?"
        "biscoito_recheado" -> "Ontem a criança comeu biscoito recheado, doces ou guloseimas?"
        else -> key
    }
}
