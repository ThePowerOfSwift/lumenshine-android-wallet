package com.soneso.stellargate.presentation.general

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import com.soneso.stellargate.SgApp

/**
 * Base activity for class.
 * Created by cristi.paval on 3/12/18.
 */
@SuppressLint("Registered")
open class SgActivity : AppCompatActivity() {

    val sgApp: SgApp
        get() = application as SgApp
}