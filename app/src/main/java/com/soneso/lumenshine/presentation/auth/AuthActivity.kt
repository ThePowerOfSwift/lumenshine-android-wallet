package com.soneso.lumenshine.presentation.auth

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationView
import com.soneso.lumenshine.R
import com.soneso.lumenshine.model.entities.RegistrationStatus
import com.soneso.lumenshine.presentation.MainActivity
import com.soneso.lumenshine.presentation.general.LsActivity
import com.soneso.lumenshine.presentation.util.showInfoDialog
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.app_bar_login.*
import kotlinx.android.synthetic.main.nav_header_main.*

/**
 * A login screen that offers login via email/password.
 */
class AuthActivity : LsActivity() {

    private lateinit var navController: NavController

    lateinit var authViewModel: AuthViewModel
        private set
    private lateinit var tabClickListener: View.OnClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setSupportActionBar(authToolbar.apply { title = "" })
        setupTabs()
        setupDrawer()
        setupNavigation()

        authViewModel = ViewModelProviders.of(this, viewModelFactory)[AuthViewModel::class.java]

        drawerLayout.post {
            subscribeForLiveData()
        }
    }

    private fun setupNavigation() {
        navController = NavHostFragment.findNavController(navHostFragment)
        navController.addOnNavigatedListener { _, _ ->
            invalidateCurrentSelection()
        }
    }

    private fun invalidateCurrentSelection() {
        loginTab.isSelected = false
        signUpTab.isSelected = false
        moreTab.isSelected = false
        val destination = navController.currentDestination ?: return
        when (destination.id) {
            R.id.login_screen -> {
                loginTab.isSelected = true
                selectMenuItem(R.id.login_item)
            }
            R.id.register_screen -> {
                signUpTab.isSelected = true
                selectMenuItem(R.id.signup_item)
            }
        }
    }

    fun navigate(@IdRes resId: Int, args: Bundle? = null) {
        navController.navigate(resId, args)
    }

    private fun setupTabs() {
        tabClickListener = View.OnClickListener { view ->
            when (view.id) {
                R.id.loginTab -> navigate(R.id.to_login)
                R.id.signUpTab -> navigate(R.id.to_register)
                R.id.moreTab -> MoreDialog.showInstance(supportFragmentManager, tabClickListener)
                R.id.lostPassTab -> {
                    navigate(R.id.to_lost_credential, LostCredentialFragment.argForPassword())
                    selectMenuItem(R.id.lostpass_item)
                }
                R.id.lostTfaTab -> {
                    navigate(R.id.to_lost_credential, LostCredentialFragment.argForTfa())
                    selectMenuItem(R.id.losttfa_item)
                }
            }
        }
        loginTab.setOnClickListener(tabClickListener)
        signUpTab.setOnClickListener(tabClickListener)
        moreTab.setOnClickListener(tabClickListener)
        tab_home.setOnClickListener(tabClickListener)
        tab_logout.setOnClickListener(tabClickListener)
        tab_fingerprint.setOnClickListener(tabClickListener)

//        if (this.hasFingerPrintSensor().not())
//            tab_fingerprint.visibility = View.GONE
//        else
//            tab_fingerprint.visibility = View.VISIBLE
    }

    private fun setupDrawer() {
        val toggle = ActionBarDrawerToggle(this, drawerLayout, authToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        val navItemListener = NavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.logout_item -> {
                }
                R.id.login_item -> navigate(R.id.to_login)
                R.id.signup_item -> navigate(R.id.to_register)
                R.id.lostpass_item -> {
                    navigate(R.id.to_lost_credential, LostCredentialFragment.argForPassword())
                    selectMenuItem(R.id.lostpass_item)
                }
                R.id.losttfa_item -> {
                    navigate(R.id.to_lost_credential, LostCredentialFragment.argForTfa())
                    selectMenuItem(R.id.losttfa_item)
                }
                R.id.mnemonic_item -> {

                }
                R.id.nav_about -> {
                    this.showInfoDialog()
                }
                R.id.nav_help -> {
                    this.showInfoDialog()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            return@OnNavigationItemSelectedListener true
        }
        selectMenuItem(R.id.login_item)
        drawerView.setNavigationItemSelectedListener(navItemListener)
    }

    /**
     * checks one menu item in navigationDrawer
     */
    private fun selectMenuItem(menuItem: Int) {
        drawerView.menu.findItem(menuItem).isChecked = true
    }

    private fun subscribeForLiveData() {

        authViewModel.liveRegistrationStatus.observe(this, Observer {
            renderRegistrationStatus(it)
        })

        authViewModel.liveLastUsername.observe(this, Observer {
            renderLastUser(it ?: return@Observer)
        })
    }

    private fun renderLastUser(username: String) {

        if (username.isNotEmpty()) {
            showUserSavedView(username)
        } else {
            // TODO: cristi.paval, 8/25/18 - this is not working.
            showUserNotSavedView()
        }
    }

    /**
     * sets menu for navigation drawer when the user credentials are saved(login scenario 2)
     */
    private fun setLoginScenario2Menu() {
        drawerView.menu.clear()
        drawerView.inflateMenu(R.menu.activity_login_drawer_scenario_2)
        selectMenuItem(R.id.home_item)
    }

    /**
     * sets menu for navigation drawer when nothing is saved(login scenario 1)
     */
    private fun setLoginScenario1Menu() {
        drawerView.menu.clear()
        drawerView.inflateMenu(R.menu.activity_login_drawer_scenario_1)
    }

    private fun setLoginSetUpMenu() {
        drawerView.menu.clear()
        drawerView.inflateMenu(R.menu.activity_login_drawer_set_up)
    }

    private fun showSetUpView() {
        set_up_view.visibility = View.VISIBLE
        login_step_1_tabs.visibility = View.GONE
        login_step_2_tabs.visibility = View.GONE
        setLoginSetUpMenu()
    }

    private fun showUserNotSavedView() {
        welcomeView.setText(R.string.welcome)
        nav_header_username.setText(R.string.not_logged_in)
        usernameView.visibility = View.INVISIBLE
        login_step_2_tabs.visibility = View.GONE
        login_step_1_tabs.visibility = View.VISIBLE
        set_up_view.visibility = View.GONE
        setLoginScenario1Menu()
    }

    private fun showUserSavedView(username: String) {
        nav_header_username.text = username
        welcomeView.setText(R.string.welcome_back)
        usernameView.visibility = View.VISIBLE
        usernameView.text = username
        login_step_1_tabs.visibility = View.GONE
        login_step_2_tabs.visibility = View.VISIBLE
        set_up_view.visibility = View.GONE
//        replaceFragment(PasswordFragment.newInstance(), PasswordFragment.TAG)
        setLoginScenario2Menu()

    }

    private fun renderRegistrationStatus(s: RegistrationStatus?) {

        val status = s ?: return
        when {
            !status.tfaConfirmed -> {
                showSetUpView()
//                replaceFragment(TfaConfirmationFragment.newInstance(), TfaConfirmationFragment.TAG)
            }
            !status.mailConfirmed -> {
                showSetUpView()
//                replaceFragment(MailConfirmationFragment.newInstance(), MailConfirmationFragment.TAG)
            }
            !status.mnemonicConfirmed -> {
                showSetUpView()
//                replaceFragment(MnemonicFragment.newInstance(), MnemonicFragment.TAG)
            }
            authViewModel.isFingerprintFlow -> {
                finishAffinity()
                MainActivity.startInstanceWithFingerprintSetup(this)
            }
        }
    }
}
