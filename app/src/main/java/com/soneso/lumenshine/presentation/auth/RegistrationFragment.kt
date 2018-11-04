package com.soneso.lumenshine.presentation.auth


import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
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

        initViews()

        subscribeForLiveData()

        setupListeners()
    }

    private fun initViews() {
        val termsOfService = getString(R.string.terms_of_service)
        val youAgreeToAbideTermsOfUse = getString(R.string.you_agree_to_abide) + termsOfService

        val spannable = SpannableString(youAgreeToAbideTermsOfUse)

        spannable.setSpan(object: ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }

            override fun onClick(p0: View) {
                Toast.makeText(context, "TERMS OF SERVICE SCREEN SHOULD OPEN", Toast.LENGTH_LONG).show()
            }
        }, youAgreeToAbideTermsOfUse.length - termsOfService.length, youAgreeToAbideTermsOfUse.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannable.setSpan(object: ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }

            override fun onClick(p0: View) {
                checkboxTermsOfService.isChecked = !checkboxTermsOfService.isChecked
            }
        }, 0, youAgreeToAbideTermsOfUse.length - termsOfService.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val spanColor: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context!!.getColor(R.color.blue)
        } else {
            context!!.resources.getColor(R.color.blue)
        }
        spannable.setSpan(ForegroundColorSpan(spanColor), youAgreeToAbideTermsOfUse.length - termsOfService.length, youAgreeToAbideTermsOfUse.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        agreeToTermsOfService.setText(spannable, TextView.BufferType.SPANNABLE)
        agreeToTermsOfService.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun subscribeForLiveData() {

        authViewModel.liveRegistration
                .observe(this, Observer {
                    renderRegistration(it ?: return@Observer)
                })
    }

    private fun setupListeners() {

        registerButton.setOnClickListener { attemptRegistration() }
    }

    private fun renderRegistration(resource: Resource<Boolean, ServerException>) {

        when (resource.state) {

            Resource.LOADING -> {
                showLoadingView()
            }
            Resource.FAILURE -> {

                hideLoadingView()
                handleError(resource.failure())
            }
            else -> {
                hideLoadingView()
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
