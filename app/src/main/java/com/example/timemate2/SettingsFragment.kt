package com.example.timemate2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.timemate2.databinding.FragmentSettingsBinding

// Fragment class representing the settings screen
class SettingsFragment : Fragment() {

    // View binding object to access views in the layout
    private lateinit var binding: FragmentSettingsBinding

    // ViewModel instance scoped to this fragment
    private val viewModel: SettingsViewModel by viewModels()

    // Called to inflate the layout and return the root view of the fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using DataBindingUtil to enable data binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)

        // Connect the ViewModel to the layout
        binding.viewModel = viewModel

        // Set the lifecycle owner so LiveData can auto-update the UI
        binding.lifecycleOwner = viewLifecycleOwner

        // Set a click listener on the logout button
        binding.logoutButton.setOnClickListener {
            // Create an intent to navigate back to the MainActivity (e.g., login screen)
            val intent = Intent(requireContext(), MainActivity::class.java)

            // Clear the activity stack so user cannot return to this fragment by pressing back
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            // Start the MainActivity
            startActivity(intent)

            // Finish the current activity to complete logout
            activity?.finish()
        }

        // Return the root view of the layout
        return binding.root
    }
}
