package com.example.nutriragente.ui.home

import android.util.Log
import kotlin.math.pow
import kotlin.math.ln

object GraphHistory {

    // Tabela LMS de IMC para Idade (OMS) - Valores reais de BMI-for-Age
    data class LMS(val L: Double, val M: Double, val S: Double)

    // Meninos - IMC para Idade (0-60 meses)
    private val boysBMI = mapOf(
        0 to LMS(1.0, 13.3, 0.065),
        1 to LMS(1.0, 14.5, 0.068),
        2 to LMS(1.0, 15.2, 0.070),
        3 to LMS(1.0, 15.7, 0.072),
        4 to LMS(1.0, 16.1, 0.074),
        5 to LMS(1.0, 16.4, 0.076),
        6 to LMS(0.95, 16.6, 0.078),
        7 to LMS(0.90, 16.7, 0.079),
        8 to LMS(0.85, 16.8, 0.080),
        9 to LMS(0.80, 16.9, 0.081),
        10 to LMS(0.75, 17.0, 0.081),
        11 to LMS(0.70, 17.0, 0.082),
        12 to LMS(0.65, 17.0, 0.082),
        13 to LMS(0.60, 16.9, 0.083),
        14 to LMS(0.58, 16.8, 0.083),
        15 to LMS(0.56, 16.7, 0.084),
        16 to LMS(0.54, 16.6, 0.084),
        17 to LMS(0.53, 16.6, 0.085),
        18 to LMS(0.52, 16.5, 0.085),
        19 to LMS(0.52, 16.5, 0.085),
        20 to LMS(0.52, 16.5, 0.085),
        21 to LMS(0.52, 16.5, 0.085),
        22 to LMS(0.52, 16.5, 0.085),
        23 to LMS(0.52, 16.5, 0.085),
        24 to LMS(0.5183, 16.5, 0.085),
        25 to LMS(0.51, 16.5, 0.085),
        26 to LMS(0.50, 16.5, 0.085),
        27 to LMS(0.48, 16.4, 0.086),
        28 to LMS(0.46, 16.4, 0.086),
        29 to LMS(0.44, 16.4, 0.086),
        30 to LMS(0.42, 16.3, 0.087),
        31 to LMS(0.40, 16.3, 0.087),
        32 to LMS(0.38, 16.3, 0.087),
        33 to LMS(0.36, 16.2, 0.088),
        34 to LMS(0.34, 16.2, 0.088),
        35 to LMS(0.32, 16.2, 0.088),
        36 to LMS(0.1918, 16.0, 0.090),
        42 to LMS(0.10, 15.8, 0.091),
        48 to LMS(-0.15, 15.7, 0.093),
        54 to LMS(-0.35, 15.6, 0.094),
        60 to LMS(-0.45, 15.5, 0.095)
    )

    // Meninas - IMC para Idade (0-60 meses)
    private val girlsBMI = mapOf(
        0 to LMS(1.0, 13.1, 0.065),
        1 to LMS(1.0, 14.3, 0.068),
        2 to LMS(1.0, 15.0, 0.070),
        3 to LMS(1.0, 15.5, 0.072),
        4 to LMS(1.0, 15.9, 0.074),
        5 to LMS(1.0, 16.2, 0.076),
        6 to LMS(0.95, 16.4, 0.078),
        7 to LMS(0.90, 16.5, 0.079),
        8 to LMS(0.85, 16.6, 0.080),
        9 to LMS(0.80, 16.7, 0.081),
        10 to LMS(0.75, 16.8, 0.081),
        11 to LMS(0.70, 16.8, 0.082),
        12 to LMS(0.65, 16.8, 0.082),
        13 to LMS(0.60, 16.7, 0.083),
        14 to LMS(0.58, 16.6, 0.083),
        15 to LMS(0.56, 16.5, 0.084),
        16 to LMS(0.54, 16.5, 0.084),
        17 to LMS(0.53, 16.4, 0.085),
        18 to LMS(0.52, 16.4, 0.085),
        19 to LMS(0.52, 16.4, 0.085),
        20 to LMS(0.52, 16.3, 0.085),
        21 to LMS(0.52, 16.3, 0.085),
        22 to LMS(0.52, 16.3, 0.085),
        23 to LMS(0.52, 16.3, 0.085),
        24 to LMS(0.3646, 16.3, 0.085),
        25 to LMS(0.35, 16.3, 0.085),
        26 to LMS(0.34, 16.3, 0.085),
        27 to LMS(0.32, 16.2, 0.086),
        28 to LMS(0.30, 16.2, 0.086),
        29 to LMS(0.28, 16.2, 0.086),
        30 to LMS(0.26, 16.1, 0.087),
        31 to LMS(0.24, 16.1, 0.087),
        32 to LMS(0.22, 16.1, 0.087),
        33 to LMS(0.20, 16.0, 0.088),
        34 to LMS(0.18, 16.0, 0.088),
        35 to LMS(0.16, 16.0, 0.088),
        36 to LMS(0.05, 15.8, 0.090),
        42 to LMS(-0.10, 15.6, 0.091),
        48 to LMS(-0.25, 15.5, 0.093),
        54 to LMS(-0.40, 15.4, 0.094),
        60 to LMS(-0.55, 15.3, 0.095)
    )

