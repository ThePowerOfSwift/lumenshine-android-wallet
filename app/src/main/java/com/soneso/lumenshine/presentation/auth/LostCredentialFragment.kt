package com.soneso.lumenshine.presentation.auth


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.soneso.lumenshine.R
import com.soneso.lumenshine.util.LsException
import com.soneso.lumenshine.util.Resource
import kotlinx.android.synthetic.main.fragment_lost_credential.*

/**
 * A simple [Fragment] subclass.
 */
class LostCredentialFragment : AuthFragment() {

    private lateinit var credential: Credential

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        credential = arguments?.getSerializable(ARG_CREDENTIAL) as? Credential ?: Credential.PASSWORD
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_lost_credential, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        subscribeForLiveData()
    }

    private fun setupListeners() {

        submit_button.setOnClickListener {

            when (credential) {

                Credential.PASSWORD -> {
                    authViewModel.requestPasswordResetEmail(email_input.trimmedText)
                }
                Credential.TFA -> {
                    authViewModel.requestTfaResetEmail(email_input.trimmedText)
                }
            }
        }
    }

    private fun subscribeForLiveData() {

        authViewModel.liveCredentialResetEmail.observe(this, Observer {
            renderCredentialReset(it ?: return@Observer)
        })
    }

    private fun renderCredentialReset(resource: Resource<Boolean, LsException>) {

        when (resource.state) {

            Resource.LOADING -> {
            }
            Resource.SUCCESS -> {

                showSnackbar("Mail sent!")
                replaceFragment(LoginFragment.newInstance(), LoginFragment.TAG)
            }
            Resource.FAILURE -> {

                showErrorSnackbar(resource.failure())
            }
        }
    }

    companion object {

        const val TAG = "LostCredentialFragment"
        private const val ARG_CREDENTIAL = "$TAG.ARG_CREDENTIAL"

        fun newInstance(credential: Credential): LostCredentialFragment {

            val instance = LostCredentialFragment()
            val args = Bundle()
            args.putSerializable(ARG_CREDENTIAL, credential)
            instance.arguments = args
            return instance
        }
    }

    enum class Credential {
        PASSWORD, TFA
    }
}
