package com.soneso.lumenshine.model.push

import com.google.firebase.iid.FirebaseInstanceIdService

/**
 * Firebase instance id service.
 * Created by cristi.paval on 3/12/18.
 */
class SgFirebaseInstanceIdService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()
    }
}