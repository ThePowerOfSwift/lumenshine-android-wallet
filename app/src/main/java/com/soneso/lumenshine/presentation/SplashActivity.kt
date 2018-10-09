package com.soneso.lumenshine.presentation

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.soneso.lumenshine.R
import com.soneso.lumenshine.presentation.auth.AuthActivity
import com.soneso.lumenshine.presentation.general.LsActivity

class SplashActivity : LsActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        subscribeToLiveData()
    }

    private fun subscribeToLiveData() {

        val viewModel = ViewModelProviders.of(this, viewModelFactory)[SplashViewModel::class.java]
        viewModel.liveIsUserLoggedIn.observe(this, Observer {
            AuthActivity.startInstance(this, it)
            finish()
        })
    }
}
