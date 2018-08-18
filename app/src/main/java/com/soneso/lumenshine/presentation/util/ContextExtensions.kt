package com.soneso.lumenshine.presentation.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.soneso.lumenshine.R


/**
 * Util class.
 * Created by cristi.paval on 3/16/18.
 */

fun Context.forwardToBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)
    if (isIntentSafe(intent)) {
        startActivity(intent)
    }
}

fun Context.isIntentSafe(intent: Intent): Boolean {
    val activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    return activities.size > 0
}

fun Activity.showInfoDialog() {
    val fragmentTransaction = this.fragmentManager.beginTransaction()
    val infoDialog = InfoDialog.newInstance(R.string.info_dialog, R.drawable.ic_error_outline)
    infoDialog.setViewBuilder(object : InfoDialog.ViewBuilder {
        override fun createView(context: Context, inflater: LayoutInflater): View {
            return inflater.inflate(R.layout.info, null)
        }
    })
    infoDialog.show(fragmentTransaction, InfoDialog.TAG)
}

/**
 * show an fullscreen information dialog
 *
 * @param titleResId the dialog title resource id
 * @param contentResId hte content layout resource id
 * @param (optional) the dialog icon id
 */
fun Activity.showInfoDialog(titleResId: Int, contentResId: Int, iconResId: Int = 0) {
    val fragmentTransaction = this.fragmentManager.beginTransaction()
    val infoDialog = InfoDialog.newInstance(titleResId, iconResId)
    infoDialog.setViewBuilder(object : InfoDialog.ViewBuilder {
        override fun createView(context: Context, inflater: LayoutInflater): View {
            return inflater.inflate(contentResId, null)
        }
    })
    infoDialog.show(fragmentTransaction, InfoDialog.TAG)
}


/**
 * Shows progress dialog
 */
fun Context.showProgressDialog() {
    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
    val view = LayoutInflater.from(this).inflate(R.layout.dialog_progress, null)
    builder.setView(view)
    builder.setCancelable(false)

    dialog = builder.create()
    dialog!!.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog!!.window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

    dialog!!.show()
}

var dialog: AlertDialog? = null

/**
 * Hides the progress dialog
 */
fun hideProgressDialog() {
    dialog?.let { if (it.isShowing) it.dismiss() }
}