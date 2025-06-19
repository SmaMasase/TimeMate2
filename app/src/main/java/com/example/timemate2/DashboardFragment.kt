package com.example.timemate2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment

class DashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate and return your dashboard fragment layout
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val module1 = view.findViewById<ConstraintLayout>(R.id.constraintLayout)
        val module2 = view.findViewById<ConstraintLayout>(R.id.frameLayout6)
        val module3 = view.findViewById<ConstraintLayout>(R.id.frameLayout7)

        module1.setOnClickListener {
            openAppointmentPage("MAD32A")
        }

        module2.setOnClickListener {
            openAppointmentPage("WDP33")
        }

        module3.setOnClickListener {
            openAppointmentPage("INF31")
        }
    }

    private fun openAppointmentPage(moduleName: String) {
        val intent = Intent(requireContext(), AppointmentActivity::class.java)
        intent.putExtra("module_name", moduleName)
        startActivity(intent)
    }

}
