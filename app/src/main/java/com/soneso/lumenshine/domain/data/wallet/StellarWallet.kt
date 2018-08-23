package com.soneso.lumenshine.domain.data.wallet

import org.stellar.sdk.KeyPair

data class StellarWallet(
        val keyPair: KeyPair
)