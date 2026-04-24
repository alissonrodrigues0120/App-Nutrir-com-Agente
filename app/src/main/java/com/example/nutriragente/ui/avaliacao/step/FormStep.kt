package com.example.nutriragente.ui.avaliacao.step

/**
 * Define uma etapa do formulário de consumo alimentar.
 *
 * @param key      Chave usada no Map<String,String> salvo no Firestore.
 * @param question Texto da pergunta exibido ao ACS.
 * @param options  Lista de opções de resposta.
 * @param type     SINGLE_CHOICE (auto-avança) ou MULTI_CHOICE (checkboxes).
 * @param condition Função que recebe as respostas atuais e retorna se a etapa
 *                  deve ser exibida. Null = sempre visível.
 */
data class FormStep(
    val key: String,
    val question: String,
    val options: List<String>,
    val type: StepType = StepType.SINGLE_CHOICE,
    val condition: ((Map<String, String>) -> Boolean)? = null
)

enum class StepType { SINGLE_CHOICE, MULTI_CHOICE }
