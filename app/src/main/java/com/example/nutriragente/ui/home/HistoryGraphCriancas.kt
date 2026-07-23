package com.example.nutriragente.ui.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint 
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.example.nutriragente.data.model.GraphHistory

class HistoryGraphCriancas @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var historico: List<GraphHistory> = emptyList()

    // Configuração dos "pincéis" para desenhar na tela
    private val linePaint = Paint().apply {
        color = Color.parseColor("#4CAF50") // Cor principal (Ex: Verde)
        strokeWidth = 8f
        style = Paint.Style.STROKE
        isAntiAlias = true
        strokeJoin = Paint.Join.ROUND
    }

    private val circlePaint = Paint().apply {
        color = Color.parseColor("#4CAF50")
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val innerCirclePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    // Método para atualizar os dados do gráfico vindos do ViewModel
    fun setDataset(novosDados: List<GraphHistory>) {
        this.historico = novosDados
        invalidate() // Força a View a se redesenhar na tela
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (historico.size < 2) return

        val padding = 50f
        val availableWidth = width - (padding * 2)
        val availableHeight = height - (padding * 2)

        val maxScore = historico.maxOf { it.score }.takeIf { it > 0 } ?: 1f
        val minScore = historico.minOf { it.score }
        val scoreRange = (maxScore - minScore).takeIf { it > 0 } ?: 1f

        val espacamentoX = availableWidth / (historico.size - 1)
        val path = Path()

        // Mapeia os pontos do gráfico
        val pontos = historico.mapIndexed { index, item ->
            val x = padding + (index * espacamentoX)
            // Inverte o eixo Y pois no Android o 0 fica no topo
            val y = padding + availableHeight - ((item.score - minScore) / scoreRange * availableHeight)
            Pair(x, y)
        }

        // Desenha a linha conectando os pontos
        path.moveTo(pontos.first().first, pontos.first().second)
        for (i in 1 until pontos.size) {
            path.lineTo(pontos[i].first, pontos[i].second)
        }
        canvas.drawPath(path, linePaint)

        // Desenha as bolinhas nos pontos
        pontos.forEach { (x, y) ->
            canvas.drawCircle(x, y, 14f, circlePaint)
            canvas.drawCircle(x, y, 7f, innerCirclePaint)
        }
    }
}
