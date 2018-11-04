package com.soneso.lumenshine.presentation.auth


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.soneso.lumenshine.R
import com.soneso.lumenshine.domain.data.ErrorCodes
import com.soneso.lumenshine.networking.dto.exceptions.ServerException
import com.soneso.lumenshine.persistence.LsPrefs
import com.soneso.lumenshine.util.GeneralUtils
import com.soneso.lumenshine.util.Resource
import kotlinx.android.synthetic.main.fragment_tfa_registration.*

/**
 * A simple [Fragment] subclass.
 *
 */
class TfaConfirmationFragment : AuthFragment() {

    private lateinit var tfaConfirmationViewModel: TFAConfirmationViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_tfa_registration, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tfaConfirmationViewModel = ViewModelProviders.of(this, viewModelFactory)[TFAConfirmationViewModel::class.java]

        tfaConfirmationViewModel.fetchTfaSecret()
        subscribeForLiveData()
        setupListeners()
        // TODO: TFA secreed need to get with live data
        setupToken(LsPrefs.tfaSecret)
    }

    private fun subscribeForLiveData() {

        tfaConfirmationViewModel.liveTfaSecret.observe(this, Observer {

            setupToken(it ?: return@Observer)
        })

        tfaConfirmationViewModel.liveTfaConfirmation.observe(this, Observer {
            renderTfaConfirmation(it ?: return@Observer)
        })
        tfaConfirmationViewModel.liveTfaChangeConfirmation.observe(this, Observer {
            renderTfaConfirmation(it ?: return@Observer)
        })
    }

    private fun setupListeners() {
        nextButton.setOnClickListener {
            subscribeForLiveData()
            if (tfaInputView.hasValidInput()) {
                tfaConfirmationViewModel.confirmTfaRegistration(tfaInputView.trimmedText)
            }
        }

        copyButton.setOnClickListener {
            tfaConfirmationViewModel.fetchTfaSecret()

            //TODO: TFA secreed need to get with live data
            context?.let { it1 -> GeneralUtils.copyToClipboard(it1, LsPrefs.tfaSecret) }
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
                authActivity.navigate(R.id.to_mnemonic_screen)
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
    }

    companion object {

        const val TAG = "TfaConfirmationFragment"

        fun newInstance() = TfaConfirmationFragment()
    }
}
