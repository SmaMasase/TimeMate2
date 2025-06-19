package com.example.timemate2

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

// This activity handles feedback submission using a RatingBar and EditText.
class FeedbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout (make sure this layout has ratingBar, etComment, and btnSubmit)
        setContentView(R.layout.layout_appointment_mad32a) // <-- This layout name might be incorrect for feedback

        // Initialize UI elements
        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)   // User selects rating here
        val etComment = findViewById<EditText>(R.id.etComment)    // User writes feedback here
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)      // Submit button

        // Handle submit button click
        btnSubmit.setOnClickListener {
            val rating = ratingBar.rating                        // Get user rating
            val comment = etComment.text.toString().trim()       // Get user comment

            // Ensure a rating was selected before submitting
            if (rating == 0f) {
                Toast.makeText(this, "Please provide a rating before submitting.", Toast.LENGTH_SHORT).show()
            } else {
                // Show thank you message including rating and optional comment
                Toast.makeText(
                    this,
                    "Thank you for your feedback!\nRating: $rating\nComment: ${if (comment.isNotEmpty()) comment else "No comment"}",
                    Toast.LENGTH_LONG
                ).show()

                // Optional: Reset the form after submission
                etComment.text.clear()
                ratingBar.rating = 0f

                // Navigate back to the dashboard
                val intent = Intent(this, DashboardFragment::class.java) // ⚠️ Fragments cannot be opened this way
                startActivity(intent)
                finish()
            }
        }
    }
}
