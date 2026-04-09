package com.example.nutriragente.ui.avaliacao

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.nutriragente.R
import com.example.nutriragente.databinding.NewEvaluationtwoyearsBinding
import com.example.nutriragente.data.model.FormType
import java.time.LocalDate


class FormsFragment_twoyears : Fragment(R.layout.new_evaluationtwoyears) {

    private var _binding: NewEvaluationtwoyearsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FormViewModel

    companion object {
        fun newInstance(userId: String, childId: String, birthDate: String) =
            FormsFragment_twoyears().apply {
                arguments = Bundle().apply {
                    putString("USER_ID", userId)
                    putString("CHILD_ID", childId)
                    putString("BIRTH_DATE", birthDate)
                }
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = NewEvaluationtwoyearsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val uid = it.getString("USER_ID", "")
            val cid = it.getString("CHILD_ID", "")
            val dateStr = it.getString("BIRTH_DATE", "")
            val bd = if (dateStr.isNotEmpty()) LocalDate.parse(dateStr) else LocalDate.now()
            val factory = FormViewModelFactory(uid, cid, FormType.TWO_YEARS_PLUS, bd)
            viewModel = ViewModelProvider(this, factory)[FormViewModel::class.java]
        }

        setupToolbar()
        setupCheckboxes()
        setupRadios()
        setupFab()
    }

    private fun setupCheckboxes() {
        val listener = CompoundButton.OnCheckedChangeListener { _, _ ->
            val selected = mutableListOf<String>()
            if (binding.cbCafeManha.isChecked) selected.add("Cafe")
            if (binding.cbLancheManha.isChecked) selected.add("LancheManha")
            if (binding.cbAlmoco.isChecked) selected.add("Almoco")
            if (binding.cbLancheTarde.isChecked) selected.add("LancheTarde")
            if (binding.cbJantar.isChecked) selected.add("Jantar")
            if (binding.cbCeia.isChecked) selected.add("Ceia")

            viewModel.updateAnswer("refeicoes", selected.joinToString(","))
        }

        binding.cbCafeManha.setOnCheckedChangeListener(listener)
        binding.cbLancheManha.setOnCheckedChangeListener(listener)
        binding.cbAlmoco.setOnCheckedChangeListener(listener)
        binding.cbLancheTarde.setOnCheckedChangeListener(listener)
        binding.cbJantar.setOnCheckedChangeListener(listener)
        binding.cbCeia.setOnCheckedChangeListener(listener)
    }

    private fun setupRadios() {
        // Configurar radios (costume_refeicoes, feijao, etc)
        // Similar aos anteriores
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
    }

    private fun setupFab() {
        binding.fabConfirm.setOnClickListener { viewModel.saveForm() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}