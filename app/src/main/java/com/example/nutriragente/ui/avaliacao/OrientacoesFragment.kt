package com.example.nutriragente.ui.avaliacao

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nutriragente.R
import com.example.nutriragente.databinding.ActivityOrientacoesBinding

class OrientacoesFragment : Fragment(R.layout.activity_orientacoes) {

    private var _binding: ActivityOrientacoesBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ActivityOrientacoesBinding.bind(view)

        setupToolbar()
    }

    private fun setupToolbar() {
        binding.toolbarOrientacoes.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
