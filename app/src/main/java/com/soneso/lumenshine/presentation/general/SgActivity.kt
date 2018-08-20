package com.soneso.lumenshine.presentation.general

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import com.soneso.lumenshine.R
import com.soneso.lumenshine.SgApp
import com.soneso.lumenshine.domain.data.SgError

/**
 * Base activity for class.
 * Created by cristi.paval on 3/12/18.
 */
@SuppressLint("Registered")
open class SgActivity : AppCompatActivity() {

    val sgApp: SgApp
        get() = application as SgApp

    lateinit var viewModelFactory: SgViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelFactory = SgViewModelFactory(sgApp.appComponent)
    }

    fun showSnackbar(text: CharSequence) {
        val view = findViewById<ViewGroup>(android.R.id.content).getChildAt(0) ?: return
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                .setAction(R.string.ok, null)
                .show()
    }

    fun showErrorSnackbar(e: SgError?) {
        val error = e ?: return
        val view = findViewById<ViewGroup>(android.R.id.content).getChildAt(0) ?: return
        val snackbar = if (error.errorResId > 0) Snackbar.make(view, error.errorResId, Snackbar.LENGTH_LONG) else Snackbar.make(view, error.message!!, Snackbar.LENGTH_LONG)
        snackbar.setAction(R.string.ok, null)
                .show()
    }
}