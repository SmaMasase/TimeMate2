package com.example.timemate2

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// ViewModel class to manage UI-related data for a settings screen
class SettingsViewModel : ViewModel() {

    // LiveData to hold the user's email
    val email = MutableLiveData<String>()

    // LiveData to hold the user's password
    val password = MutableLiveData<String>()

    // Initialization block to set default values
    init {
        email.value = "Sma123"      // Default email value (can be updated by user input)
        password.value = ""         // Default password value (empty at start)
    }
}
