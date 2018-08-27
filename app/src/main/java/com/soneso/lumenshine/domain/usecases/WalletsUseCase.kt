package com.soneso.lumenshine.domain.usecases

import com.soneso.lumenshine.model.UserRepository
import com.soneso.lumenshine.model.WalletRepository
import com.soneso.lumenshine.model.entities.Wallet
import com.soneso.lumenshine.networking.dto.exceptions.ServerException
import com.soneso.lumenshine.util.Resource
import io.reactivex.Flowable
import javax.inject.Inject

class WalletsUseCase @Inject constructor(
        private val userRepo: UserRepository,
        private val walletRepo: WalletRepository
) {

    fun provideAllWallets(): Flowable<Resource<List<Wallet>, ServerException>> {

//        userRepo.getCurrentUserSecurity()
//                .
//        walletRepo.loadStellarWallet(LsSessionProfile.)
        return walletRepo.loadAllWallets()
    }
}