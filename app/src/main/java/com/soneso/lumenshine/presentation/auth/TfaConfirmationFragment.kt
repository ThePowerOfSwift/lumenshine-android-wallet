package com.soneso.lumenshine.presentation.auth


import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.soneso.lumenshine.R
import com.soneso.lumenshine.domain.data.ErrorCodes
import com.soneso.lumenshine.networking.dto.exceptions.ServerException
import com.soneso.lumenshine.util.Resource
import kotlinx.android.synthetic.main.fragment_tfa_registration.*

/**
 * A simple [Fragment] subclass.
 *
 */
class TfaConfirmationFragment : AuthFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_tfa_registration, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel.fetchTfaSecret()
        subscribeForLiveData()
        setupListeners()
    }

    private fun subscribeForLiveData() {

        authViewModel.liveTfaSecret.observe(this, Observer {
            renderTfaSecret(it ?: return@Observer)
        })

        authViewModel.liveTfaConfirmation.observe(this, Observer {
            renderTfaConfirmation(it ?: return@Observer)
        })
        authViewModel.liveTfaChangeConfirmation.observe(this, Observer {
            renderTfaConfirmation(it ?: return@Observer)
        })
    }

    private fun setupListeners() {
        nextButton.setOnClickListener {
            if (tfaInputView.hasValidInput()) {
                authViewModel.confirmTfaRegistration(tfaInputView.trimmedText)
            }
        }
    }

    private fun renderTfaSecret(resource: Resource<String, ServerException>) {

        when (resource.state) {
            Resource.FAILURE -> {
                handleError(resource.failure())
            }
            Resource.LOADING -> {
            }
            Resource.SUCCESS -> {
                setupToken(resource.success())
            }
        }
    }

    private fun renderTfaConfirmation(resource: Resource<Boolean, ServerException>) {
        when (resource.state) {
            Resource.FAILURE -> {
                hideLoadingView()
                handleError(resource.failure())
            }
            Resource.LOADING -> {
                showLoadingView()
            }
            Resource.SUCCESS -> {
                hideLoadingView()
            }
        }
    }

    /**
     * handling response errors
     */
    private fun handleError(error: ServerException) {

        when (error.code) {
            ErrorCodes.LOGIN_INVALID_2FA -> {
                tfaInputView.error = error.message
            }
            else -> {
                showErrorSnackbar(error)
            }
        }
    }

    private fun setupToken(tfaSecret: String) {

        tfaSecretView.text = getString(R.string.lbl_tfa_secret, tfaSecret)
        copyButton.setOnClickListener {

            val clipboard = context?.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("2FA secret", tfaSecret)
            clipboard.primaryClip = clip
            showSnackbar(getString(R.string.tfa_secret_copy))
        }
    }

    companion object {

        const val TAG = "TfaConfirmationFragment"

        fun newInstance() = TfaConfirmationFragment()
    }
}
