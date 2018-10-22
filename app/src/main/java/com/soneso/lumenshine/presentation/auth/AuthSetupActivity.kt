package com.soneso.lumenshine.presentation.auth

import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import com.google.android.material.navigation.NavigationView
import com.soneso.lumenshine.R
import com.soneso.lumenshine.model.entities.RegistrationStatus
import com.soneso.lumenshine.presentation.MainActivity
import kotlinx.android.synthetic.main.activity_base_auth.*

class AuthSetupActivity : BaseAuthActivity() {

    override val tabLayoutId: Int
        get() = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupDrawer()
        subscribeToLiveData()
    }

    private fun subscribeToLiveData() {
        authViewModel.liveRegistrationStatus.observe(this, Observer {
            renderRegistrationStatus(it)
        })
    }

    private fun renderRegistrationStatus(s: RegistrationStatus?) {

        val status = s ?: return
        when {
            !status.tfaConfirmed -> {
                navigate(R.id.to_confirm_tfa_screen)
            }
            !status.mailConfirmed -> {
            }
            !status.mnemonicConfirmed -> {
                navigate(R.id.to_mnemonic_screen)
            }
            authViewModel.isFingerprintFlow -> {
                finishAffinity()
                MainActivity.startInstanceWithFingerprintSetup(this)
            }
        }
    }

    private fun setupDrawer() {
        drawerView.inflateMenu(R.menu.drawer_auth_setup)
        val navItemListener = NavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.logout_item -> authViewModel.logout()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            return@OnNavigationItemSelectedListener true
        }
        drawerView.setNavigationItemSelectedListener(navItemListener)
    }
}