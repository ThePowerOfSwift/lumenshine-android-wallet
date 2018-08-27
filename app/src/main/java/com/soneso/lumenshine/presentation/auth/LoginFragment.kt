package com.soneso.lumenshine.presentation.auth


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.soneso.lumenshine.R
import com.soneso.lumenshine.domain.data.ErrorCodes
import com.soneso.lumenshine.networking.dto.exceptions.ServerException
import com.soneso.lumenshine.presentation.util.hideProgressDialog
import com.soneso.lumenshine.presentation.util.showProgressDialog
import com.soneso.lumenshine.util.Resource
import kotlinx.android.synthetic.main.fragment_login.*


/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : AuthFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeForLiveData()
        setupListeners()
    }

    private fun subscribeForLiveData() {

        authViewModel.liveLogin.observe(this, Observer {
            renderLoginStatus(it ?: return@Observer)
        })
    }

    private fun setupListeners() {

        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })
        email_sign_in_button.setOnClickListener { attemptLogin() }
    }

    private fun attemptLogin() {

        if (!isValidForm()) {
            return
        }
        authViewModel.login(
                email.trimmedText,
                password.trimmedText,
                two_factor_code.trimmedText
        )
    }

    private fun renderLoginStatus(resource: Resource<Boolean, ServerException>) {

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
            ErrorCodes.LOGIN_EMAIL_NOT_EXIST -> {
                email.error = e.message
            }
            ErrorCodes.LOGIN_INVALID_2FA -> {
                two_factor_code.error = e.message
            }
            ErrorCodes.LOGIN_WRONG_PASSWORD -> {
                password.error = e.message
            }
            else -> {
                showErrorSnackbar(e)
            }
        }
    }

    private fun isValidForm() = email.hasValidInput()

    companion object {

        const val TAG = "LoginFragment"

        fun newInstance() = LoginFragment()
    }
}
