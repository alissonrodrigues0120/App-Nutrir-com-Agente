package com.example.nutriragente

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.nutriragente.R


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // <- deve ser o layout com NavHostFragment

        val navController = findNavController(R.id.nav_host_fragment)
       
    }

     override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    
    fun goToSignup() {
        val navController = findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.action_login_to_signup)
    }



    fun goToLogin() {
        val navController = findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.action_signup_to_login)
    }

    fun logintoHome() {
        val navController = findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.action_login_to_home)
    }

    fun hometoNewEvaluation() {
        val navController = findNavController(R.id.nav_host_fragment)
        navController.navigate(R.id.action_new_evaluation_to_home)

    }


}
