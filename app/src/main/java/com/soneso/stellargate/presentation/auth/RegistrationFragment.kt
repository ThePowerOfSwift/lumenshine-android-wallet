package com.soneso.stellargate.presentation.auth


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.soneso.stellargate.R
import com.soneso.stellargate.model.dto.DataStatus
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
        val textEmail = email.text
        val validEmail = validEmail(textEmail)
        if (!validEmail) {
            email.error = getText(R.string.error_invalid_email)
        }

        val pass = password.text
        val validPass = validPassword(pass)
        if (!validPass) {
            password.error = getText(R.string.error_invalid_password)
        }

        if (!validEmail || !validPass) {
            return
        }

        val dataProvider = regViewModel.createAccount(textEmail, pass)
        dataProvider.liveStatus.observe(this, Observer {
            val status = it ?: return@Observer

            when (status) {
                DataStatus.SUCCESS -> {
                    replaceFragment(QrCodeFragment.newInstance("This is a test!"), QrCodeFragment.TAG)
                }
            }
        })
    }

    private fun validPassword(password: CharSequence): Boolean {
        return password.length >= 9 && password.matches(Regex("^([a-zA-Z0-9+]+)\$"))
    }

    private fun validEmail(email: CharSequence): Boolean {
        return email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"))
    }

    companion object {
        const val TAG = "RegistrationFragment"

        fun newInstance() = RegistrationFragment()
    }
}
