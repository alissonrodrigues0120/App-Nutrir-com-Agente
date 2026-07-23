package com.example.nutriragente.ui.avaliacao

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.nutriragente.R
import com.example.nutriragente.data.model.OrientacaoTopic
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Exibe o conteúdo detalhado de um tópico de orientação (ex.: "Técnica de
 * amamentação") ao tocar em um dos cards da tela de Orientações.
 */
class OrientacaoDetalheBottomSheet(
    private val topic: OrientacaoTopic
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottom_sheet_orientacao, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.iv_detalhe_icon).setImageResource(topic.iconRes)
        view.findViewById<TextView>(R.id.tv_detalhe_title).text = topic.title

        val container = view.findViewById<ViewGroup>(R.id.container_detalhe_bullets)
        val inflater = LayoutInflater.from(requireContext())

        topic.bullets.forEach { bullet ->
            val bulletView = inflater.inflate(R.layout.item_orientacao_bullet, container, false)
            bulletView.findViewById<TextView>(R.id.tv_bullet_text).text = bullet
            container.addView(bulletView)
        }
    }

    companion object {
        const val TAG = "OrientacaoDetalheBottomSheet"
    }
}
