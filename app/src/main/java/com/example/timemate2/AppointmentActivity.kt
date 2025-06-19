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

        // Runtime permission for notifications (Android 13+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        val moduleName = intent.getStringExtra("module_name")
        val lecturerName = when (moduleName) {
            "MAD32A" -> "Dr.  Katsande"
            "WDP33" -> "Prof. Letsolenyane"
            "INF31" -> "Ms. Tshabalala"
            else -> "Unknown Lecturer"
        }

        when (moduleName) {
            "MAD32A" -> setContentView(R.layout.layout_appointment_mad32a)
            "WDP33" -> setContentView(R.layout.layout_appointment_wdp33)
            "INF31" -> setContentView(R.layout.layout_appointment_inf31)
            else -> {
                setContentView(R.layout.activity_appointment)
                Toast.makeText(this, "Unknown module: $moduleName", Toast.LENGTH_SHORT).show()
            }
        }

        val selectDateButton = findViewById<Button>(R.id.selectDateButton)
        val selectedDateTextView = findViewById<TextView>(R.id.selectedDateTextView)
        val timeSpinner = findViewById<Spinner>(R.id.timeSpinner)
        val selectedTimeTextView = findViewById<TextView>(R.id.selectedTimeTextView)
        val confirmButton = findViewById<Button>(R.id.confirmButton)
        val backButton = findViewById<ImageButton?>(R.id.backButton)

        var isDateSelected = false
        var isTimeSelected = false
        confirmButton?.isEnabled = false

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

        val timeSlots = listOf("-- Select Time --", "12:00", "12:30", "13:00", "13:30", "14:00")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeSlots)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeSpinner?.adapter = adapter

        timeSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    val selectedTime = timeSlots[position]
                    selectedTimeTextView?.text = "Time: $selectedTime"
                    isTimeSelected = true
                    if (isDateSelected && isTimeSelected) {
                        confirmButton?.isEnabled = true
                    }
                } else {
                    // If user selects the default "-- Select Time --"
                    selectedTimeTextView?.text = ""
                    isTimeSelected = false
                    confirmButton?.isEnabled = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        backButton?.setOnClickListener {
            finish()
        }

        confirmButton?.setOnClickListener {
            val dateText = selectedDateTextView?.text?.toString()?.removePrefix("Selected Date: ")?.trim()
            val timeText = selectedTimeTextView?.text?.toString()?.removePrefix("Time: ")?.trim()

            if (dateText.isNullOrBlank() || timeText.isNullOrBlank()) {
                Toast.makeText(this, "Please select both date and time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPref = getSharedPreferences("appointments", MODE_PRIVATE)
            val allReminders = sharedPref.all

            // Check for existing appointment on the same date
            for ((_, value) in allReminders) {
                val reminder = value as? String ?: continue
                // Reminder format: "Module: ...\nLecturer: ...\nDate: dd/MM/yyyy\nTime: hh:mm"
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

            val reminderText =
                "Module: $moduleName\nLecturer: $lecturerName\nDate: $dateText\nTime: $timeText"

            val uniqueKey = "reminder_${moduleName}_${System.currentTimeMillis()}"

            sharedPref.edit().apply {
                putString(uniqueKey, reminderText)
                apply()
            }

            try {
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

                val alarmIntent = Intent(this, NotificationReceiver::class.java).apply {
                    putExtra("module_name", moduleName)
                    putExtra("lecturer_name", lecturerName)
                    putExtra("date", dateText)
                    putExtra("time", timeText)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    uniqueKey.hashCode(),
                    alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

                if (calendar.timeInMillis > System.currentTimeMillis()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                    Toast.makeText(this, "Reminder set for $dateText at $timeText", Toast.LENGTH_LONG)
                        .show()
                } else {
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

            finish()
        }
    }

    private fun isValidDate(calendar: Calendar): Boolean {
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        if (dayOfWeek != Calendar.TUESDAY && dayOfWeek != Calendar.WEDNESDAY) return false

        val publicHolidays = listOf(
            Pair(1, 1),    // Jan 1
            Pair(4, 27),   // Freedom Day
            Pair(12, 25),  // Christmas
            Pair(12, 26)   // Boxing Day
        )

        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return !publicHolidays.contains(Pair(month, day))
    }
}
