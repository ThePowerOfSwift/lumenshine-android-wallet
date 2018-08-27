package com.soneso.lumenshine.presentation.auth


import android.app.Activity
import android.arch.lifecycle.Observer
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import com.soneso.lumenshine.R
import com.soneso.lumenshine.domain.data.ErrorCodes
import com.soneso.lumenshine.networking.dto.exceptions.ServerException
import com.soneso.lumenshine.presentation.util.hideProgressDialog
import com.soneso.lumenshine.presentation.util.showInfoDialog
import com.soneso.lumenshine.presentation.util.showProgressDialog
import com.soneso.lumenshine.util.Resource
import kotlinx.android.synthetic.main.fragment_registration.*


/**
 * A simple [Fragment] subclass.
 */
class RegistrationFragment : AuthFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_registration, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeForLiveData()

        setupListeners()
        password_info_button.setOnClickListener {
            (activity as Activity).showInfoDialog(R.string.password_requirements, R.layout.info_password)
        }
    }

    private fun subscribeForLiveData() {

        authViewModel.liveRegistration
                .observe(this, Observer {
                    renderRegistration(it ?: return@Observer)
                })
    }

    private fun setupListeners() {

        sign_in_button.setOnClickListener { replaceFragment(LoginFragment.newInstance(), LoginFragment.TAG) }
        email_registration_button.setOnClickListener { attemptRegistration() }
        salutation_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d(TAG, "Selected position: nothing")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val textView = view as? TextView ?: return
                textView.setTextColor(Color.BLACK)
            }
        }
    }

    private fun renderRegistration(resource: Resource<Boolean, ServerException>) {

        when (resource.state) {

            Resource.LOADING -> {
                context?.showProgressDialog()
            }
            Resource.FAILURE -> {

                hideProgressDialog()
                handleError(resource.failure())
            }
            else -> {
                hideProgressDialog()
            }
        }
    }

    /**
     * handling login response errors
     */
    private fun handleError(e: ServerException) {

        when (e.code) {
            ErrorCodes.SIGNUP_EMAIL_ALREADY_EXIST -> {
                email.error = e.message
            }
            else -> {
                showErrorSnackbar(e)
            }
        }
    }

    private fun attemptRegistration() {

        if (!isValidForm()) {
            return
        }

        authViewModel.createAccount(
                email.trimmedText,
                password.trimmedText,
                country_spinner.selectedItemPosition
        )
    }

    private fun isValidForm() =
            email.hasValidInput()
                    && password.isValidPassword()
                    && isPasswordMatch()


    private fun isPasswordMatch(): Boolean {
        val match = password.trimmedText == password_confirmation.trimmedText
        if (!match) {
            password.error = getString(R.string.password_not_match)
            password_confirmation.error = getString(R.string.password_not_match)
        }
        return match
    }

    companion object {
        const val TAG = "RegistrationFragment"

        fun newInstance() = RegistrationFragment()
    }
}
