package com.example.nutriragente.ui.login

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nutriragente.R
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment(R.layout.activity_login) {

    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val emailEditText = view.findViewById<EditText>(R.id.login_email)
        val passwordEditText = view.findViewById<EditText>(R.id.password)
        val loginButton = view.findViewById<Button>(R.id.login_button)
        val signupText = view.findViewById<TextView>(R.id.signup_redirect_text)
        val forgotPasswordText = view.findViewById<TextView>(R.id.resetpassword_redirect_text)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        findNavController().navigate(R.id.action_login_to_home)
                    } else {
                        Toast.makeText(
                            context,
                            "Erro: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        signupText.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_signup)
        }

        forgotPasswordText.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_resetPasswordFragment)
        }
    }
}
