package com.soneso.lumenshine.presentation.auth


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.soneso.lumenshine.R
import com.soneso.lumenshine.model.entities.RegistrationInfo
import com.soneso.lumenshine.networking.dto.exceptions.ServerException
import com.soneso.lumenshine.util.Resource
import kotlinx.android.synthetic.main.fragment_finger_print.*


/**
 * A simple [Fragment] subclass.
 *
 */
class FingerPrintFragment : AuthFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_finger_print, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        subscribeForLiveData()
    }

    private fun setupListeners() {

        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })
        submit_button.setOnClickListener { attemptLogin() }
    }

    private fun subscribeForLiveData() {
        // TODO: cristi.paval, 8/27/18 - subscribe here accordingly
    }

    private fun showLoadingButton(loading: Boolean) {
        if (loading) {
            progress_bar.visibility = View.VISIBLE
            submit_button.visibility = View.INVISIBLE
        } else {
            progress_bar.visibility = View.GONE
            submit_button.visibility = View.VISIBLE
        }
    }

    private fun renderRegistrationStatus(resource: Resource<RegistrationInfo, ServerException>) {

        when (resource.state) {
            Resource.LOADING -> {

                showLoadingButton(true)
            }
            Resource.FAILURE -> {

                showLoadingButton(false)
                handleError(resource.failure())
            }
            else -> {

                showLoadingButton(false)
            }
        }
    }

    /**
     * handling login response errors
     */
    private fun handleError(e: ServerException) {
        // TODO: cristi.paval, 8/25/18 - error handling here
//        val error = e ?: return
//
//        when (error.errorCode) {
//            ErrorCodes.LOGIN_EMAIL_NOT_EXIST -> {
//                showErrorSnackbar(error)
//            }
//            ErrorCodes.LOGIN_INVALID_2FA -> {
//                showErrorSnackbar(error)
//            }
//            ErrorCodes.LOGIN_WRONG_PASSWORD -> {
//                password.error = if (error.errorResId == 0) error.message!! else getString(error.errorResId)
//            }
//            else -> {
//                showErrorSnackbar(error)
//            }
//        }
    }

    private fun attemptLogin() {

//        val username = authViewModel.liveLastUsername.value ?: return
//        val tfaCode = OtpProvider.currentTotpCode(credentials.tfaSecret.decodeBase32()) ?: return
//
//        authViewModel.loginAndFingerprintSetup(credentials.username, password.trimmedText, tfaCode)
    }

    companion object {

        const val TAG = "FingerPrintFragment"
        private const val FRAGMENT_TYPE = "fragment_type"

        fun newInstance(fragmentType: FingerPrintFragment.FingerprintFragmentType): FingerPrintFragment {

            val instance = FingerPrintFragment()
            val args = Bundle()
            args.putSerializable(FRAGMENT_TYPE, fragmentType)
            instance.arguments = args
            return instance
        }
    }

    enum class FingerprintFragmentType {
        FINGERPRINT, FACE_ID
    }


}
