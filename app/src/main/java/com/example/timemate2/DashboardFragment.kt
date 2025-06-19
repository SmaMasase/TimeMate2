package com.example.timemate2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment

// This fragment represents the student dashboard screen with module options.
class DashboardFragment : Fragment() {

    // Inflates the layout for this fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate and return the dashboard layout (fragment_dashboard.xml)
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    // Called after the view is created. Here we handle UI logic and interaction
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get references to each module card (ConstraintLayouts)
        val module1 = view.findViewById<ConstraintLayout>(R.id.constraintLayout)
        val module2 = view.findViewById<ConstraintLayout>(R.id.frameLayout6)
        val module3 = view.findViewById<ConstraintLayout>(R.id.frameLayout7)

        // Set click listeners for each module card to open appointment booking
        module1.setOnClickListener {
            openAppointmentPage("MAD32A") // Launch appointment screen for MAD32A
        }

        module2.setOnClickListener {
            openAppointmentPage("WDP33") // Launch appointment screen for WDP33
        }

        module3.setOnClickListener {
            openAppointmentPage("INF31") // Launch appointment screen for INF31
        }
    }

    // Opens the AppointmentActivity and passes the selected module name
    private fun openAppointmentPage(moduleName: String) {
        val intent = Intent(requireContext(), AppointmentActivity::class.java)
        intent.putExtra("module_name", moduleName) // Pass the selected module
        startActivity(intent) // Start the appointment booking activity
    }
}
