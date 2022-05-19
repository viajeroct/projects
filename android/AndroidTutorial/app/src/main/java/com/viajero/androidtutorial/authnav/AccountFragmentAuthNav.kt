package com.viajero.androidtutorial.authnav

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.viajero.androidtutorial.R
import com.viajero.androidtutorial.databinding.FragmentAccountAuthNavBinding

class AccountFragmentAuthNav : Fragment() {
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var binding: FragmentAccountAuthNavBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_account_auth_nav, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAuthenticationState(view.context)
    }

    private fun observeAuthenticationState(context: Context) {
        viewModel.authenticationState.observe(viewLifecycleOwner, { authenticationState ->
            if (authenticationState == LoginViewModel.AuthenticationState.AUTHENTICATED) {
                val hello = String.format(
                    resources.getString(
                        R.string.welcome_message,
                        FirebaseAuth.getInstance().currentUser?.displayName
                    )
                )
                binding.tvWelcome.text = hello
                binding.logoutButton.setOnClickListener {
                    AuthUI.getInstance().signOut(requireContext())
                    findNavController().popBackStack()
                    Toast.makeText(context, "Успешный выход!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
