package com.viajero.androidtutorial.first

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.viajero.androidtutorial.R
import kotlinx.android.synthetic.main.activity_random.*

class RandomActivity : AppCompatActivity() {
    companion object {
        const val TOTAL_COUNT = "total_count"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random)

        showRandomNumber()
    }

    private fun showRandomNumber() {
        val count = intent.getIntExtra(TOTAL_COUNT, 0)
        val randomIntArray = mutableListOf((0..count).random())
        for (i in 0..5) {
            randomIntArray.add((0..count).random())
        }
        textViewRandom.text = randomIntArray.toString()
        textViewLabel.text = getString(R.string.random_string, count)
    }
}
