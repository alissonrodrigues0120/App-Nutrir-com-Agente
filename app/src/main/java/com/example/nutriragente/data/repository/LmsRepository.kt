package com.example.nutriragente.data.repository

import android.content.Context
import com.example.nutriragente.R
import com.example.nutriragente.ui.home.GraphHistory.LMS
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.math.ln
import kotlin.math.pow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Carrega e fornece as tabelas LMS (OMS) para cálculo de Z-Score de IMC.
 *
 * Estratégia de performance:
 * - O JSON é lido em Dispatchers.IO no momento em que o singleton é criado
 *   (logo na inicialização do app, via NutrirApplication).
 * - Quando o ACS chega na tela de avaliação (~segundos depois), as tabelas
 *   já estão prontas — sem bloqueio na main thread.
 * - Antes: mapOf(...) estático bloqueava a JVM ao carregar a classe no
 *   primeiro acesso; Agora: leitura feita em background, zero impacto na UI.
 */
@Singleton
class LmsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Serialização ─────────────────────────────────────────────────────────
    @Serializable
    private data class LmsEntry(val age: Int, val L: Double, val M: Double, val S: Double)

    @Serializable
    private data class LmsData(val M: List<LmsEntry>, val F: List<LmsEntry>)

    private val json = Json { ignoreUnknownKeys = true }

    // Carregamento assíncrono ───────────────────────────────────────────────
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Deferred iniciado imediatamente na criação do singleton.
     * O await() em calcularEscoreZ é instantâneo se as tabelas já foram carregadas.
     */
    private val tablesDeferred: Deferred<Pair<Map<Int, LMS>, Map<Int, LMS>>> =
        scope.async { loadFromJson() }

    // API pública ───────────────────────────────────────────────────────────

    /**
     * Calcula o Z-Score de IMC usando o método LMS da OMS.
     * Suspend: aguarda o carregamento do JSON se ainda não estiver pronto
     * (na prática, estará pronto antes do usuário chegar nesta tela).
     */
    suspend fun calcularEscoreZ(imc: Double, idadeMeses: Int, sexo: String): Double {
        val lms = getLMS(idadeMeses, sexo)
        return if (lms.L != 0.0) {
            ((imc / lms.M).pow(lms.L) - 1) / (lms.L * lms.S)
        } else {
            ln(imc / lms.M) / lms.S
        }
    }

    // Internos ─────────────────────────────────────────────────────────────

    private suspend fun getLMS(idadeMeses: Int, sexo: String): LMS {
        val (boys, girls) = tablesDeferred.await()
        val table = if (sexo.uppercase() == "M") boys else girls
        return interpolate(table, idadeMeses)
    }

    private fun interpolate(table: Map<Int, LMS>, idadeMeses: Int): LMS {
        table[idadeMeses]?.let { return it }

        val keys   = table.keys.sorted()
        val lower  = keys.filter { it <= idadeMeses }.maxOrNull() ?: keys.first()
        val upper  = keys.filter { it >= idadeMeses }.minOrNull() ?: keys.last()

        if (lower == upper) return table[lower]!!

        val ratio    = (idadeMeses - lower).toDouble() / (upper - lower)
        val lowerLMS = table[lower]!!
        val upperLMS = table[upper]!!

        return LMS(
            L = lowerLMS.L + (upperLMS.L - lowerLMS.L) * ratio,
            M = lowerLMS.M + (upperLMS.M - lowerLMS.M) * ratio,
            S = lowerLMS.S + (upperLMS.S - lowerLMS.S) * ratio
        )
    }

    private fun loadFromJson(): Pair<Map<Int, LMS>, Map<Int, LMS>> {
        val raw  = context.resources.openRawResource(R.raw.lms_tables)
            .bufferedReader().use { it.readText() }
        val data = json.decodeFromString<LmsData>(raw)
        val boys = data.M.associate { it.age to LMS(it.L, it.M, it.S) }
        val girls = data.F.associate { it.age to LMS(it.L, it.M, it.S) }
        return Pair(boys, girls)
    }
}
