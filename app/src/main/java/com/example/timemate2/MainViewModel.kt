package com.example.timemate2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// ViewModel for handling login logic and state
class MainViewModel : ViewModel() {

    // Two-way data binding variables for user input (email and password)
    val email = MutableLiveData<String>("")
    val password = MutableLiveData<String>("")

    // Internal mutable LiveData to track login result (success or error)
    private val _loginResult = MutableLiveData<LoginResult>()

    // Public immutable LiveData exposed to the UI
    val loginResult: LiveData<LoginResult> get() = _loginResult

    // Hardcoded student credentials
    private val studentEmail = "Sma@gmail.com"
    private val studentPassword = "Sma123"

    // Hardcoded staff credentials
    private val staffEmail = "Masase@gmail.com"
    private val staffPassword = "Masase123"

    // Called when the student login button is pressed
    fun loginAsStudent() {
        // Validate entered credentials against hardcoded student credentials
        if (email.value == studentEmail && password.value == studentPassword) {
            // Notify observers of successful login with role "student"
            _loginResult.value = LoginResult.Success("student")
        } else {
            // Notify observers of login failure
            _loginResult.value = LoginResult.Error("Invalid student credentials")
        }
    }

    // Called when the staff login button is pressed
    fun loginAsStaff() {
        // Validate entered credentials against hardcoded staff credentials
        if (email.value == staffEmail && password.value == staffPassword) {
            // Notify observers of successful login with role "staff"
            _loginResult.value = LoginResult.Success("staff")
        } else {
            // Notify observers of login failure
            _loginResult.value = LoginResult.Error("Invalid staff credentials")
        }
    }

    // Sealed class to represent login result (either Success or Error)
    sealed class LoginResult {
        // Success case with user role information (student or staff)
        data class Success(val role: String) : LoginResult()

        // Error case with message to display
        data class Error(val message: String) : LoginResult()
    }
}
