package com.soneso.lumenshine.model.entities

import org.stellar.sdk.KeyPair

data class StellarWallet(
        val keyPair: KeyPair
)