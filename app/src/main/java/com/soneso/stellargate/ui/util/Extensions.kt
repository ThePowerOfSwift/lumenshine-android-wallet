package com.soneso.stellargate.ui.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri


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