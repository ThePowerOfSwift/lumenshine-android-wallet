package com.soneso.lumenshine.presentation.auth

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.View
import com.soneso.lumenshine.R
import com.soneso.lumenshine.model.entities.RegistrationInfo
import com.soneso.lumenshine.presentation.MainActivity
import com.soneso.lumenshine.presentation.general.SgActivity
import com.soneso.lumenshine.presentation.util.hasFingerPrintSensor
import com.soneso.lumenshine.presentation.util.showInfoDialog
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.app_bar_login.*
import kotlinx.android.synthetic.main.nav_header_main.*

/**
 * A login screen that offers login via email/password.
 */
class AuthActivity : SgActivity() {


    lateinit var authViewModel: AuthViewModel
        private set
    lateinit var useCase: UseCase
        private set

    private lateinit var moreDialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(login_toolbar)
        title = ""

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, login_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        setupNavigationView()

        authViewModel = ViewModelProviders.of(this, viewModelFactory)[AuthViewModel::class.java]
        useCase = intent?.getSerializableExtra(EXTRA_USE_CASE) as? UseCase ?: UseCase.AUTH

        startPage()
        setupMoreDialog()
        initTabView()

        drawer_layout.post {
            subscribeForLiveData()
        }
    }

    private fun setupNavigationView() {

        val navItemListener = NavigationView.OnNavigationItemSelectedListener { item ->
            clearButtons()
            // Handle navigation view item clicks here.
            when (item.itemId) {
                R.id.nav_logout -> {

                }
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
                    this.showInfoDialog()
                }
                R.id.nav_help -> {
                    this.showInfoDialog()
                }
            }

            drawer_layout.closeDrawer(GravityCompat.START)
            return@OnNavigationItemSelectedListener true
        }
        nav_view.setNavigationItemSelectedListener(navItemListener)

        val homeItem = nav_view.menu.getItem(0)
        homeItem.isChecked = true
        navItemListener.onNavigationItemSelected(homeItem)
    }

    /**
     * unchecks all the "tab" buttons
     */
    private fun clearButtons() {
        tab_login.isChecked = false
        tab_sign_up.isChecked = false
        tab_more.isChecked = false
    }

    /**
     * checks one menu item in navigationDrawer
     */
    private fun selectMenuItem(menuItem: Int) {
        val item = nav_view.menu.findItem(menuItem)
        item.isChecked = true
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
        nav_view.menu.clear()
        nav_view.inflateMenu(R.menu.activity_login_drawer_scenario_2)
        selectMenuItem(R.id.nav_home)
    }

    /**
     * sets menu for navigation drawer when nothing is saved(login scenario 1)
     */
    private fun setLoginScenario1Menu() {
        nav_view.menu.clear()
        nav_view.inflateMenu(R.menu.activity_login_drawer_scenario_1)
    }

    private fun setLoginSetUpMenu() {
        nav_view.menu.clear()
        nav_view.inflateMenu(R.menu.activity_login_drawer_set_up)
    }

    private fun showSetUpView() {
        set_up_view.visibility = View.VISIBLE
        login_step_1_tabs.visibility = View.GONE
        login_step_2_tabs.visibility = View.GONE
        setLoginSetUpMenu()
    }

    private fun showUserNotSavedView() {
        welcome_text.setText(R.string.welcome)
        nav_header_username.setText(R.string.not_logged_in)
        welcome_user_email_text.visibility = View.INVISIBLE
        login_step_2_tabs.visibility = View.GONE
        login_step_1_tabs.visibility = View.VISIBLE
        set_up_view.visibility = View.GONE
        setLoginScenario1Menu()
    }

    private fun showUserSavedView(username: String) {
        nav_header_username.text = username
        welcome_text.setText(R.string.welcome_back)
        welcome_user_email_text.visibility = View.VISIBLE
        welcome_user_email_text.text = username
        login_step_1_tabs.visibility = View.GONE
        login_step_2_tabs.visibility = View.VISIBLE
        set_up_view.visibility = View.GONE
        replaceFragment(PasswordFragment.newInstance(), PasswordFragment.TAG)
        setLoginScenario2Menu()

    }

    private fun renderRegistrationStatus(s: RegistrationInfo?) {

        val status = s ?: return
        when {
            !status.tfaConfirmed -> {
                showSetUpView()
                replaceFragment(TfaConfirmationFragment.newInstance(), TfaConfirmationFragment.TAG)
            }
            !status.mailConfirmed -> {
                showSetUpView()
                replaceFragment(MailConfirmationFragment.newInstance(), MailConfirmationFragment.TAG)
            }
            !status.mnemonicConfirmed -> {
                showSetUpView()
                replaceFragment(MnemonicFragment.newInstance(), MnemonicFragment.TAG)
            }
//            status.fingerprintSetupRequested -> {
//                finishAffinity()
//                MainActivity.startInstanceWithFingerprintSetup(this)
//            }
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

    /**
     * setting up the More BottomSheetDialog
     */
    private fun setupMoreDialog() {
        moreDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_more, null)
        moreDialog.setContentView(view)
    }

    fun replaceFragment(fragment: AuthFragment, tag: String) {
        val myFragment = supportFragmentManager.findFragmentByTag(tag)
        if (myFragment == null)
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment, tag)
                    .commit()
    }

    /**
     * setting up the tab view
     */
    private fun initTabView() {
        val tabClickListener = View.OnClickListener { view ->
            when (view) {
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
                    moreDialog.show()
                }
                tab_logout -> {
                    tab_login.isChecked = true
                    tab_sign_up.isChecked = false
                    tab_more.isChecked = false
                    // TODO: cristi.paval, 8/25/18 - implement logout accordingly
//                    SgPrefs.removeUserCrendentials()
//                    authViewModel.refreshLastUserCredentials()
//                    replaceFragment(LoginFragment.newInstance(), LoginFragment.TAG)
                }
                tab_fingerprint -> {
                    tab_logout.isChecked = false
                    tab_home.isChecked = false
                    tab_fingerprint.isChecked = true
                    replaceFragment(FingerPrintFragment.newInstance(FingerPrintFragment.FingerprintFragmentType.FINGERPRINT), FingerPrintFragment.TAG)
                    selectMenuItem(R.id.nav_fingerprit)
                }
                tab_home -> {
                    tab_logout.isChecked = false
                    tab_home.isChecked = true
                    tab_fingerprint.isChecked = false
                    replaceFragment(PasswordFragment.newInstance(), PasswordFragment.TAG)
                    selectMenuItem(R.id.nav_fingerprit)
                }
            }
        }
        tab_login.setOnClickListener(tabClickListener)
        tab_sign_up.setOnClickListener(tabClickListener)
        tab_more.setOnClickListener(tabClickListener)
        tab_login.isChecked = true
        tab_home.setOnClickListener(tabClickListener)
        tab_logout.setOnClickListener(tabClickListener)
        tab_fingerprint.setOnClickListener(tabClickListener)

        if (this.hasFingerPrintSensor().not())
            tab_fingerprint.visibility = View.GONE
        else
            tab_fingerprint.visibility = View.VISIBLE

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
