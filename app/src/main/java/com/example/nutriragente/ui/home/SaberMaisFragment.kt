package com.example.nutriragente.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nutriragente.R
import com.example.nutriragente.databinding.FragmentSabermaisBinding

class SaberMaisFragment : Fragment(R.layout.fragment_sabermais) {

    private var _binding: FragmentSabermaisBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSabermaisBinding.bind(view)

        binding.toolbarParaSaberMais.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
