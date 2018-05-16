package com.soneso.stellargate.presentation.settings


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.soneso.stellargate.R
import com.soneso.stellargate.presentation.general.SgFragment

/**
 * A simple [SgFragment] subclass.
 *
 */
class SettingsFragment : SgFragment() {

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[SettingsViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_settings, container, false)


    companion object {

        const val TAG = "SettingsFragment"

        fun newInstance() = SettingsFragment()
    }
}
