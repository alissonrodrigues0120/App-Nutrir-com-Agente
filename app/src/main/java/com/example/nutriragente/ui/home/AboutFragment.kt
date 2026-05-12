package com.example.nutriragente.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nutriragente.R
import com.example.nutriragente.databinding.AboutBinding

class AboutFragment : Fragment(R.layout.about) {

    private var _binding: AboutBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = AboutBinding.bind(view)

        binding.toolbarAbout.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
