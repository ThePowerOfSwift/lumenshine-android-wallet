package com.soneso.lumenshine.domain.data.wallet

data class Wallet(
        val id: Long,
        val name: String,
        val federationAddress: String,
        val shownInHomeScreen: Boolean
)