package com.example.nutriragente.ui.avaliacao

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.nutriragente.R
import com.example.nutriragente.databinding.NewEvaluationsixtotwentythreeBinding
import com.example.nutriragente.data.model.FormType
import kotlinx.coroutines.launch
import java.time.LocalDate


class FormsFragment_sixtotwentythree : Fragment(R.layout.new_evaluationsixtotwentythree) {

    private var _binding: NewEvaluationsixtotwentythreeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FormViewModel
    private var userId = ""
    private var childId = ""
    private var birthDate = LocalDate.now()


    companion object {
        fun newInstance(userId: String, childId: String, birthDate: String) =
            FormsFragment_sixtotwentythree().apply {
                arguments = Bundle().apply {
                    putString("USER_ID", userId)
                    putString("CHILD_ID", childId)
                    putString("BIRTH_DATE", birthDate)
                }
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = NewEvaluationsixtotwentythreeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            userId = it.getString("USER_ID", "")
            childId = it.getString("CHILD_ID", "")
            val dateStr = it.getString("BIRTH_DATE", "")
            birthDate = if (dateStr.isNotEmpty()) LocalDate.parse(dateStr) else LocalDate.now()
        }

        val factory = FormViewModelFactory(userId, childId, FormType.SIX_TO_23M, birthDate)
        viewModel = ViewModelProvider(this, factory)[FormViewModel::class.java]

        setupToolbar()
        setupComplexLogic() // Lógica condicional aqui
        setupSimpleRadios()
        setupFab()
        observeState()
    }

    private fun setupComplexLogic() {
        // Pergunta Fruta -> Controla visibilidade de "Quantas vezes"
        binding.rgFruta.setOnCheckedChangeListener { _, checkedId ->
            val isSim = checkedId == R.id.rb_fruta_sim
            // O XML não tem ID no LinearLayout pai, vamos assumir que você adicionou id="ll_fruta_quantas"
            // Ou usamos binding direto se o layout tiver IDs nos grupos
            binding.rgFrutaVezes.visibility = if (isSim) View.VISIBLE else View.GONE

            viewModel.updateAnswer("fruta", if (isSim) "Sim" else (if(checkedId == R.id.rb_fruta_nao) "Não" else "Não Sabe"))
        }

        binding.rgFrutaVezes.setOnCheckedChangeListener { _, id ->
            val valStr = when(id) {
                R.id.rb_fruta_1vez -> "1 vez"
                R.id.rb_fruta_2vezes -> "2 vezes"
                R.id.rb_fruta_3oumais -> "3 vezes ou mais"
                else -> "Não Sabe"
            }
            viewModel.updateAnswer("fruta_vezes", valStr)
        }

        // Pergunta Comida de Sal -> Controla visibilidade de "Quantas vezes" e "Como oferecida"
        binding.rgComidaSal.setOnCheckedChangeListener { _, checkedId ->
            val isSim = checkedId == R.id.rb_comida_sal_sim
            binding.rgComidaSalVezes.visibility = if (isSim) View.VISIBLE else View.GONE
            binding.rgComidaOferecida.visibility = if (isSim) View.VISIBLE else View.GONE
            viewModel.updateAnswer("comida_sal", if(isSim) "Sim" else "Não") // Simplificado
        }

        binding.rgComidaSalVezes.setOnCheckedChangeListener { _, id ->
            // Lógica similar para comida_sal_vezes
            viewModel.updateAnswer("comida_sal_vezes", "valor_lido")
        }
    }

    private fun setupSimpleRadios() {
        // Configurar os outros RadioGroups (leite_peito, outro_leite, mingau, etc)
        // Mesma lógica do Fragment 1
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
    }

    private fun setupFab() {
        binding.fabConfirm.setOnClickListener { viewModel.saveForm() }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                if (state is FormUiState.Saved) {
                    Toast.makeText(requireContext(), "Salvo!", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}