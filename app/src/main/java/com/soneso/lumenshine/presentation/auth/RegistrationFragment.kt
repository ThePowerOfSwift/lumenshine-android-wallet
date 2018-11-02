package com.soneso.lumenshine.presentation.auth


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.soneso.lumenshine.R
import com.soneso.lumenshine.domain.data.ErrorCodes
import com.soneso.lumenshine.networking.dto.exceptions.ServerException
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
    }

    private fun subscribeForLiveData() {
        authViewModel.liveRegistration.observe(this, Observer {
                    renderRegistration(it ?: return@Observer)
                })
    }

    private fun setupListeners() {

        registerButton.setOnClickListener { attemptRegistration() }
    }

    private fun renderRegistration(resource: Resource<Boolean, ServerException>) {
        when (resource.state) {

            Resource.LOADING -> {

            }
            Resource.FAILURE -> {

                hideLoadingView()
                handleError(resource.failure())
            }
            else -> {
                hideLoadingView()
                authActivity.navigate(R.id.to_confirm_tfa_screen)
            }
        }
    }

    /**
     * handling login response errors
     */
    private fun handleError(e: ServerException) {

        when (e.code) {
            ErrorCodes.SIGNUP_EMAIL_ALREADY_EXIST -> {
                emailView.error = e.message
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

        showLoadingView()

        authViewModel.createAccount(
                emailView.trimmedText,
                password.trimmedText
        )
    }

    private fun isValidForm() =
            emailView.hasValidInput()
                    && password.isValidPassword()
                    && isPasswordMatch()


    private fun isPasswordMatch(): Boolean {
        val match = password.trimmedText == passConfirmationView.trimmedText
        if (!match) {
            password.error = getString(R.string.password_not_match)
            passConfirmationView.error = getString(R.string.password_not_match)
        }
        return match
    }

    companion object {
        const val TAG = "RegistrationFragment"

        fun newInstance() = RegistrationFragment()
    }
}
