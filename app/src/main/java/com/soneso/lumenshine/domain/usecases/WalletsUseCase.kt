package com.soneso.lumenshine.domain.usecases

import com.soneso.lumenshine.model.WalletRepository
import javax.inject.Inject

class WalletsUseCase
@Inject constructor(private val walletRepo: WalletRepository) {

    fun provideAllWallets() = walletRepo.loadAllWallets()
}