package com.soneso.stellargate.presentation.general

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.soneso.stellargate.SgApp
import com.soneso.stellargate.di.AppComponent
import javax.inject.Inject

/**
 * Base activity for class.
 * Created by cristi.paval on 3/12/18.
 */
@SuppressLint("Registered")
open class SgActivity : AppCompatActivity() {

    val sgApp: SgApp
        get() = application as SgApp

    val appComponent: AppComponent
        get() = sgApp.appComponent

    @Inject
    lateinit var viewModelFactory: SgViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appComponent.inject(this)
    }
}