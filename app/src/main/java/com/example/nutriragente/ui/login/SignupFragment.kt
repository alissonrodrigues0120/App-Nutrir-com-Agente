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
class SignupFragment : Fragment(R.layout.activity_signup) {

    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLoginWindow()

        val nameEditText            = view.findViewById<EditText>(R.id.signup_username)
        val emailEditText           = view.findViewById<EditText>(R.id.signup_email)
        val passwordEditText        = view.findViewById<EditText>(R.id.signup_password)
        val confirmPasswordEditText = view.findViewById<EditText>(R.id.signup_confirm_password)
        val signupButton            = view.findViewById<Button>(R.id.signup_button)
        val loginRedirect           = view.findViewById<TextView>(R.id.login_redirect_text)

        signupButton.setOnClickListener {
            val name            = nameEditText.text.toString().trim()
            val email           = emailEditText.text.toString().trim()
            val password        = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(context, "Todos os campos são obrigatórios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(context, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.register(email, password)
        }

        loginRedirect.setOnClickListener {
            findNavController().navigate(R.id.action_signup_to_login)
        }

        observeEvents()
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { event ->
                    when (event) {
                        is AuthViewModel.AuthEvent.RegisterSuccess ->
                            findNavController().navigate(R.id.action_signup_to_login)
                        is AuthViewModel.AuthEvent.Error ->
                            Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                        else -> Unit
                    }
                }
            }
        }
    }
}
