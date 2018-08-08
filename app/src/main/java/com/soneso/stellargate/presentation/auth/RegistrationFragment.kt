package com.soneso.stellargate.presentation.auth


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
import com.soneso.stellargate.R
import com.soneso.stellargate.domain.data.Country
import com.soneso.stellargate.domain.data.RegistrationStatus
import com.soneso.stellargate.presentation.general.SgViewState
import com.soneso.stellargate.presentation.general.State
import com.soneso.stellargate.presentation.util.showInfoDialog
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

                showLoadingButton(true)
            }
            State.ERROR -> {

                showLoadingButton(false)
                showErrorSnackbar(viewState.error)
            }
            else -> {

                showLoadingButton(false)
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
