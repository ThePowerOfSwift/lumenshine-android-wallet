package com.soneso.stellargate.domain.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Account model.
 * Created by cristi.paval on 3/20/18.
 */
@Entity
class StellarAccount(

        @PrimaryKey
        val accountId: String,
        val balance: String
)