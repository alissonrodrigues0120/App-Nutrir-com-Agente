package com.example.nutriragente.ui.avaliacao

import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.nutriragente.R
import com.example.nutriragente.databinding.NewEvaluationsixmonthBinding
import com.example.nutriragente.data.model.FormType
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.time.LocalDate

class FormsFragment_sixmonth : AppCompatActivity() {

    private var _binding: NewEvaluationsixmonthBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: FormViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = NewEvaluationsixmonthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra(ARG_USER_ID) ?: ""
        val childId = intent.getStringExtra(ARG_CHILD_ID) ?: ""
        val birthDateStr = intent.getStringExtra(ARG_BIRTH_DATE) ?: ""
        val birthDate = if (birthDateStr.isNotEmpty()) LocalDate.parse(birthDateStr) else LocalDate.now()

        // Setup ViewModel
        val factory = FormViewModelFactory(userId, childId, FormType.UNDER_6M, birthDate)
        viewModel = ViewModelProvider(this, factory)[FormViewModel::class.java]

        setupToolbar()
        setupRadioGroups()
        setupFab()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupRadioGroups() {
        binding.rgLeitePeito.setOnCheckedChangeListener { _, checkedId ->
            val answer = when (checkedId) {
                R.id.rb_leite_peito_sim -> "Sim"
                R.id.rb_leite_peito_nao -> "Não"
                R.id.rb_leite_peito_nao_sabe -> "Não Sabe"
                else -> return@setOnCheckedChangeListener
            }
            viewModel.updateAnswer("leite_peito", answer)
        }

        binding.rgMingau.setOnCheckedChangeListener { _, checkedId -> viewModel.updateAnswer("mingau", getRadioAnswer(checkedId)) }
        binding.rgAguaCha.setOnCheckedChangeListener { _, checkedId -> viewModel.updateAnswer("agua_cha", getRadioAnswer(checkedId)) }
        binding.rgLeiteVaca.setOnCheckedChangeListener { _, checkedId -> viewModel.updateAnswer("leite_vaca", getRadioAnswer(checkedId)) }
        binding.rgFormulaInfantil.setOnCheckedChangeListener { _, checkedId -> viewModel.updateAnswer("formula_infantil", getRadioAnswer(checkedId)) }
        binding.rgSucoFruta.setOnCheckedChangeListener { _, checkedId -> viewModel.updateAnswer("suco_fruta", getRadioAnswer(checkedId)) }
        binding.rgFruta.setOnCheckedChangeListener { _, checkedId -> viewModel.updateAnswer("fruta", getRadioAnswer(checkedId)) }
        binding.rgComidaSal.setOnCheckedChangeListener { _, checkedId -> viewModel.updateAnswer("comida_sal", getRadioAnswer(checkedId)) }
        binding.rgOutrosAlimentos.setOnCheckedChangeListener { _, checkedId -> viewModel.updateAnswer("outros_alimentos", getRadioAnswer(checkedId)) }
    }

    private fun getRadioAnswer(checkedId: Int): String {
        return when (checkedId) {
            R.id.rb_leite_peito_sim, R.id.rb_mingau_sim, R.id.rb_agua_cha_sim,
            R.id.rb_leite_vaca_sim, R.id.rb_formula_infantil_sim, R.id.rb_suco_fruta_sim,
            R.id.rb_fruta_sim, R.id.rb_comida_sal_sim, R.id.rb_outros_alimentos_sim -> "Sim"

            R.id.rb_leite_peito_nao, R.id.rb_mingau_nao, R.id.rb_agua_cha_nao,
            R.id.rb_leite_vaca_nao, R.id.rb_formula_infantil_nao, R.id.rb_suco_fruta_nao,
            R.id.rb_fruta_nao, R.id.rb_comida_sal_nao, R.id.rb_outros_alimentos_nao -> "Não"

            else -> "Não Sabe"
        }
    }

    private fun setupFab() {
        binding.fabConfirm.setOnClickListener {
            viewModel.saveForm()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is FormUiState.Ready -> restoreAnswers(state.responses)
                        is FormUiState.Saved -> {
                            Snackbar.make(binding.root, "Formulário salvo com sucesso!", Snackbar.LENGTH_SHORT).show()
                            finish()
                        }
                        is FormUiState.Error -> Toast.makeText(this@FormsFragment_sixmonth, "Erro: ${state.message}", Toast.LENGTH_LONG).show()
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun restoreAnswers(savedAnswers: Map<String, String>) {
        savedAnswers["leite_peito"]?.let { checkRadio(binding.rgLeitePeito, it) }
        savedAnswers["mingau"]?.let { checkRadio(binding.rgMingau, it) }
        savedAnswers["agua_cha"]?.let { checkRadio(binding.rgAguaCha, it) }
        savedAnswers["leite_vaca"]?.let { checkRadio(binding.rgLeiteVaca, it) }
        savedAnswers["formula_infantil"]?.let { checkRadio(binding.rgFormulaInfantil, it) }
        savedAnswers["suco_fruta"]?.let { checkRadio(binding.rgSucoFruta, it) }
        savedAnswers["fruta"]?.let { checkRadio(binding.rgFruta, it) }
        savedAnswers["comida_sal"]?.let { checkRadio(binding.rgComidaSal, it) }
        savedAnswers["outros_alimentos"]?.let { checkRadio(binding.rgOutrosAlimentos, it) }
    }

    private fun checkRadio(radioGroup: RadioGroup, answer: String) {
        val checkedId = when (answer) {
            "Sim" -> radioGroup.getChildAt(0).id
            "Não" -> radioGroup.getChildAt(1).id
            else -> radioGroup.getChildAt(2).id
        }
        radioGroup.check(checkedId)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val ARG_USER_ID = "USER_ID"
        const val ARG_CHILD_ID = "CHILD_ID"
        const val ARG_BIRTH_DATE = "BIRTH_DATE"
    }
}
