package com.soneso.stellargate.presentation.auth


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.soneso.stellargate.R
import kotlinx.android.synthetic.main.fragment_registration.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 */
class RegistrationFragment : AuthFragment() {

    @Inject
    lateinit var regViewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_registration, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sign_in_button.setOnClickListener { replaceFragment(LoginFragment.newInstance(), LoginFragment.TAG) }

        email_registration_button.setOnClickListener {
            attemptRegistration()
        }
    }

    private fun attemptRegistration() {
        regViewModel.createAccount(email.text, password.text)
    }

    companion object {
        const val TAG = "RegistrationFragment"

        fun newInstance() = RegistrationFragment()
    }
}
