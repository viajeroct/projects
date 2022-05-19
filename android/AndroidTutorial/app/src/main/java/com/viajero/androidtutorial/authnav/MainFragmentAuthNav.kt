package com.viajero.androidtutorial.authnav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.viajero.androidtutorial.R
import com.viajero.androidtutorial.databinding.FragmentMainAuthNavBinding

class MainFragmentAuthNav : Fragment() {
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var binding: FragmentMainAuthNavBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_main_auth_nav, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAuthenticationState()
    }

    private fun observeAuthenticationState() {
        viewModel.authenticationState.observe(viewLifecycleOwner, { authenticationState ->
            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    binding.buttonPersonal.setOnClickListener {
                        findNavController().navigate(R.id.accountFragmentAuthNav)
                    }
                }
                else -> {
                    binding.buttonPersonal.setOnClickListener {
                        findNavController().navigate(R.id.loginFragmentAuthNav)
                    }
                }
            }
        })
    }
}
