package com.viajero.androidtutorial.navigation

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.viajero.androidtutorial.R

class MainFragment : Fragment(R.layout.fragment_main) {
    companion object {
        const val usernameKey = "USER_NAME"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            view.findViewById<AppCompatTextView>(R.id.mainNameTextView).text = arguments?.getString(
            usernameKey
        )
    }
}
