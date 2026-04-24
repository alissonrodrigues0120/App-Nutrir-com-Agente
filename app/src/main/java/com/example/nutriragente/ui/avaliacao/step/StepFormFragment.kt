package com.example.nutriragente.ui.avaliacao.step

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.nutriragente.R
import com.example.nutriragente.data.model.FormType
import com.example.nutriragente.databinding.FragmentStepFormBinding
import com.example.nutriragente.ui.avaliacao.FormUiState
import com.example.nutriragente.ui.avaliacao.FormViewModel
import com.example.nutriragente.ui.avaliacao.FormViewModelFactory
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Base Fragment para todos os formulários de consumo alimentar.
 *
 * Implementa o padrão Typeform: uma pergunta por tela, barra de progresso
 * no topo, navegação Anterior / Próxima no rodapé e auto-avanço após
 * seleção em perguntas de escolha única.
 *
 * As subclasses apenas definem as etapas via [buildSteps] e os metadados
 * ([getFormType], [getFormTitle]). Toda a lógica de renderização,
 * animação e persistência fica aqui.
 */
abstract class StepFormFragment : Fragment(R.layout.fragment_step_form) {

    protected lateinit var viewModel: FormViewModel

    // ── Controle de etapas ─────────────────────────────────────────────────
    private lateinit var allSteps: List<FormStep>
    private var visibleSteps: List<FormStep> = emptyList()
    private var currentIndex = 0
    protected val answers = mutableMapOf<String, String>()
    private var alreadyRestored = false

    // ── View Binding ────────────────────────────────────────────────────────
    private var _binding: FragmentStepFormBinding? = null
    private val binding get() = _binding!!

    // ── Interface pública para subclasses ───────────────────────────────────
    abstract fun buildSteps(): List<FormStep>
    abstract fun getFormType(): FormType
    abstract fun getFormTitle(): String

    // ════════════════════════════════════════════════════════════════════════
    // Ciclo de vida
    // ════════════════════════════════════════════════════════════════════════

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStepFormBinding.bind(view)

        savedInstanceState?.let {
            currentIndex = it.getInt(KEY_STEP, 0)
            @Suppress("UNCHECKED_CAST")
            (it.getSerializable(KEY_ANSWERS) as? HashMap<String, String>)
                ?.let { saved -> answers.putAll(saved) }
            alreadyRestored = answers.isNotEmpty()
        }

        allSteps = buildSteps()
        setupViewModel()
        setupToolbar()
        setupNavButtons()
        recomputeVisibleSteps()
        renderStep(animate = false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_STEP, currentIndex)
        outState.putSerializable(KEY_ANSWERS, HashMap(answers))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ════════════════════════════════════════════════════════════════════════
    // Setup
    // ════════════════════════════════════════════════════════════════════════

