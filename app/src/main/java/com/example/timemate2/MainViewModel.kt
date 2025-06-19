package com.example.timemate2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val email = MutableLiveData<String>("")
    val password = MutableLiveData<String>("")

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> get() = _loginResult

    private val studentEmail = "Sma@gmail.com"
    private val studentPassword = "Sma123"
    private val staffEmail = "Masase@gmail.com"
    private val staffPassword = "Masase123"

    fun loginAsStudent() {
        if (email.value == studentEmail && password.value == studentPassword) {
            _loginResult.value = LoginResult.Success("student")
        } else {
            _loginResult.value = LoginResult.Error("Invalid student credentials")
        }
    }

    fun loginAsStaff() {
        if (email.value == staffEmail && password.value == staffPassword) {
            _loginResult.value = LoginResult.Success("staff")
        } else {
            _loginResult.value = LoginResult.Error("Invalid staff credentials")
        }
    }

    sealed class LoginResult {
        data class Success(val role: String) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }
}
