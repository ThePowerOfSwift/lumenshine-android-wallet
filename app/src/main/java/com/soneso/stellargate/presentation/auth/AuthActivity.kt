package com.soneso.stellargate.presentation.auth

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.internal.NavigationMenu
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.view.View
import com.soneso.stellargate.R
import com.soneso.stellargate.domain.data.RegistrationStatus
import com.soneso.stellargate.presentation.MainActivity
import com.soneso.stellargate.presentation.general.SgActivity
import com.soneso.stellargate.presentation.general.SgViewState
import com.soneso.stellargate.presentation.general.State
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.app_bar_login.*

/**
 * A login screen that offers login via email/password.
 */
class AuthActivity : SgActivity(), NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {


    lateinit var authViewModel: AuthViewModel
        private set
    lateinit var useCase: UseCase
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(login_toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, login_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val homeItem = nav_view.menu.getItem(0)
        homeItem.isChecked = true
        onNavigationItemSelected(homeItem)

        authViewModel = ViewModelProviders.of(this, viewModelFactory)[AuthViewModel::class.java]

        title = ""

        subscribeForLiveData()
        useCase = intent?.getSerializableExtra(EXTRA_USE_CASE) as? UseCase ?: UseCase.AUTH

        startPage()
        initTabView()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        clearButtons()
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_login -> {
                replaceFragment(LoginFragment.newInstance(), LoginFragment.TAG)
                tab_login.isChecked = true
            }
            R.id.nav_sign_up -> {
                replaceFragment(RegistrationFragment.newInstance(), RegistrationFragment.TAG)
                tab_sign_up.isChecked = true
            }
            R.id.nav_lost_password -> {
                replaceFragment(LostCredentialFragment.newInstance(LostCredentialFragment.Credential.PASSWORD), LostCredentialFragment.TAG)
            }
            R.id.nav_lost_tfa_secret -> {
                replaceFragment(LostCredentialFragment.newInstance(LostCredentialFragment.Credential.TFA), LostCredentialFragment.TAG)
            }
            R.id.nav_import_mnemonic -> {

            }
            R.id.nav_about -> {

            }
            R.id.nav_help -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onClick(p0: View?) {
        when (p0) {
            tab_login -> {
                tab_login.isChecked = true
                tab_sign_up.isChecked = false
                tab_more.isChecked = false
                replaceFragment(LoginFragment.newInstance(), LoginFragment.TAG)
                selectMenuItem(R.id.nav_login)
            }
            tab_sign_up -> {
                tab_login.isChecked = false
                tab_sign_up.isChecked = true
                tab_more.isChecked = false
                replaceFragment(RegistrationFragment.newInstance(), RegistrationFragment.TAG)
                selectMenuItem(R.id.nav_sign_up)
            }
            tab_more -> {
                tab_login.isChecked = false
                tab_sign_up.isChecked = false
                tab_more.isChecked = true
            }
        }
    }

    /**
     * unchecks all the "tab" buttons
     */
    private fun clearButtons() {
        tab_login.isChecked = false
        tab_sign_up.isChecked = false
        tab_more.isChecked = false
    }

    private fun selectMenuItem(menuItem: Int) {
        val item = nav_view.menu.findItem(menuItem)
        item.isChecked = true
        onNavigationItemSelected(item)
    }

    private fun startPage() {

        when (useCase) {
            UseCase.AUTH -> {
                replaceFragment(LoginFragment.newInstance(), LoginFragment.TAG)
            }
            UseCase.CONFIRM_TFA_SECRET_CHANGE -> {
                replaceFragment(TfaConfirmationFragment.newInstance(), TfaConfirmationFragment.TAG)
            }
        }
    }

    private fun subscribeForLiveData() {

        authViewModel.liveRegistrationStatus.observe(this, Observer {
            renderRegistrationStatus(it ?: return@Observer)
        })
    }

    private fun renderRegistrationStatus(viewState: SgViewState<RegistrationStatus>) {

        when (viewState.state) {
            State.READY -> {

                handleRegistrationStatus(viewState.data!!)
            }
            else -> {
                // cristi.paval, 5/3/18 - this should be rendered in subclasses
            }
        }
    }

    private fun handleRegistrationStatus(status: RegistrationStatus) {

        when {
            !status.tfaConfirmed -> {

                replaceFragment(TfaConfirmationFragment.newInstance(), TfaConfirmationFragment.TAG)
            }
            !status.emailConfirmed -> {

                replaceFragment(MailConfirmationFragment.newInstance(), MailConfirmationFragment.TAG)
            }
            !status.mnemonicConfirmed -> {

                replaceFragment(MnemonicFragment.newInstance(), MnemonicFragment.TAG)
            }
            else -> {

                handleRegistrationCompleted()
            }
        }
    }

    private fun handleRegistrationCompleted() {

        when (useCase) {
            UseCase.AUTH -> {
                finishAffinity()
                MainActivity.startInstance(this)
            }
            UseCase.CONFIRM_TFA_SECRET_CHANGE -> {
                finish()
            }
        }
    }


    fun replaceFragment(fragment: AuthFragment, tag: String) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .commit()
    }

    private fun initTabView() {
        tab_login.setOnClickListener(this)
        tab_sign_up.setOnClickListener(this)
        tab_more.setOnClickListener(this)
        tab_login.isChecked = true
    }

    companion object {

        const val TAG = "AuthActivity"
        private const val EXTRA_USE_CASE = "$TAG.EXTRA_USE_CASE"

        fun startInstance(context: Context, page: UseCase) {
            val intent = Intent(context, AuthActivity::class.java)
            intent.putExtra(EXTRA_USE_CASE, page)
            context.startActivity(intent)
        }
    }

    enum class UseCase {
        AUTH, CONFIRM_TFA_SECRET_CHANGE
    }
}
