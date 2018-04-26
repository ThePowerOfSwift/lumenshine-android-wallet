package com.soneso.stellargate.presentation.auth


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.soneso.stellargate.R
import com.soneso.stellargate.model.dto.DataStatus
import kotlinx.android.synthetic.main.fragment_registration.*


/**
 * A simple [Fragment] subclass.
 */
class RegistrationFragment : AuthFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        regViewModel = ViewModelProviders.of(authActivity, viewModelFactory)[RegistrationViewModel::class.java]
    }

    private lateinit var regViewModel: RegistrationViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_registration, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeForLiveData()

        regViewModel.refreshSalutations()
        sign_in_button.setOnClickListener { replaceFragment(LoginFragment.newInstance(), LoginFragment.TAG) }

        email_registration_button.setOnClickListener {
            attemptRegistration()
        }
    }

    private fun subscribeForLiveData() {
        regViewModel.liveError.observe(this, Observer {
            showErrorSnackbar(it)
        })

        regViewModel.liveSalutations.observe(this, Observer {
            val salutations = it ?: return@Observer
            renderSalutations(salutations)
        })
    }

    private fun renderSalutations(salutations: List<String>) {
        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item)
        adapter.clear()
        adapter.addAll(salutations)
        salutation_spinner.adapter = adapter
    }

    private fun attemptRegistration() {

        if (!isValidForm()) {
            return
        }

        val dataProvider = regViewModel.createAccount(email.trimmedText, password.trimmedText)
        dataProvider.liveStatus.observe(this, Observer {
            val status = it ?: return@Observer

            when (status) {
                DataStatus.SUCCESS -> {
                    progress_bar.visibility = View.GONE
                    val userLogin = dataProvider.data!!
                    replaceFragment(TfaRegistrationFragment.newInstance(userLogin.token2fa), TfaRegistrationFragment.TAG)
                }
                DataStatus.ERROR -> {
                    progress_bar.visibility = View.GONE
                    email_registration_button.visibility = View.VISIBLE
                    showErrorSnackbar(dataProvider.error)
                }
                DataStatus.LOADING -> {
                    email_registration_button.visibility = View.INVISIBLE
                    progress_bar.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun isValidForm() =
            email.hasValidInput()
                    && password.hasValidInput()

    companion object {
        const val TAG = "RegistrationFragment"

        fun newInstance() = RegistrationFragment()
    }
}
