package com.example.timemate2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StaffNotificationsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReminderAdapter
    private val reminders = mutableListOf<Reminder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.staff_notifications_activity) // Set the layout for this screen

        // Find and set up the RecyclerView for displaying reminders
        recyclerView = findViewById(R.id.reminderecyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load reminder data from SharedPreferences
        loadReminders()

        // Set up the adapter with the loaded reminders
        adapter = ReminderAdapter(reminders)
        recyclerView.adapter = adapter

        // Set up the Logout button
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            // Create an intent to go back to MainActivity (login screen)
            val intent = Intent(this, MainActivity::class.java)

            // Clear the activity stack so user cannot go back using the back button
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            // Start MainActivity
            startActivity(intent)

            // Finish the current activity
            finish()
        }
    }

    // Function to load reminders from SharedPreferences
    private fun loadReminders() {
        val sharedPref = getSharedPreferences("appointments", MODE_PRIVATE)
        reminders.clear() // Clear the list before adding new items

        // Loop through all stored keys in SharedPreferences
        for ((key, value) in sharedPref.all) {
            if (key.startsWith("reminder_")) {
                val text = value as? String ?: continue

                // Parse the string format: "Module: XYZ\nLecturer: ABC\nDate: DD/MM/YYYY\nTime: HH:MM"
                val lines = text.lines()

                var module = ""
                var lecturer = ""
                var date = ""
                var time = ""

                for (line in lines) {
                    when {
                        line.startsWith("Module: ") -> module = line.removePrefix("Module: ").trim()
                        line.startsWith("Lecturer: ") -> lecturer = line.removePrefix("Lecturer: ").trim()
                        line.startsWith("Date: ") -> date = line.removePrefix("Date: ").trim()
                        line.startsWith("Time: ") -> time = line.removePrefix("Time: ").trim()
                    }
                }

                // Add parsed reminder to the list
                reminders.add(Reminder(module, lecturer, date, time))
            }
        }
    }
}
