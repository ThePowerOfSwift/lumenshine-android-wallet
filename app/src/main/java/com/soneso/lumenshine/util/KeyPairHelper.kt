package com.soneso.lumenshine.util

import com.soneso.stellarmnemonics.Wallet
import org.stellar.sdk.KeyPair

object KeyPairHelper {

    fun keyPair(mnemonic: CharArray): KeyPair {
        return Wallet.createKeyPair(mnemonic, null, 0)
    }
}