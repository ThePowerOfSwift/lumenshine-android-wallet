package com.soneso.lumenshine.domain.data.wallet

import com.soneso.lumenshine.networking.dto.WalletDto
import org.stellar.sdk.responses.AccountResponse

fun WalletDto.toWallet(): Wallet {

    return Wallet(
            id,
            name,
            federationAddress,
            showOnHomeScreen
    )
}

fun AccountResponse.toStellarWallet(): StellarWallet {

    return StellarWallet(
            keypair
    )
}