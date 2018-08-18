package com.soneso.lumenshine.presentation.auth


import android.app.Activity
import android.arch.lifecycle.Observer
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.soneso.lumenshine.R
import com.soneso.lumenshine.domain.data.Country
import com.soneso.lumenshine.domain.data.ErrorCodes
import com.soneso.lumenshine.domain.data.RegistrationStatus
import com.soneso.lumenshine.domain.data.SgError
import com.soneso.lumenshine.presentation.general.SgViewState
import com.soneso.lumenshine.presentation.general.State
import com.soneso.lumenshine.presentation.util.hideProgressDialog
import com.soneso.lumenshine.presentation.util.showInfoDialog
import com.soneso.lumenshine.presentation.util.showProgressDialog
import kotlinx.android.synthetic.main.fragment_registration.*


/**
 * A simple [Fragment] subclass.
 */
class RegistrationFragment : AuthFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_registration, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeForLiveData()

//        authViewModel.refreshSalutations()
//        authViewModel.refreshCountries()

        setupListeners()
        password_info_button.setOnClickListener {
            (activity as Activity).showInfoDialog(R.string.password_requirements, R.layout.info_password)
        }
    }

    private fun subscribeForLiveData() {
//        authViewModel.liveSalutations.observe(this, Observer {
//            renderSalutations(it ?: return@Observer)
//        })
//
//        authViewModel.liveCountries.observe(this, Observer {
//            renderCountries(it ?: return@Observer)
//        })

        authViewModel.liveRegistrationStatus.observe(this, Observer {
            renderRegistrationStatus(it ?: return@Observer)
        })
    }

    private fun setupListeners() {

        sign_in_button.setOnClickListener { replaceFragment(LoginFragment.newInstance(), LoginFragment.TAG) }
        email_registration_button.setOnClickListener { attemptRegistration() }
        salutation_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d(TAG, "Selected position: nothing")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val textView = view as? TextView ?: return
                textView.setTextColor(Color.BLACK)
            }
        }
    }

    private fun renderSalutations(viewState: SgViewState<List<String>>) {
        when (viewState.state) {
            State.LOADING -> {

            }
            State.READY -> {
                val adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item)
                adapter.clear()
                adapter.addAll(viewState.data!!)
                salutation_spinner.adapter = adapter
            }
            State.ERROR -> {
                showErrorSnackbar(viewState.error)
            }
        }
    }

    private fun renderCountries(viewState: SgViewState<List<Country>>) {
        when (viewState.state) {

            State.LOADING -> {

                showLoadingButton(true)
            }
            State.READY -> {

                showLoadingButton(false)

                val adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item)
                adapter.clear()
                for (country in viewState.data!!) {
                    adapter.add(country.name)
                }
                country_spinner.adapter = adapter
            }

            State.ERROR -> {

                showLoadingButton(false)
                showErrorSnackbar(viewState.error)
            }
        }
    }

    private fun renderRegistrationStatus(viewState: SgViewState<RegistrationStatus>) {

        when (viewState.state) {

            State.LOADING -> {

                context?.showProgressDialog()
            }
            State.ERROR -> {

                hideProgressDialog()
                handleError(viewState.error)
            }
            else -> {

                hideProgressDialog()
            }
        }
    }

    /**
     * handling login response errors
     */
    private fun handleError(e: SgError?) {
        val error = e ?: return

        when (error.errorCode) {
            ErrorCodes.SIGNUP_EMAIL_ALREADY_EXIST -> {
                email.error = if (error.errorResId == 0) error.message!! else getString(error.errorResId)
            }
            else -> {
                showErrorSnackbar(error)
            }
        }
    }

    private fun showLoadingButton(loading: Boolean) {
        if (loading) {
            progress_bar.visibility = View.VISIBLE
            email_registration_button.visibility = View.INVISIBLE
        } else {
            progress_bar.visibility = View.GONE
            email_registration_button.visibility = View.VISIBLE
        }
    }

    private fun attemptRegistration() {

        if (!isValidForm()) {
            return
        }

        authViewModel.createAccount(
                email.trimmedText,
                password.trimmedText,
                country_spinner.selectedItemPosition
        )
    }

    private fun isValidForm() =
            email.hasValidInput()
                    && password.isValidPassword()
                    && isPasswordMatch()


    private fun isPasswordMatch(): Boolean {
        val match = password.trimmedText == password_confirmation.trimmedText
        if (!match) {
            password.error = getString(R.string.password_not_match)
            password_confirmation.error = getString(R.string.password_not_match)
        }
        return match
    }

    companion object {
        const val TAG = "RegistrationFragment"

        fun newInstance() = RegistrationFragment()
    }
}
