package com.example.nutriragente.ui.avaliacao

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nutriragente.R
import com.example.nutriragente.databinding.FragmentNewEvaluationStepBinding
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Calendar

/**
 * Wizard de 3 etapas para coleta dos dados básicos da criança.
 *
 * Etapa 1 — Identificação: Nome · Data de nascimento · Sexo
 * Etapa 2 — Medidas: Peso (kg) · Altura (cm) · IMC estimado ao vivo
 * Etapa 3 — Aleitamento: seleção visual do tipo de aleitamento materno
 *
 * A navegação entre etapas usa crossfade (150 ms saída + 200 ms entrada).
 * Validação ocorre ao avançar: campos obrigatórios são verificados antes
 * de exibir a próxima etapa.
 */
@AndroidEntryPoint
class NewEvaluationFragment : Fragment(R.layout.fragment_new_evaluation_step) {

    private val viewModel: EvaluationViewModel by viewModels()

    private var _binding: FragmentNewEvaluationStepBinding? = null
    private val binding get() = _binding!!

    // ── Estado do wizard ────────────────────────────────────────────────────
    private var currentStep = 1
    private val totalSteps  = 3

    private var sexoSelecionado: String = ""          // "M" ou "F"
    private var tipoAmSelecionado: String = ""        // "Exclusivo", "Materno", etc.

    private val aleitamentoOpcoes = listOf(
        "Aleitamento Materno Exclusivo"   to "Exclusivo",
        "Aleitamento Materno Predominante" to "Predominante",
        "Aleitamento Materno"             to "Materno",
        "Aleitamento Materno Complementado" to "Complementado",
        "Aleitamento Misto ou Parcial"    to "Misto",
        "Aleitamento Artificial"          to "Artificial"
    )

    // ════════════════════════════════════════════════════════════════════════
    // Ciclo de vida
    // ════════════════════════════════════════════════════════════════════════

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNewEvaluationStepBinding.bind(view)

        binding.toolbarEval.setNavigationOnClickListener { findNavController().navigateUp() }

