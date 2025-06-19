package com.example.timemate2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// RecyclerView Adapter to display a list of Reminder objects
class ReminderAdapter(
    private var reminders: MutableList<Reminder> // List of reminders to show
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    // ViewHolder class that holds references to each item's views
    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val moduleTextView: TextView = itemView.findViewById(R.id.moduleTextView)     // Displays module name
        val lecturerTextView: TextView = itemView.findViewById(R.id.lecturerTextView) // Displays lecturer name
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)         // Displays appointment date
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)         // Displays appointment time
    }

    // Called to create a new ViewHolder and inflate the item layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reminder, parent, false) // Inflate individual reminder layout
        return ReminderViewHolder(view)
    }

    // Called to bind data to a ViewHolder at a specific position
    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position] // Get reminder data for this position

        // Set text for each TextView with formatted strings
        holder.moduleTextView.text = "Module: ${reminder.module}"
        holder.lecturerTextView.text = "Lecturer: ${reminder.lecturer}"
        holder.dateTextView.text = "Date: ${reminder.date}"
        holder.timeTextView.text = "Time: ${reminder.time}"
    }

    // Returns the total number of reminder items
    override fun getItemCount(): Int = reminders.size
}
