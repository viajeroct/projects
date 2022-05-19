package com.viajero.androidtutorial.viewpager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.viajero.androidtutorial.R

const val ARG_OBJECT = "object"

class NumberFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_number, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments.takeIf { it!!.containsKey(ARG_OBJECT) }?.apply {
            val textView: TextView = view.findViewById(R.id.textViewForPager)
            textView.text = getInt(ARG_OBJECT).toString()
        }
    }
}