        setupDatePicker()
        setupSexoButtons()
        buildAleitamentoButtons()
        setupImcPreview()
        setupNavButtons()
        observeViewModel()
        renderStep(animate = false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ════════════════════════════════════════════════════════════════════════
    // Setup de componentes
    // ════════════════════════════════════════════════════════════════════════

    private fun setupDatePicker() {
        binding.etDataNascimento.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, ano, mes, dia ->
                    binding.etDataNascimento.setText(
                        String.format("%02d/%02d/%04d", dia, mes + 1, ano)
                    )
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupSexoButtons() {
        fun select(escolhido: MaterialButton, outro: MaterialButton, valor: String) {
            val blue = ContextCompat.getColor(requireContext(), R.color.blue_toolbar)
            escolhido.backgroundTintList = ColorStateList.valueOf(blue)
            escolhido.setTextColor(Color.WHITE)
            escolhido.strokeWidth = 0
            outro.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            outro.setTextColor(blue)
            outro.strokeWidth = 2.dp
            outro.strokeColor = ColorStateList.valueOf(blue)
            sexoSelecionado = valor
        }

        binding.btnFeminino.setOnClickListener {
            select(binding.btnFeminino, binding.btnMasculino, "F")
        }
        binding.btnMasculino.setOnClickListener {
            select(binding.btnMasculino, binding.btnFeminino, "M")
        }
    }

    private fun buildAleitamentoButtons() {
        val container = binding.containerAleitamento
        val blue = ContextCompat.getColor(requireContext(), R.color.blue_toolbar)

        aleitamentoOpcoes.forEachIndexed { index, (label, valor) ->
            val btn = MaterialButton(requireContext()).apply {
                text = label
                textSize = 14f
                setPadding(16.dp, 0, 16.dp, 0)
                cornerRadius = 12.dp
                insetTop = 0; insetBottom = 0
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    52.dp
                ).apply { bottomMargin = 10.dp }
                // Estilo não-selecionado
                backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                setTextColor(blue)
                strokeWidth = 2.dp
                strokeColor = ColorStateList.valueOf(blue)
            }
            btn.setOnClickListener {
                // Deseleciona todos
                for (i in 0 until container.childCount) {
                    val b = container.getChildAt(i) as? MaterialButton ?: continue
                    b.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                    b.setTextColor(blue)
                    b.strokeWidth = 2.dp
                }
                // Seleciona o escolhido
                btn.backgroundTintList = ColorStateList.valueOf(blue)
                btn.setTextColor(Color.WHITE)
                btn.strokeWidth = 0
                tipoAmSelecionado = valor
            }
            container.addView(btn)
        }
    }

    private fun setupImcPreview() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) = updateImcPreview()
        }
        binding.etPeso.addTextChangedListener(watcher)
        binding.etAltura.addTextChangedListener(watcher)
    }

    private fun updateImcPreview() {
        val peso    = binding.etPeso.text.toString().toDoubleOrNull()
        val alturaCm = binding.etAltura.text.toString().toDoubleOrNull()
        if (peso != null && alturaCm != null && alturaCm > 0) {
            val alturaM = alturaCm / 100.0
            val imc = peso / (alturaM * alturaM)
            binding.tvImcPreview.text = "%.1f kg/m²".format(imc)
            binding.cardImcPreview.visibility = View.VISIBLE
        } else {
            binding.cardImcPreview.visibility = View.GONE
        }
    }

    private fun setupNavButtons() {
        binding.evalBtnPrevious.setOnClickListener {
            if (currentStep > 1) {
                currentStep--
                renderStep(animate = true, forward = false)
            }
        }
        binding.evalBtnNext.setOnClickListener {
            if (currentStep < totalSteps) {
                if (!validateCurrentStep()) return@setOnClickListener
                currentStep++
                renderStep(animate = true, forward = true)
            } else {
                submitForm()
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Renderização e animação
    // ════════════════════════════════════════════════════════════════════════

    private fun renderStep(animate: Boolean = true, forward: Boolean = true) {
        updateProgress()
        if (animate) {
            binding.evalScroll.animate()
                .alpha(0f).setDuration(150)
                .withEndAction {
                    showStep(currentStep)
                    binding.evalScroll.animate().alpha(1f).setDuration(200).start()
                }.start()
        } else {
            showStep(currentStep)
        }
    }

    private fun showStep(step: Int) {
        binding.cardStep1.visibility = if (step == 1) View.VISIBLE else View.GONE
        binding.cardStep2.visibility = if (step == 2) View.VISIBLE else View.GONE
        binding.cardStep3.visibility = if (step == 3) View.VISIBLE else View.GONE
        binding.evalScroll.scrollTo(0, 0)
    }

    private fun updateProgress() {
        binding.tvEvalStepCounter.text = "$currentStep de $totalSteps"
        binding.evalProgressBar.max = totalSteps
        binding.evalProgressBar.setProgressCompat(currentStep, true)
        binding.evalBtnPrevious.isVisible = currentStep > 1
        binding.evalBtnNext.text = if (currentStep == totalSteps) "Confirmar ✓" else "Próxima →"
    }

    // ════════════════════════════════════════════════════════════════════════
    // Validação e envio
    // ════════════════════════════════════════════════════════════════════════

    private fun validateCurrentStep(): Boolean {
        return when (currentStep) {
            1 -> {
                when {
                    binding.etNome.text.isNullOrBlank() -> {
                        toast("Informe o nome da criança"); false
                    }
                    binding.etDataNascimento.text.isNullOrBlank() -> {
                        toast("Informe a data de nascimento"); false
                    }
                    sexoSelecionado.isEmpty() -> {
                        toast("Selecione o sexo da criança"); false
                    }
                    else -> true
                }
            }
            2 -> {
                val pesoOk  = binding.etPeso.text.toString().toDoubleOrNull() != null
                val altOk   = binding.etAltura.text.toString().toDoubleOrNull() != null
                if (!pesoOk || !altOk) {
                    toast("Informe peso e altura válidos")
                    false
                } else true
            }
            else -> true
        }
    }

    private fun submitForm() {
        val nome     = binding.etNome.text.toString().trim()
        val dataStr  = binding.etDataNascimento.text.toString().trim()
        val pesoStr  = binding.etPeso.text.toString().trim()
        val altStr   = binding.etAltura.text.toString().trim()

        try {
            val peso        = pesoStr.toDouble()
            val alturaCm    = altStr.toDouble()
            val idadeMeses  = calcularIdadeEmMeses(dataStr)
            val tipoAm      = tipoAmSelecionado.ifEmpty { "Não informado" }

            viewModel.salvarAvaliacao(
                nome          = nome,
                peso          = peso,
                alturaCm      = alturaCm,
                idadeMeses    = idadeMeses,
                sexo          = sexoSelecionado,
                tipoAm        = tipoAm,
                dataNascimento = dataStr
            )
        } catch (e: Exception) {
            toast("Verifique os dados informados")
        }
    }

    private fun observeViewModel() {
        viewModel.navegacaoEvent.observe(viewLifecycleOwner) { evento ->
            evento?.let { (destino, bundle) ->
                findNavController().navigate(destino, bundle)
                viewModel.resetNavegacao()
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Helpers
    // ════════════════════════════════════════════════════════════════════════

    private fun calcularIdadeEmMeses(dataNascimento: String): Int {
        val formatter  = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val nascimento = LocalDate.parse(dataNascimento, formatter)
        val periodo    = Period.between(nascimento, LocalDate.now())
        return (periodo.years * 12) + periodo.months
    }

    private fun toast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

    private val Int.dp: Int
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics
        ).toInt()
}
