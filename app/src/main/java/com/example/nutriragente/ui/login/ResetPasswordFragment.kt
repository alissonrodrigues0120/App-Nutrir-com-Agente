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
import com.example.nutriragente.ui.login.LogCasResViewModel

class ResetPasswordFragment : Fragment(R.layout.fragment_reset_password) {



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

        val LogCasResViewModel = LogCasResViewModel()

        val emailEditText = view.findViewById<EditText>(R.id.email)
        val resetButton = view.findViewById<Button>(R.id.reset_button)
        val backToLogin = view.findViewById<TextView>(R.id.back_to_login_text)

        resetButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(context, "Digite seu e-mail", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //
            LogCasResViewModel.resetPassword(email) { success, message ->
                if (success) {
                    Toast.makeText(context, "E-mail de recuperação enviado", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }

        }

        backToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_resetPassword_to_login)
        }
    }
}
