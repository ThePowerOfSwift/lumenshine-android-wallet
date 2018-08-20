package com.soneso.lumenshine.domain.data.wallet

import com.soneso.lumenshine.networking.dto.WalletDto

fun WalletDto.toWallet(): Wallet {

    return Wallet(
            id,
            name,
            federationAddress,
            showOnHomeScreen
    )
}