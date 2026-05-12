package com.example.nutriragente.ui.login

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.nutriragente.R
import com.example.nutriragente.util.setupLoginWindow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ResetPasswordFragment : Fragment(R.layout.fragment_reset_password) {

    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLoginWindow()

        val emailEditText = view.findViewById<EditText>(R.id.email)
        val resetButton   = view.findViewById<Button>(R.id.reset_button)
        val backToLogin   = view.findViewById<TextView>(R.id.back_to_login_text)

        resetButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(context, "Digite seu e-mail", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.resetPassword(email)
        }

        backToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_resetPassword_to_login)
        }

        observeEvents()
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { event ->
                    when (event) {
                        is AuthViewModel.AuthEvent.ResetSuccess ->
                            Toast.makeText(context, "E-mail de recuperação enviado", Toast.LENGTH_SHORT).show()
                        is AuthViewModel.AuthEvent.Error ->
                            Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                        else -> Unit
                    }
                }
            }
        }
    }
}
