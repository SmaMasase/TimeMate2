package com.example.timemate2

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast

// Fragment to display a list of reminder notifications using RecyclerView
class NotificationsFragment : Fragment() {

    private lateinit var adapter: ReminderAdapter              // Adapter for displaying reminders
    private lateinit var recyclerView: RecyclerView            // RecyclerView to show reminder items
    private lateinit var sharedPref: android.content.SharedPreferences  // SharedPreferences for storing reminders
    private var reminderList = mutableListOf<Reminder>()       // List to hold loaded reminders

    // Called to inflate the fragment's UI
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate and return the notifications fragment layout
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    // Called after the view is created; good place for setup logic
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the RecyclerView and its layout manager
        recyclerView = view.findViewById(R.id.reminderecyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize SharedPreferences to fetch stored reminders
        sharedPref = requireActivity().getSharedPreferences("appointments", Context.MODE_PRIVATE)

        // Load reminders from SharedPreferences
        loadReminders()
    }

    // Loads all reminders from SharedPreferences and populates the RecyclerView
    private fun loadReminders() {
        val allEntries = sharedPref.all  // Get all key-value pairs
        Log.d("SharedPrefsDebug", "Found entries: ${allEntries.size}")  // Log count for debugging

        // Filter and parse only keys that start with "reminder_"
        reminderList = allEntries
            .filterKeys { it.startsWith("reminder_") }
            .mapNotNull { entry ->
                val value = entry.value as? String ?: return@mapNotNull null
                val lines = value.split("\n")  // Expected format: 4 lines (module, lecturer, date, time)
                if (lines.size == 4) {
                    try {
                        // Parse the individual fields from each reminder string
                        val module = lines[0].removePrefix("Module: ").trim()
                        val lecturer = lines[1].removePrefix("Lecturer: ").trim()
                        val date = lines[2].removePrefix("Date: ").trim()
                        val time = lines[3].removePrefix("Time: ").trim()
                        Reminder(module, lecturer, date, time) // Create Reminder object
                    } catch (e: Exception) {
                        Log.e("ReminderParseError", "Error parsing reminder: ${e.message}")
                        null
                    }
                } else {
                    // Handle unexpected reminder format
                    Log.w("ReminderWarning", "Reminder entry has unexpected format: $value")
                    null
                }
            }.toMutableList() // Convert result to mutable list

        // Set up the RecyclerView adapter with the loaded reminders
        adapter = ReminderAdapter(reminderList)
        recyclerView.adapter = adapter

        // Show a toast if no reminders were found
        if (reminderList.isEmpty()) {
            Toast.makeText(requireContext(), "No reminders set.", Toast.LENGTH_SHORT).show()
        }
    }
}