    private fun setupViewModel() {
        val userId      = arguments?.getString("USER_ID")      ?: ""
        val childId     = arguments?.getString("CHILD_ID")     ?: ""
        val birthStr    = arguments?.getString("BIRTH_DATE")   ?: ""
        val birthDate   = if (birthStr.isNotEmpty()) LocalDate.parse(birthStr) else LocalDate.now()

        val factory = FormViewModelFactory(userId, childId, getFormType(), birthDate)
        viewModel = ViewModelProvider(this, factory)[FormViewModel::class.java]

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is FormUiState.Ready -> {
                            // Restaura respostas salvas apenas uma vez (na primeira carga)
                            if (!alreadyRestored && state.responses.isNotEmpty()) {
                                answers.putAll(state.responses)
                                alreadyRestored = true
                                recomputeVisibleSteps()
                                renderStep(animate = false)
                            }
                        }
                        is FormUiState.Saved -> {
                            Toast.makeText(requireContext(), "Formulário salvo!", Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp()
                        }
                        is FormUiState.Error -> {
                            Toast.makeText(requireContext(), "Erro: ${state.message}", Toast.LENGTH_LONG).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbarStep.title = getFormTitle()
        binding.toolbarStep.setNavigationOnClickListener { findNavController().navigateUp() }
        binding.tvFormTitle.text = getFormTitle()
    }

    private fun setupNavButtons() {
        binding.btnPrevious.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                recomputeVisibleSteps()
                renderStep(animate = true, forward = false)
            }
        }
        binding.btnNext.setOnClickListener {
            if (currentIndex < visibleSteps.lastIndex) {
                currentIndex++
                recomputeVisibleSteps()
                renderStep(animate = true, forward = true)
            } else {
                viewModel.saveForm()
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Lógica de etapas
    // ════════════════════════════════════════════════════════════════════════

    private fun recomputeVisibleSteps() {
        visibleSteps = allSteps.filter { step ->
            step.condition == null || step.condition.invoke(answers)
        }
        if (currentIndex > visibleSteps.lastIndex) {
            currentIndex = maxOf(0, visibleSteps.lastIndex)
        }
    }

    private fun renderStep(animate: Boolean = true, forward: Boolean = true) {
        if (visibleSteps.isEmpty()) return
        updateProgress()
        if (animate) {
            animateTransition { populateStep(visibleSteps[currentIndex]) }
        } else {
            populateStep(visibleSteps[currentIndex])
        }
    }

    private fun populateStep(step: FormStep) {
        binding.tvQuestion.text = step.question
        binding.optionsContainer.removeAllViews()
        binding.scrollStep.scrollTo(0, 0)

        val savedAnswer = answers[step.key]

        when (step.type) {
            StepType.SINGLE_CHOICE -> buildSingleChoice(step, savedAnswer)
            StepType.MULTI_CHOICE  -> buildMultiChoice(step, savedAnswer)
        }
    }

    // ── Escolha única (auto-avança 300 ms após seleção) ──────────────────

    private fun buildSingleChoice(step: FormStep, savedAnswer: String?) {
        step.options.forEach { option ->
            val btn = createOptionButton(option, option == savedAnswer)
            btn.setOnClickListener {
                deselectAll()
                setButtonSelected(btn, true)
                answers[step.key] = option
                viewModel.updateAnswer(step.key, option)
                recomputeVisibleSteps()
                updateProgress()

                // Auto-avança se não for a última etapa
                if (currentIndex < visibleSteps.lastIndex) {
                    btn.postDelayed({
                        if (isAdded && _binding != null) {
                            currentIndex++
                            recomputeVisibleSteps()
                            renderStep(animate = true, forward = true)
                        }
                    }, 320)
                }
            }
            binding.optionsContainer.addView(btn)
        }
    }

    // ── Múltipla escolha (checkboxes estilizados) ─────────────────────────

    private fun buildMultiChoice(step: FormStep, savedAnswer: String?) {
        val selectedValues = savedAnswer
            ?.split(",")
            ?.filter { it.isNotBlank() }
            ?.toMutableSet() ?: mutableSetOf()

        step.options.forEach { option ->
            val btn = createOptionButton(option, option in selectedValues)
            btn.setOnClickListener {
                if (option in selectedValues) {
                    selectedValues.remove(option)
                    setButtonSelected(btn, false)
                } else {
                    selectedValues.add(option)
                    setButtonSelected(btn, true)
                }
                val joined = selectedValues.joinToString(",")
                answers[step.key] = joined
                viewModel.updateAnswer(step.key, joined)
            }
            binding.optionsContainer.addView(btn)
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // UI helpers
    // ════════════════════════════════════════════════════════════════════════

    private fun createOptionButton(text: String, selected: Boolean): MaterialButton {
        return MaterialButton(requireContext()).apply {
            this.text = text
            textSize = 15f
            gravity = Gravity.START or Gravity.CENTER_VERTICAL
            setPadding(16.dp, 0, 16.dp, 0)
            cornerRadius = 12.dp
            insetTop = 0; insetBottom = 0
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                56.dp
            ).apply { bottomMargin = 12.dp }
            setButtonSelected(this, selected)
        }
    }

    private fun deselectAll() {
        for (i in 0 until binding.optionsContainer.childCount) {
            val child = binding.optionsContainer.getChildAt(i)
            if (child is MaterialButton) setButtonSelected(child, false)
        }
    }

    private fun setButtonSelected(btn: MaterialButton, selected: Boolean) {
        val blue = ContextCompat.getColor(requireContext(), R.color.blue_toolbar)
        if (selected) {
            btn.backgroundTintList = ColorStateList.valueOf(blue)
            btn.setTextColor(Color.WHITE)
            btn.strokeWidth = 0
        } else {
            btn.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            btn.setTextColor(blue)
            btn.strokeWidth = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1.5f, resources.displayMetrics
            ).toInt()
            btn.strokeColor = ColorStateList.valueOf(blue)
        }
    }

    private fun updateProgress() {
        val total   = visibleSteps.size
        val current = currentIndex + 1
        binding.tvStepCounter.text = "$current de $total"
        binding.progressBar.max = total
        binding.progressBar.setProgressCompat(current, true)

        binding.btnPrevious.isVisible = currentIndex > 0
        binding.btnNext.text = if (currentIndex == visibleSteps.lastIndex) "Confirmar ✓" else "Próxima →"
    }

    /** Crossfade suave entre etapas (150 ms out + 200 ms in). */
    private fun animateTransition(populate: () -> Unit) {
        binding.scrollStep.animate()
            .alpha(0f).setDuration(150)
            .withEndAction {
                populate()
                binding.scrollStep.animate()
                    .alpha(1f).setDuration(200).start()
            }.start()
    }

    // ════════════════════════════════════════════════════════════════════════
    // Extension helpers
    // ════════════════════════════════════════════════════════════════════════

    private val Int.dp: Int
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics
        ).toInt()

    companion object {
        private const val KEY_STEP    = "step_index"
        private const val KEY_ANSWERS = "step_answers"
    }
}
