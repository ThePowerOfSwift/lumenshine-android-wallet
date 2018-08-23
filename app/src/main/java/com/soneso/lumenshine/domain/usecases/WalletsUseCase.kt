package com.soneso.lumenshine.domain.usecases

import com.soneso.lumenshine.model.UserRepository
import com.soneso.lumenshine.model.WalletRepository
import javax.inject.Inject

class WalletsUseCase @Inject constructor(
        private val userRepo: UserRepository,
        private val walletRepo: WalletRepository
) {

    fun provideAllWallets() = walletRepo.loadAllWallets()
}