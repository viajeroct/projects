package com.viajero.androidtutorial.trnav

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.viajero.androidtutorial.R

class FragmentOne : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageButton: ImageButton = view.findViewById(R.id.imgButton)
        val editText: EditText = view.findViewById(R.id.editText)
        // val bundle = Bundle()

        imageButton.setOnClickListener {
            val name = editText.text.toString()
            // val hello = "Hello, $name!"

            val action = FragmentOneDirections.actionFragmentOneToFragmentTwo(name /* hello */)

            val extras = FragmentNavigatorExtras()

            // bundle.putString("MyArg", hello)
            // findNavController().navigate(R.id.fragmentTwo, bundle)

            findNavController().navigate(action, extras)
        }
    }
}
