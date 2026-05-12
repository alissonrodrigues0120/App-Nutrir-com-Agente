package com.example.nutriragente.ui.home

import kotlin.math.pow

/**
 * Utilitário de cálculo nutricional — cálculos puros, sem dados estáticos.
 *
 * As tabelas LMS da OMS foram movidas para res/raw/lms_tables.json e são
 * carregadas em background pelo LmsRepository, eliminando o congelamento
 * da main thread que ocorria ao inicializar os mapOf(...) estáticos aqui.
 */
object GraphHistory {

    /** Parâmetros LMS (Box-Cox) da OMS para uma faixa etária/sexo. */
    data class LMS(val L: Double, val M: Double, val S: Double)

    /**
     * Calcula IMC simples: peso / altura².
     * @param pesoKg   Peso em quilogramas
     * @param alturaM  Altura em metros
     */
    fun calcularIMC(pesoKg: Double, alturaM: Double): Double {
        if (alturaM <= 0) return 0.0
        return pesoKg / alturaM.pow(2)
    }

    /**
     * Classificação nutricional baseada no Z-Score (referência OMS, 0-5 anos).
     * O cálculo do Z-Score em si é responsabilidade do LmsRepository,
     * pois depende da tabela LMS carregada do JSON.
     */
    fun getClassificacao(zScore: Double, @Suppress("UNUSED_PARAMETER") idadeMeses: Int = 0): String {
        return when {
            zScore >= 3.0  -> "Obesidade Grave"
            zScore >= 2.0  -> "Obesidade"
            zScore >= 1.0  -> "Sobrepeso"
            zScore >= -2.0 -> "Peso Adequado"
            zScore >= -3.0 -> "Magreza"
            else           -> "Magreza Acentuada"
        }
    }

    /** Resultado completo de uma avaliação nutricional. */
    data class ResultadoIMC(
        val imc: Double,
        val zScore: Double,
        val classificacao: String,
        val idadeMeses: Int,
        val sexo: String
    )
}
