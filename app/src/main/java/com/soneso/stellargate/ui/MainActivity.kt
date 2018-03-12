package com.soneso.stellargate.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.firebase.iid.FirebaseInstanceId
import com.soneso.stellargate.R
import com.soneso.stellargate.ui.home.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->

            copyFcmIdToClipboard()

            Snackbar.make(view, "Your FCM device id was copied to clipboard!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val homeItem = nav_view.menu.getItem(0)
        homeItem.isChecked = true
        onNavigationItemSelected(homeItem)
    }

    private fun copyFcmIdToClipboard() {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("FCM_ID", FirebaseInstanceId.getInstance().token)
        clipboard.primaryClip = clip
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                replaceFragment(HomeFragment.newInstance(), HomeFragment.TAG)
            }
            R.id.nav_accounts -> {

            }
            R.id.nav_transactions -> {

            }
            R.id.nav_promotions -> {

            }
            R.id.nav_settings -> {

            }
            R.id.nav_help -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun replaceFragment(fragment: SgFragment, tag: String) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, tag)
                .commit()
    }
}
