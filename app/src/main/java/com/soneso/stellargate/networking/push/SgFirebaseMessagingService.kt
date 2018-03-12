package com.soneso.stellargate.networking.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.soneso.stellargate.ui.notification.SgPushController
import org.json.JSONObject

/**
 * Firebase messaging service.
 * Created by cristi.paval on 3/12/18.
 */
class SgFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var pushController: SgPushController

    override fun onCreate() {
        super.onCreate()

        pushController = SgPushController(this)
    }

    override fun onMessageReceived(p0: RemoteMessage?) {
        val rm = p0 ?: return

        pushController.showGeneralNotification(JSONObject(rm.data).toString())
    }
}