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

class NotificationsFragment : Fragment() {

    private lateinit var adapter: ReminderAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var sharedPref: android.content.SharedPreferences
    private var reminderList = mutableListOf<Reminder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notifications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.reminderecyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        sharedPref = requireActivity().getSharedPreferences("appointments", Context.MODE_PRIVATE)

        loadReminders()
    }

    private fun loadReminders() {
        val allEntries = sharedPref.all
        Log.d("SharedPrefsDebug", "Found entries: ${allEntries.size}")

        reminderList = allEntries
            .filterKeys { it.startsWith("reminder_") }
            .mapNotNull { entry ->
                val value = entry.value as? String ?: return@mapNotNull null
                val lines = value.split("\n")
                if (lines.size == 4) {
                    try {
                        val module = lines[0].removePrefix("Module: ").trim()
                        val lecturer = lines[1].removePrefix("Lecturer: ").trim()
                        val date = lines[2].removePrefix("Date: ").trim()
                        val time = lines[3].removePrefix("Time: ").trim()
                        Reminder(module, lecturer, date, time)
                    } catch (e: Exception) {
                        Log.e("ReminderParseError", "Error parsing reminder: ${e.message}")
                        null
                    }
                } else {
                    Log.w("ReminderWarning", "Reminder entry has unexpected format: $value")
                    null
                }
            }.toMutableList()

        adapter = ReminderAdapter(reminderList)
        recyclerView.adapter = adapter

        if (reminderList.isEmpty()) {
            Toast.makeText(requireContext(), "No reminders set.", Toast.LENGTH_SHORT).show()
        }
    }
}
