package com.example.timemate2

import android.Manifest
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class AppointmentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request runtime permission for notifications on Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        // Get module name from intent
        val moduleName = intent.getStringExtra("module_name")

        // Determine lecturer name based on module
        val lecturerName = when (moduleName) {
            "MAD32A" -> "Dr. Katsande"
            "WDP33" -> "Prof. Letsolenyane"
            "INF31" -> "Ms. Tshabalala"
            else -> "Unknown Lecturer"
        }

        // Load appropriate layout based on module
        when (moduleName) {
            "MAD32A" -> setContentView(R.layout.layout_appointment_mad32a)
            "WDP33" -> setContentView(R.layout.layout_appointment_wdp33)
            "INF31" -> setContentView(R.layout.layout_appointment_inf31)
            else -> {
                setContentView(R.layout.activity_appointment)
                Toast.makeText(this, "Unknown module: $moduleName", Toast.LENGTH_SHORT).show()
            }
        }

        // Initialize UI elements
        val selectDateButton = findViewById<Button>(R.id.selectDateButton)
        val selectedDateTextView = findViewById<TextView>(R.id.selectedDateTextView)
        val timeSpinner = findViewById<Spinner>(R.id.timeSpinner)
        val selectedTimeTextView = findViewById<TextView>(R.id.selectedTimeTextView)
        val confirmButton = findViewById<Button>(R.id.confirmButton)
        val backButton = findViewById<ImageButton?>(R.id.backButton)

        // Flags to track if both date and time are selected
        var isDateSelected = false
        var isTimeSelected = false
        confirmButton?.isEnabled = false

        // Handle date selection
        selectDateButton?.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDay)

                    // Check if date is valid (Tues/Wed and not a holiday)
                    if (isValidDate(selectedCalendar)) {
                        val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                        selectedDateTextView?.text = "Selected Date: $date"
                        isDateSelected = true
                        if (isDateSelected && isTimeSelected) {
                            confirmButton?.isEnabled = true
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Only Tuesdays/Wednesdays excluding holidays are allowed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        // Set up time spinner with options
        val timeSlots = listOf("-- Select Time --", "12:00", "12:30", "13:00", "13:30", "14:00")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeSlots)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeSpinner?.adapter = adapter

        // Handle time selection
        timeSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                if (position > 0) {
                    val selectedTime = timeSlots[position]
                    selectedTimeTextView?.text = "Time: $selectedTime"
                    isTimeSelected = true
                    if (isDateSelected && isTimeSelected) {
                        confirmButton?.isEnabled = true
                    }
                } else {
                    selectedTimeTextView?.text = ""
                    isTimeSelected = false
                    confirmButton?.isEnabled = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Go back when back button is clicked
        backButton?.setOnClickListener {
            finish()
        }

        // Handle confirm appointment
        confirmButton?.setOnClickListener {
            val dateText = selectedDateTextView?.text?.toString()?.removePrefix("Selected Date: ")?.trim()
            val timeText = selectedTimeTextView?.text?.toString()?.removePrefix("Time: ")?.trim()

            if (dateText.isNullOrBlank() || timeText.isNullOrBlank()) {
                Toast.makeText(this, "Please select both date and time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPref = getSharedPreferences("appointments", MODE_PRIVATE)
            val allReminders = sharedPref.all

            // Prevent double booking on the same date
            for ((_, value) in allReminders) {
                val reminder = value as? String ?: continue
                val lines = reminder.lines()
                val existingDate = lines.find { it.startsWith("Date:") }?.removePrefix("Date:")?.trim()

                if (existingDate == dateText) {
                    Toast.makeText(
                        this,
                        "You already have an appointment booked on $dateText.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
            }

            // Format reminder string
            val reminderText = "Module: $moduleName\nLecturer: $lecturerName\nDate: $dateText\nTime: $timeText"
            val uniqueKey = "reminder_${moduleName}_${System.currentTimeMillis()}"

            // Save reminder to SharedPreferences
            sharedPref.edit().apply {
                putString(uniqueKey, reminderText)
                apply()
            }

            try {
                // Parse date and time into calendar instance
                val dateParts = dateText.split("/")
                val timeParts = timeText.split(":")
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, dateParts[2].toInt())
                    set(Calendar.MONTH, dateParts[1].toInt() - 1)
                    set(Calendar.DAY_OF_MONTH, dateParts[0].toInt())
                    set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
                    set(Calendar.MINUTE, timeParts[1].toInt())
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                // Create intent to trigger notification
                val alarmIntent = Intent(this, NotificationReceiver::class.java).apply {
                    putExtra("module_name", moduleName)
                    putExtra("lecturer_name", lecturerName)
                    putExtra("date", dateText)
                    putExtra("time", timeText)
                }

                // Use PendingIntent for the alarm
                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    uniqueKey.hashCode(),
                    alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // Set up the AlarmManager
                val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

                if (calendar.timeInMillis > System.currentTimeMillis()) {
                    // Schedule notification for the selected date/time
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                    Toast.makeText(this, "Reminder set for $dateText at $timeText", Toast.LENGTH_LONG).show()
                } else {
                    // If selected time is in the past, send notification immediately
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + 2000,
                        pendingIntent
                    )
                    Toast.makeText(
                        this,
                        "Selected time is in the past, notification sent now.",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Reminder Set Successfully", Toast.LENGTH_SHORT).show()
            }

            // Close activity after booking
            finish()
        }
    }

    // Helper function to check if selected date is valid
    private fun isValidDate(calendar: Calendar): Boolean {
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        // Only allow Tuesdays and Wednesdays
        if (dayOfWeek != Calendar.TUESDAY && dayOfWeek != Calendar.WEDNESDAY) return false

        // Define public holidays (month, day)
        val publicHolidays = listOf(
            Pair(1, 1),    // New Year's Day
            Pair(4, 27),   // Freedom Day
            Pair(12, 25),  // Christmas Day
            Pair(12, 26)   // Boxing Day
        )

        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Return false if selected date is a public holiday
        return !publicHolidays.contains(Pair(month, day))
    }
}
