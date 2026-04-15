package com.example.nutriragente.ui.login

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nutriragente.R
import com.google.firebase.auth.FirebaseAuth

class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            findNavController().navigate(R.id.action_splash_to_home)
        } else {
            findNavController().navigate(R.id.action_splash_to_login)
        }
    }
}