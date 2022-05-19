package com.viajero.androidtutorial.first

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.viajero.androidtutorial.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    // TODO: late initialisation
    lateinit var onEventsObserver: OnEventsObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO: initialisation
        onEventsObserver = OnEventsObserver()
        lifecycle.addObserver(onEventsObserver)
    }

    fun toastMe(view: View) {
        val myToast = Toast.makeText(this, "Hello, user!", Toast.LENGTH_LONG)
        myToast.show()
    }

    fun countMe(view: View) {
        textView.text = (Integer.parseInt(textView.text.toString()) + 1).toString()
    }

    fun randomMe(view: View) {
        val randomIntent = Intent(this, RandomActivity::class.java)

        val number = Integer.parseInt(textView.text.toString())
        randomIntent.putExtra(RandomActivity.TOTAL_COUNT, number)

        startActivity(randomIntent)
    }

    private fun toastMeState(message: String) {
        Toast.makeText(this, "${lifecycle.currentState}, $message", Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()
        toastMeState("ON_START")
    }

    // TODO: onWindowFocusChanged
    override fun onResume() {
        super.onResume()
        toastMeState("ON_RESUME (VISIBLE)")
    }

    override fun onPostResume() {
        super.onPostResume()
        toastMeState("ALL IS WORKING")
    }

    // multi-window
    override fun onPause() {
        super.onPause()
        toastMeState("ON_PAUSE")
    }

    override fun onStop() {
        super.onStop()
        toastMeState("ON_STOP")
    }

    override fun onRestart() {
        super.onRestart()
        toastMeState("ON_RESTART")
    }

    override fun onDestroy() {
        super.onDestroy()
        toastMeState("ON_DESTROY")
    }
}
