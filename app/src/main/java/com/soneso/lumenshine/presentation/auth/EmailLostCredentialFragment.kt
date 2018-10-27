package com.soneso.lumenshine.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.soneso.lumenshine.R
import kotlinx.android.synthetic.main.fragment_mail_lost_credential.*


/**
 * A simple [Fragment] subclass.
 */
class EmailLostCredentialFragment : AuthFragment() {
    private lateinit var credential: Credential

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        credential = arguments?.getSerializable(EmailLostCredentialFragment.ARG_CREDENTIAL) as? Credential ?: Credential.PASSWORD
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_mail_lost_credential, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateViews()
        setupListeners()
        subscribeForLiveData()
    }

    private fun populateViews() {
        when (credential) {
            Credential.PASSWORD -> {
                subtitleSuccessView.setText(R.string.reset_password_email_sent)
                subTitleView.setText(R.string.enter_email_address_reset_password_description)
            }
            Credential.TFA -> {
                subtitleSuccessView.setText(R.string.reset_2fa_email_sent)
                subTitleView.setText(R.string.enter_email_address_reset_2fa_description)
            }
        }
    }

    private fun setupListeners() {

    }

    private fun subscribeForLiveData() {

    }

    companion object {

        const val TAG = "EmailLostCredentialFragment"
        private const val ARG_CREDENTIAL = "$TAG.ARG_CREDENTIAL"

        fun argForPassword() = Bundle().apply {
            putSerializable(ARG_CREDENTIAL, Credential.PASSWORD)
        }

        fun argForTfa() = Bundle().apply {
            putSerializable(ARG_CREDENTIAL, Credential.TFA)
        }
    }
}