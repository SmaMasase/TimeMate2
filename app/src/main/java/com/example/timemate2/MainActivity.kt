package com.example.timemate2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.timemate2.databinding.ActivityMainBinding

// This activity handles the login screen and navigation based on user role
class MainActivity : AppCompatActivity() {

    // Data binding object to connect UI layout with ViewModel
    private lateinit var binding: ActivityMainBinding

    // Create and bind the MainViewModel using Kotlin property delegation
    private val viewModel: MainViewModel by viewModels()

    // Entry point for the activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up data binding for the layout
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Connect the ViewModel to the layout so data binding works
        binding.viewModel = viewModel

        // Set lifecycle owner so LiveData updates the UI automatically
        binding.lifecycleOwner = this

        // Observe the login result LiveData from the ViewModel
        viewModel.loginResult.observe(this, Observer { result ->
            when (result) {
                // If login was successful
                is MainViewModel.LoginResult.Success -> {
                    when (result.role) {
                        "student" -> {
                            // Navigate to ModulesActivity for students
                            startActivity(Intent(this, ModulesActivity::class.java))
                            finish() // Finish current activity to prevent going back to login screen
                        }
                        "staff" -> {
                            // Navigate to StaffNotificationsActivity for staff
                            startActivity(Intent(this, StaffNotificationsActivity::class.java))
                            finish()
                        }
                    }
                }

                // If login failed, show an error message
                is MainViewModel.LoginResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
