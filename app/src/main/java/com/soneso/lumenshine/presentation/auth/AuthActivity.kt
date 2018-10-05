package com.soneso.lumenshine.presentation.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
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
    lateinit var useCase: UseCase
        private set

    private lateinit var moreDialog: com.google.android.material.bottomsheet.BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setSupportActionBar(authToolbar.apply { title = "" })
        setupDrawer()
        setupTabViews()
        setupNavigation()

        authViewModel = ViewModelProviders.of(this, viewModelFactory)[AuthViewModel::class.java]
        useCase = intent?.getSerializableExtra(EXTRA_USE_CASE) as? UseCase ?: UseCase.AUTH

//        startPage()
        setupMoreDialog()
        initTabView()

        drawerLayout.post {
            subscribeForLiveData()
        }
    }

    private fun setupNavigation() {
        navController = NavHostFragment.findNavController(navHostFragment)
    }

    private fun setupDrawer() {
        val toggle = ActionBarDrawerToggle(this, drawerLayout, authToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun setupTabViews() {

        val navItemListener = com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener { item ->
            clearButtons()
            // Handle navigation view item clicks here.
            when (item.itemId) {
                R.id.nav_logout -> {

                }
                R.id.nav_login -> {
                    replaceFragment(LoginFragment.newInstance(), LoginFragment.TAG)
                    loginTab.isSelected = true
                }
                R.id.nav_sign_up -> {
                    replaceFragment(RegistrationFragment.newInstance(), RegistrationFragment.TAG)
                    signUpTab.isSelected = true
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

            drawerLayout.closeDrawer(GravityCompat.START)
            return@OnNavigationItemSelectedListener true
        }
        drawerView.setNavigationItemSelectedListener(navItemListener)

        val homeItem = drawerView.menu.getItem(0)
        homeItem.isChecked = true
        navItemListener.onNavigationItemSelected(homeItem)
    }

    /**
     * unchecks all the "tab" buttons
     */
    private fun clearButtons() {
        loginTab.isSelected = false
        signUpTab.isSelected = false
        moreTab.isSelected = false
    }

    /**
     * checks one menu item in navigationDrawer
     */
    private fun selectMenuItem(menuItem: Int) {
        val item = drawerView.menu.findItem(menuItem)
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
        drawerView.menu.clear()
        drawerView.inflateMenu(R.menu.activity_login_drawer_scenario_2)
        selectMenuItem(R.id.nav_home)
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
        replaceFragment(PasswordFragment.newInstance(), PasswordFragment.TAG)
        setLoginScenario2Menu()

    }

    private fun renderRegistrationStatus(s: RegistrationStatus?) {

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
            authViewModel.isFingerprintFlow -> {
                finishAffinity()
                MainActivity.startInstanceWithFingerprintSetup(this)
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

    /**
     * setting up the More BottomSheetDialog
     */
    private fun setupMoreDialog() {
        moreDialog = com.google.android.material.bottomsheet.BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_more, null)
        moreDialog.setContentView(view)
    }

    fun replaceFragment(fragment: AuthFragment, tag: String) {
//        val myFragment = supportFragmentManager.findFragmentByTag(tag)
//        if (myFragment == null)
//            supportFragmentManager
//                    .beginTransaction()
//                    .replace(R.id.fragment_container, fragment, tag)
//                    .commit()
    }

    /**
     * setting up the tab view
     */
    private fun initTabView() {
        val tabClickListener = View.OnClickListener { view ->
            when (view) {
                loginTab -> {
                    loginTab.isSelected = true
                    signUpTab.isSelected = false
                    moreTab.isSelected = false
                    replaceFragment(LoginFragment.newInstance(), LoginFragment.TAG)
                    selectMenuItem(R.id.nav_login)
                }
                signUpTab -> {
                    loginTab.isSelected = false
                    signUpTab.isSelected = true
                    moreTab.isSelected = false
                    replaceFragment(RegistrationFragment.newInstance(), RegistrationFragment.TAG)
                    selectMenuItem(R.id.nav_sign_up)
                }
                moreTab -> {
                    moreDialog.show()
                }
                tab_logout -> {
                    loginTab.isSelected = true
                    signUpTab.isSelected = false
                    moreTab.isSelected = false
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
        loginTab.setOnClickListener(tabClickListener)
        signUpTab.setOnClickListener(tabClickListener)
        moreTab.setOnClickListener(tabClickListener)
        loginTab.isSelected = true
        tab_home.setOnClickListener(tabClickListener)
        tab_logout.setOnClickListener(tabClickListener)
        tab_fingerprint.setOnClickListener(tabClickListener)

//        if (this.hasFingerPrintSensor().not())
//            tab_fingerprint.visibility = View.GONE
//        else
//            tab_fingerprint.visibility = View.VISIBLE

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
