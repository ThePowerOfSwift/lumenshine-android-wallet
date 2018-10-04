package com.soneso.lumenshine.presentation.general

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import com.soneso.lumenshine.LsApp
import com.soneso.lumenshine.R
import com.soneso.lumenshine.util.LsException

/**
 * Base activity for class.
 * Created by cristi.paval on 3/12/18.
 */
@SuppressLint("Registered")
open class LsActivity : AppCompatActivity() {

    val lsApp: LsApp
        get() = application as LsApp

    lateinit var viewModelFactory: SgViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelFactory = SgViewModelFactory(lsApp.appComponent)
    }

    fun showSnackbar(text: CharSequence) {

        val view = findViewById<ViewGroup>(android.R.id.content).getChildAt(0) ?: return
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                .setAction(R.string.ok, null)
                .show()
    }

    fun showErrorSnackbar(e: LsException?) {

        val message = e?.throwable?.message ?: getString(R.string.unknown_error)
        showSnackbar(message)
    }
}