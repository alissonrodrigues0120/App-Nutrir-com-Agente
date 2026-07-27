package com.example.nutriragente.data.model

enum class OrientacaoType {
    UNDER_6M,               // 0 a 6 meses
    SEVEN_TO_11M,           // 7 a 11 meses
    TWELVEM_TWO_YEARS_PLUS  // 12 meses em diante (inclui 2 anos ou mais)
    ;

    companion object {
        /**
         * Classifica a faixa etária de orientações a partir da idade da
         * criança em meses. Essa é a única fonte de verdade para essa
         * classificação — sempre usar esta função em vez de comparar
         * `idadeMeses` manualmente em outros lugares do app.
         */
        fun fromIdadeMeses(idadeMeses: Int): OrientacaoType = when {
            idadeMeses <= 6 -> UNDER_6M
            idadeMeses in 7..11 -> SEVEN_TO_11M
            else -> TWELVEM_TWO_YEARS_PLUS
        }
    }
}
