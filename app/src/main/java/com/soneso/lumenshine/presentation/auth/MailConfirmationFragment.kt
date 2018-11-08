package com.soneso.lumenshine.presentation.auth


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.buildSpannedString
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.soneso.lumenshine.R
import com.soneso.lumenshine.model.entities.RegistrationStatus
import com.soneso.lumenshine.networking.dto.exceptions.ServerException
import com.soneso.lumenshine.presentation.util.TypefaceSpan
import com.soneso.lumenshine.util.Resource
import kotlinx.android.synthetic.main.fragment_mail_confirmation.*


/**
 * A simple [Fragment] subclass.
 *
 */
class MailConfirmationFragment : AuthFragment() {

    private var isEmailConfirmed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isEmailConfirmed = arguments?.getBoolean(ARG_EMAIL_CONFIRMED)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_mail_confirmation, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel.initLastUsername()
        setupListeners()
        subscribeForLiveData()
    }

    private fun setupDescription(username: String) {
        descriptionView.text = buildSpannedString {
            append(getText(R.string.email_confirmation_prehint))
            append(' ')
            append(username)
            val font = context?.let {
                ResourcesCompat.getFont(it, R.font.encodesans_bold)
            }
            font?.let {
                setSpan(TypefaceSpan(it), length - username.length, length, 0)
            }
            append(". ")
            append(getText(R.string.email_confirmation_posthint))
        }
    }

    private fun subscribeForLiveData() {

        authViewModel.liveLastUsername.observe(this, Observer {
            setupDescription(it ?: return@Observer)
        })
        authViewModel.liveRegistrationRefresh.observe(this, Observer {
            renderRegistrationRefresh(it ?: return@Observer)
        })
        authViewModel.liveConfirmationMail.observe(this, Observer {
            renderConfirmationMail(it ?: return@Observer)
        })
    }

    private fun setupListeners() {

        resendButton.setOnClickListener {
            errorView.text = ""
            authViewModel.resendConfirmationMail()
        }
        submitButton.setOnClickListener {
            errorView.text = ""
            if (!isEmailConfirmed) {
                errorView.setText(R.string.error_verify_email)
                authViewModel.refreshRegistrationStatus()
            } else {
                authActivity.navigate(R.id.to_mnemonic_screen)
            }
        }
    }

    private fun renderRegistrationRefresh(resource: Resource<Boolean, ServerException>) {

        when (resource.state) {
            Resource.LOADING -> {
                showLoadingView()
            }
            Resource.SUCCESS -> {
                hideLoadingView()
            }
            Resource.FAILURE -> {
                hideLoadingView()
                showErrorSnackbar(resource.failure())
            }
        }
    }

    private fun renderConfirmationMail(resource: Resource<Boolean, ServerException>) {

        when (resource.state) {
            Resource.LOADING -> {
                showLoadingView()
            }
            Resource.SUCCESS -> {
                hideLoadingView()
                showSnackbar(R.string.confirmation_mail_resent)
            }
            Resource.FAILURE -> {
                hideLoadingView()
                errorView.text = resource.failure().message
            }
        }
    }

    companion object {

        const val TAG = "MailConfirmationFragment"
        private const val ARG_EMAIL_CONFIRMED = "$TAG.ARG_EMAIL_CONFIRMED"

        fun argForEmailConfirmed(emailConfirmed: Boolean) = Bundle().apply {
            putSerializable(ARG_EMAIL_CONFIRMED, emailConfirmed)
        }

        fun newInstance() = MailConfirmationFragment()
    }
}