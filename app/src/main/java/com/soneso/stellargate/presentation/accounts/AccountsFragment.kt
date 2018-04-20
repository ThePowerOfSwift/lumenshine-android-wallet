package com.soneso.stellargate.presentation.accounts


import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.soneso.stellargate.R
import com.soneso.stellargate.presentation.general.SgFragment
import com.soneso.stellargate.presentation.util.displayQrCode
import kotlinx.android.synthetic.main.fragment_accounts.*


/**
 * A simple [Fragment] subclass.
 */
class AccountsFragment : SgFragment() {

    //    @Inject
    lateinit var accountsViewModel: AccountsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        appComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_accounts, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accountsViewModel.liveAccountDetails.observe(this, Observer {
            val details = it ?: return@Observer
            qr_code_view.displayQrCode(details.accountId)
            account_view.text = details.balance
        })

//        accountsViewModel.createAccount()
    }


    companion object {
        const val TAG = "AccountsFragment"

        fun newInstance() = AccountsFragment()
    }
}
