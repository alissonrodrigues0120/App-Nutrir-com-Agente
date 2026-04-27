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
class LoginFragment : Fragment(R.layout.activity_login) {

    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLoginWindow()

        val emailEditText    = view.findViewById<EditText>(R.id.login_email)
        val passwordEditText = view.findViewById<EditText>(R.id.password)
        val loginButton      = view.findViewById<Button>(R.id.login_button)
        val signupText       = view.findViewById<TextView>(R.id.signup_redirect_text)
        val forgotText       = view.findViewById<TextView>(R.id.resetpassword_redirect_text)

        loginButton.setOnClickListener {
            val email    = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(email, password)
        }

        signupText.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_signup)
        }

        forgotText.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_resetPasswordFragment)
        }

        observeEvents()
    }

    private fun observeEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { event ->
                    when (event) {
                        is AuthViewModel.AuthEvent.LoginSuccess ->
                            findNavController().navigate(R.id.action_login_to_home)
                        is AuthViewModel.AuthEvent.Error ->
                            Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                        else -> Unit
                    }
                }
            }
        }
    }
}
