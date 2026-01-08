package com.example.nutriragente.ui.home

import android.content.Context
import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.IOException
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.pow

@Serializable
data class WhoEntry(
    val month: Int,
    val L: Double,
    val M: Double,
    val S: Double

)

@Serializable
data class WhoDataRoot(
    val lfa_boys_zscores_monthly: List<WhoEntry>,   // ← nome exato do JSON
    val lfa_girls_percentiles_monthly: List<WhoEntry> // ← nome exato do JSON
)

/**
 * Serviço adaptado para o JSON da OMS com estrutura:
 * {
 *   "lfa_boys_zscores_monthly": [...],
 *   "lfa_girls_percentiles_monthly": [...]
 * }
 */
object GraphHistory {

    private var _isInitialized = false
    private lateinit var whoData: WhoDataRoot // ← novo nome

    private const val WHO_FILE_NAME = "lfa_who_monthly_data.json" // mantém o mesmo nome do arquivo

    private fun loadJsonFromAssets(context: Context, fileName: String): String {
        return try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            Log.e("GraphHistory", "Erro ao abrir $fileName nos assets", e)
            throw RuntimeException("Erro ao carregar arquivo WHO. Verifique se está em assets.", e)
        }
    }

    fun initialize(context: Context) {
        if (_isInitialized) return

        val jsonString = loadJsonFromAssets(context, WHO_FILE_NAME)

        try {
            // Usa Json configuration compatível com seu arquivo
            val json = Json { ignoreUnknownKeys = true }
            whoData = json.decodeFromString(jsonString)
            _isInitialized = true
            Log.d("GraphHistory", "GraphHistory inicializado com sucesso (estrutura WHO).")
        } catch (e: Exception) {
            Log.e("GraphHistory", "Erro ao ler/desserializar o JSON", e)
            throw RuntimeException("Erro ao converter JSON WHO. Verifique o formato.", e)
        }
    }

    /**
     * Retorna L, M e S para determinada idade em meses e sexo
     * @param idadeMeses Idade em meses (0 a 60+)
     * @param sexo "M" para masculino, "F" para feminino
     */
    private fun getLmsParameters(idadeMeses: Int, sexo: String): Triple<Double, Double, Double> {
        if (!_isInitialized) {
            throw IllegalStateException("GraphHistory não inicializado. Chame initialize(context).")
        }

        val list = when (sexo.uppercase()) {
            "M" -> whoData.lfa_boys_zscores_monthly  // ← usa o array de meninos
            "F" -> whoData.lfa_girls_percentiles_monthly // ← usa o array de meninas
            else -> throw IllegalArgumentException("Sexo inválido: use 'M' ou 'F'")
        }

        // Busca a entrada mais próxima (se não achar exata)
        val entry = list.minByOrNull { abs(it.month - idadeMeses) }
            ?: throw NoSuchElementException("WHO data não encontrado para idade: $idadeMeses meses")

        return Triple(entry.L, entry.M, entry.S)
    }

    // Mantém os métodos de cálculo (mas atenção: os dados são de ALTURA, não IMC!)
    fun calcularIMC(pesoKg: Double, alturaM: Double): Double {
        if (alturaM <= 0) return 0.0
        return pesoKg / (alturaM.pow(2))
    }

    fun calcularEscoreZ(imc: Double, idadeMeses: Int, sexo: String): Double {
        val (L, M, S) = getLmsParameters(idadeMeses, sexo)

        return if (L != 0.0) {
            ((imc / M).pow(L) - 1) / (L * S)
        } else {
            ln(imc / M) / S
        }
    }

    fun getClassificacao(zScore: Double, idadeMeses: Int): String {
        return if (idadeMeses <= 60) {
            when {
                zScore >= 3.0  -> "Obesidade Grave"
                zScore >= 2.0  -> "Obesidade"
                zScore >= 1.0  -> "Sobrepeso"
                zScore >= -2.0 -> "Peso Adequado"
                zScore >= -3.0 -> "Magreza"
                else           -> "Magreza Acentuada"
            }
        } else {
            when {
                zScore > 2.0   -> "Obesidade"
                zScore > 1.0   -> "Sobrepeso"
                zScore >= -2.0 -> "Peso Adequado"
                else           -> "Magreza"
            }
        }
    }
}
