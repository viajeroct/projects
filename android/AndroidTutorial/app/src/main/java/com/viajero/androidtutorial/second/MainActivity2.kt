package com.viajero.androidtutorial.second

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.viajero.androidtutorial.R
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity() {
    // TODO: Bundle - previous states
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        buttonForEditText.setOnClickListener {
            textViewForEditText.text = editText.text
        }
    }

    // TODO: save bundle
    override fun onSaveInstanceState(outState: Bundle) {
        outState.run {
            putString("KEY", textViewForEditText.text.toString())
        }

        super.onSaveInstanceState(outState)
    }

    // TODO: alive bundle session :)
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        textViewForEditText.text = savedInstanceState.getString("KEY")
    }
}
