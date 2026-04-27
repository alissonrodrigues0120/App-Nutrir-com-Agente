package com.example.nutriragente.ui.avaliacao

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nutriragente.R
import com.example.nutriragente.data.model.Crianca
import com.example.nutriragente.data.repository.FormRepository
import com.example.nutriragente.databinding.ScreenResultadosBinding
import com.example.nutriragente.util.setupEdgeToEdge
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ResultadosFragment : Fragment(R.layout.screen_resultados) {

    private var _binding: ScreenResultadosBinding? = null
    private val binding get() = _binding!!
    private lateinit var formRepository: FormRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ScreenResultadosBinding.bind(view)
        
        // Corrige o alinhamento da tela (status bar e navigation bar)
        setupEdgeToEdge(view)

        val crianca = arguments?.getSerializable("CRIANCA") as? Crianca
        val userId = arguments?.getString("USER_ID") ?: ""
        val childId = arguments?.getString("CHILD_ID") ?: ""
        val formType = arguments?.getString("FORM_TYPE") ?: ""

        formRepository = FormRepository(userId, childId, formType)

        setupToolbar()
        
        if (crianca != null) {
            exibirDadosAvaliacao(crianca)
            carregarDadosFormulario(formType)
        }

        binding.btnGoToOrientations.setOnClickListener {
            findNavController().navigate(R.id.action_resultados_to_orientacoes)
        }
    }

    private fun setupToolbar() {
        binding.toolbarResultados.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun exibirDadosAvaliacao(crianca: Crianca) {
        binding.tvChildNameValue.text = crianca.nome
        binding.tvChildAgeValue.text = "${crianca.idadeMeses} meses"
        binding.tvPhysicalInfo.text = "${crianca.peso}kg | ${crianca.altura}m"
        binding.tvNutritionalStatus.text = crianca.statusNutricional

        when {
            crianca.statusNutricional.contains("Sobrepeso", true) || 
            crianca.statusNutricional.contains("Obesidade", true) -> {
                binding.cardStatusResult.setCardBackgroundColor(requireContext().getColor(R.color.yellow_alert))
                binding.tvStatusTitle.text = "Atenção!"
                binding.tvStatusDescription.text = "Cuidado! Sua criança está acima do peso adequado para a idade."
                binding.ivDoctorResult.setImageResource(R.drawable.enfermeira_3)
            }
            crianca.statusNutricional.contains("Magreza", true) -> {
                binding.cardStatusResult.setCardBackgroundColor(requireContext().getColor(R.color.pink_alert))
                binding.tvStatusTitle.text = "Atenção!"
                binding.tvStatusDescription.text = "Cuidado! Sua criança está abaixo do peso adequado para a idade."
                binding.ivDoctorResult.setImageResource(R.drawable.enfermeira_3)
            }
            else -> {
                binding.cardStatusResult.setCardBackgroundColor(requireContext().getColor(R.color.teal_700))
                binding.tvStatusTitle.text = "Tudo certo!"
                binding.tvStatusDescription.text = "Sua criança está com o peso adequado."
                binding.ivDoctorResult.setImageResource(R.drawable.enfermeira_1)
            }
        }
    }

    private fun carregarDadosFormulario(formType: String) {
        lifecycleScope.launch {
            val form = formRepository.getFormByType(formType).firstOrNull()
            if (form != null) {
                setupFormRecyclerView(form.responses)
            }
        }
    }

    private fun setupFormRecyclerView(responses: Map<String, String>) {
        val adapter = FormResumoAdapter(responses.toList())
        binding.rvFormResponses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFormResponses.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