    /**
     * Calcula IMC simples (peso / altura²)
     */
    fun calcularIMC(pesoKg: Double, alturaM: Double): Double {
        if (alturaM <= 0) return 0.0
        return pesoKg / (alturaM.pow(2))
    }

    /**
     * Busca parâmetros LMS para idade e sexo
     */
    private fun getLMS(idadeMeses: Int, sexo: String): LMS {
        val table = if (sexo.uppercase() == "M") boysBMI else girlsBMI

        // Busca exata
        table[idadeMeses]?.let { return it }

        // Busca os valores mais próximos para interpolação
        val keys = table.keys.sorted()
        val lower = keys.filter { it <= idadeMeses }.maxOrNull() ?: keys.first()
        val upper = keys.filter { it >= idadeMeses }.minOrNull() ?: keys.last()

        if (lower == upper) return table[lower]!!

        // Interpolação linear
        val ratio = (idadeMeses - lower).toDouble() / (upper - lower)
        val lowerLMS = table[lower]!!
        val upperLMS = table[upper]!!

        return LMS(
            L = lowerLMS.L + (upperLMS.L - lowerLMS.L) * ratio,
            M = lowerLMS.M + (upperLMS.M - lowerLMS.M) * ratio,
            S = lowerLMS.S + (upperLMS.S - lowerLMS.S) * ratio
        )
    }

    /**
     * Calcula Z-Score usando método LMS da OMS
     * Compatível com o nome usado no EvaluationViewModel
     */
    fun calcularEscoreZ(imc: Double, idadeMeses: Int, sexo: String): Double {
        val lms = getLMS(idadeMeses, sexo)

        Log.d("IMC_DEBUG", "Idade: $idadeMeses meses, Sexo: $sexo")
        Log.d("IMC_DEBUG", "L=${lms.L}, M=${lms.M}, S=${lms.S}")
        Log.d("IMC_DEBUG", "IMC Calculado: ${String.format("%.2f", imc)}")

        return if (lms.L != 0.0) {
            ((imc / lms.M).pow(lms.L) - 1) / (lms.L * lms.S)
        } else {
            ln(imc / lms.M) / lms.S
        }
    }

    /**
     * Classificação baseada no Z-Score (OMS - crianças até 5 anos)
     * Compatível com o parâmetro idadeMeses do ViewModel
     */
    fun getClassificacao(zScore: Double, idadeMeses: Int = 0): String {
        return when {
            zScore >= 3.0  -> "Obesidade Grave"
            zScore >= 2.0  -> "Obesidade"
            zScore >= 1.0  -> "Sobrepeso"
            zScore >= -2.0 -> "Peso Adequado"
            zScore >= -3.0 -> "Magreza"
            else           -> "Magreza Acentuada"
        }
    }

    /**
     * Método completo - calcula tudo de uma vez
     */
    fun calcularIMCCompleto(
        pesoKg: Double,
        alturaM: Double,
        idadeMeses: Int,
        sexo: String
    ): ResultadoIMC {
        val imc = calcularIMC(pesoKg, alturaM)
        val zScore = calcularEscoreZ(imc, idadeMeses, sexo)
        val classificacao = getClassificacao(zScore, idadeMeses)

        return ResultadoIMC(
            imc = imc,
            zScore = zScore,
            classificacao = classificacao,
            idadeMeses = idadeMeses,
            sexo = sexo
        )
    }

    data class ResultadoIMC(
        val imc: Double,
        val zScore: Double,
        val classificacao: String,
        val idadeMeses: Int,
        val sexo: String
    )
}