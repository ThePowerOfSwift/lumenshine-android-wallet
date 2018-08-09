package com.soneso.lumenshine.presentation.accounts


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.soneso.lumenshine.R
import com.soneso.lumenshine.presentation.general.SgFragment


/**
 * A simple [Fragment] subclass.
 */
class WalletsFragment : SgFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_accounts, container, false)


    companion object {
        const val TAG = "WalletsFragment"

        fun newInstance() = WalletsFragment()
    }
}
