package com.soneso.lumenshine.presentation.auth


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.soneso.lumenshine.R
import com.soneso.lumenshine.networking.dto.exceptions.ServerException
import com.soneso.lumenshine.presentation.util.hideProgressDialog
import com.soneso.lumenshine.presentation.util.showProgressDialog
import com.soneso.lumenshine.util.LsException
import com.soneso.lumenshine.util.Resource
import kotlinx.android.synthetic.main.fragment_mail_confirmation.*


/**
 * A simple [Fragment] subclass.
 *
 */
class MailConfirmationFragment : AuthFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_mail_confirmation, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeForLiveData()
        setupListeners()
    }

    private fun subscribeForLiveData() {

        authViewModel.liveRegistrationRefresh.observe(this, Observer {
            renderRegistrationRefresh(it ?: return@Observer)
        })
        authViewModel.liveConfirmationMail.observe(this, Observer {
            renderConfirmationMail(it ?: return@Observer)
        })
    }

    private fun setupListeners() {

        resend_mail.setOnClickListener {
            authViewModel.resendConfirmationMail()
        }
        already_confirmed.setOnClickListener {
            mail_confirmation_error_text.text = ""
            mail_confirmation_error_text.visibility = View.VISIBLE
            authViewModel.refreshRegistrationStatus()
        }
    }

    private fun renderRegistrationRefresh(resource: Resource<Boolean, ServerException>) {

        when (resource.state) {
            Resource.LOADING -> {
                context?.showProgressDialog()
            }
            Resource.SUCCESS -> {
                hideProgressDialog()
            }
            Resource.FAILURE -> {
                hideProgressDialog()
                showErrorSnackbar(resource.failure())
            }
        }
    }

    private fun renderConfirmationMail(resource: Resource<Boolean, LsException>) {

        when (resource.state) {
            Resource.LOADING -> {
                context?.showProgressDialog()
            }
            Resource.SUCCESS -> {
                hideProgressDialog()
                showSnackbar("Mail sent!")
            }
            Resource.FAILURE -> {
                hideProgressDialog()
                showErrorSnackbar(resource.failure())
            }
        }
    }

    companion object {

        const val TAG = "MailConfirmationFragment"

        fun newInstance() = MailConfirmationFragment()
    }
}