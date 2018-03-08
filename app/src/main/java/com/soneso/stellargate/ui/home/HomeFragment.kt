package com.soneso.stellargate.ui.home


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.soneso.stellargate.R
import com.soneso.stellargate.ui.SgFragment


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : SgFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_home, container, false)


    companion object {
        const val TAG = "HomeFragment"

        fun newInstance() = HomeFragment()
    }
}
