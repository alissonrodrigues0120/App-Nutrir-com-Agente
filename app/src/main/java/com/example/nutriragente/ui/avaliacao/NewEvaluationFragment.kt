package com.example.nutriragente.ui.avaliacao

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.nutriragente.R
import com.example.nutriragente.databinding.NewEvaluationBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Calendar

class NewEvaluationFragment : Fragment(R.layout.new_evaluation) {

    private val viewModel: EvaluationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = NewEvaluationBinding.bind(view)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp() // Também VOLTA para a tela anterior
        }

        val window = requireActivity().window

        // Configura a status bar
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.blue_toolbar)

        // Edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Ajusta padding do conteúdo
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(
                top = systemBars.top,
                left = systemBars.left,
                right = systemBars.right,
                bottom = systemBars.bottom
            )
            insets
        }

        val etNome = view.findViewById<EditText>(R.id.et_nome)
        val etPeso = view.findViewById<EditText>(R.id.et_peso)
        val etAltura = view.findViewById<EditText>(R.id.et_altura)
        val etDataNascimento = view.findViewById<EditText>(R.id.et_data_nascimento)
        val fabConfirm = view.findViewById<FloatingActionButton>(R.id.fab_confirm)

        observeViewModel()

        // Configuração do DatePicker
        etDataNascimento.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(requireContext(), { _, ano, mes, dia ->
                val dataFormatada = String.format("%02d/%02d/%04d", dia, mes + 1, ano)
                etDataNascimento.setText(dataFormatada)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        fabConfirm.setOnClickListener {
            val nome = etNome.text.toString().trim()
            val pesoStr = etPeso.text.toString().trim()
            val alturaStr = etAltura.text.toString().trim()
            val dataStr = etDataNascimento.text.toString().trim()

            if (nome.isEmpty() || pesoStr.isEmpty() || alturaStr.isEmpty() || dataStr.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val peso = pesoStr.toDouble()
                val alturaCm = alturaStr.toDouble()
                val idadeMeses = calcularIdadeEmMeses(dataStr)

                val sexo = if (view.findViewById<RadioButton>(R.id.rb_masculino).isChecked) "M" else "F"

                val tipoAm = when {
                    view.findViewById<RadioButton>(R.id.rb_ame).isChecked -> "Exclusivo"
                    view.findViewById<RadioButton>(R.id.rb_amp).isChecked -> "Predominante"
                    view.findViewById<RadioButton>(R.id.rb_am).isChecked -> "Materno"
                    view.findViewById<RadioButton>(R.id.rb_amc).isChecked -> "Complementado"
                    view.findViewById<RadioButton>(R.id.rb_ammp).isChecked -> "Misto"
                    else -> "Artificial"
                }

                // Envia para o ViewModel incluindo a data para navegação
                viewModel.salvarAvaliacao(nome, peso, alturaCm, idadeMeses, sexo, tipoAm, dataStr)


            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erro nos dados informados", Toast.LENGTH_SHORT).show()
            }
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

    private fun calcularIdadeEmMeses(dataNascimento: String): Int {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val nascimento = LocalDate.parse(dataNascimento, formatter)
        val hoje = LocalDate.now()
        val periodo = Period.between(nascimento, hoje)
        return (periodo.years * 12) + periodo.months
    }
}
