package com.example.timemate2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReminderAdapter(
    private var reminders: MutableList<Reminder>
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val moduleTextView: TextView = itemView.findViewById(R.id.moduleTextView)
        val lecturerTextView: TextView = itemView.findViewById(R.id.lecturerTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reminder, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.moduleTextView.text = "Module: ${reminder.module}"
        holder.lecturerTextView.text = "Lecturer: ${reminder.lecturer}"
        holder.dateTextView.text = "Date: ${reminder.date}"
        holder.timeTextView.text = "Time: ${reminder.time}"
    }

    override fun getItemCount(): Int = reminders.size
}
