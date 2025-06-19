package com.example.timemate2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

// This activity serves as the main hub for students after login
// It uses a BottomNavigationView to switch between fragments: Dashboard, Calendar, Notifications, and Settings
class ModulesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the UI layout for this activity
        setContentView(R.layout.activity_modules)

        // Find the BottomNavigationView from the layout
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Load the default fragment when the activity is first created
        loadFragment(DashboardFragment())

        // Set up item selection listener for navigation bar
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                // Load respective fragment based on the selected item
                R.id.nav_dashboard -> loadFragment(DashboardFragment())
                R.id.nav_calendar -> loadFragment(CalendarFragment())
                R.id.nav_notifications -> loadFragment(NotificationsFragment())
                R.id.nav_settings -> loadFragment(SettingsFragment())
                else -> false
            }
            true // Return true to indicate the item was handled
        }
    }

    // Function to replace the fragment container with the selected fragment
    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.moduleFragmentContainer, fragment) // Replace existing fragment
            .commit() // Commit the transaction
        return true
    }
}
