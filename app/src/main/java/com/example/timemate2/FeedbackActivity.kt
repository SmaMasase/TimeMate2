package com.example.timemate2

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class FeedbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_appointment_mad32a)

        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        val etComment = findViewById<EditText>(R.id.etComment)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            val rating = ratingBar.rating
            val comment = etComment.text.toString().trim()

            if (rating == 0f) {
                Toast.makeText(this, "Please provide a rating before submitting.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Thank you for your feedback!\nRating: $rating\nComment: ${if (comment.isNotEmpty()) comment else "No comment"}",
                    Toast.LENGTH_LONG
                ).show()

                // Optional: clear fields after submission
                etComment.text.clear()
                ratingBar.rating = 0f

                // Navigate to DashboardActivity
                val intent = Intent(this, DashboardFragment::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
