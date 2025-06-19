package com.example.timemate2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.timemate2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.loginResult.observe(this, Observer { result ->
            when (result) {
                is MainViewModel.LoginResult.Success -> {
                    when (result.role) {
                        "student" -> {
                            startActivity(Intent(this, ModulesActivity::class.java))
                            finish()
                        }
                        "staff" -> {
                            startActivity(Intent(this, StaffNotificationsActivity::class.java))
                            finish()
                        }
                    }
                }
                is MainViewModel.LoginResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
