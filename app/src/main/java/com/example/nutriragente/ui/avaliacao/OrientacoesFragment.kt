package com.example.nutriragente.ui.avaliacao

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nutriragente.R
import com.example.nutriragente.data.model.OrientacaoType
import com.example.nutriragente.data.repository.OrientacoesContentProvider
import com.example.nutriragente.databinding.ActivityOrientacoesBinding
import com.example.nutriragente.util.setupEdgeToEdge

/**
 * Tela de Orientações: mostra um grid de tópicos de orientação nutricional
 * de acordo com a faixa etária da criança avaliada (0-6m, 6-23m ou 2 anos+).
 *
 * Recebe o argumento OrientacaoType (nome do enum [OrientacaoType]) via Navigation
 * Component, geralmente encaminhado pela ResultadosFragment.
 */
class OrientacoesFragment : Fragment(R.layout.activity_orientacoes) {

    private var _binding: ActivityOrientacoesBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ActivityOrientacoesBinding.bind(view)

        setupEdgeToEdge(view)


        val orientacaoType = parseFormType(arguments?.getString("ORIENTACAO_TYPE"))

        setupToolbar()
        setupOrientacoes(orientacaoType)
    }

    private fun parseFormType(raw: String?): OrientacaoType {
        return try {
            if (raw.isNullOrBlank()) OrientacaoType.UNDER_6M else OrientacaoType.valueOf(raw)
        } catch (e: IllegalArgumentException) {
            OrientacaoType.UNDER_6M
        }
    }

    private fun setupToolbar() {
        binding.toolbarOrientacoes.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupOrientacoes(formType: OrientacaoType) {
        binding.tvOrientacoesSubtitulo.text =
            "Orientações para ${OrientacoesContentProvider.subtituloPara(formType)}"

        val topicos = OrientacoesContentProvider.topicosPara(formType)

        binding.rvOrientacoes.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvOrientacoes.adapter = OrientacoesAdapter(topicos) { topic ->
            OrientacaoDetalheBottomSheet(topic)
                .show(childFragmentManager, OrientacaoDetalheBottomSheet.TAG)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
