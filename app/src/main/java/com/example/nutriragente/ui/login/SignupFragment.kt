package com.example.nutriragente.ui.login

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nutriragente.R
import com.google.firebase.auth.FirebaseAuth

class SignupFragment : Fragment(R.layout.activity_signup) {

    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.displayCutout()
            )
            v.updatePadding(
                top = systemBars.top,
                left = systemBars.left,
                right = systemBars.right
            )
            insets
        }

        auth = FirebaseAuth.getInstance()

        val nameEditText = view.findViewById<EditText>(R.id.signup_username)
        val emailEditText = view.findViewById<EditText>(R.id.signup_email)
        val passwordEditText = view.findViewById<EditText>(R.id.signup_password)
        val confirmPasswordEditText = view.findViewById<EditText>(R.id.signup_confirm_password)
        val signupButton = view.findViewById<Button>(R.id.signup_button)
        val loginRedirect = view.findViewById<TextView>(R.id.login_redirect_text)

        signupButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(context, "Todos os campos são obrigatórios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(context, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                        findNavController()
                            .navigate(R.id.action_signup_to_login)
                    } else {
                        Toast.makeText(
                            context,
                            "Erro: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        loginRedirect.setOnClickListener {
            findNavController()
                .navigate(R.id.action_signup_to_login)
        }
    }
}
