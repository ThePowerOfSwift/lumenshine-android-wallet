package com.soneso.lumenshine.presentation.wallets

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.soneso.lumenshine.domain.data.wallet.Wallet
import com.soneso.lumenshine.domain.usecases.WalletsUseCase
import com.soneso.lumenshine.networking.dto.exceptions.ServerException
import com.soneso.lumenshine.util.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class WalletsViewModel(private val walletsUseCase: WalletsUseCase) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val liveWallets: LiveData<Resource<List<Wallet>, ServerException>> = MutableLiveData()

    init {
        compositeDisposable.add(fetchAllWallets())
    }

    private fun fetchAllWallets() = walletsUseCase.provideAllWallets()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                (liveWallets as MutableLiveData).value = it
            }

    override fun onCleared() {

        compositeDisposable.dispose()
        super.onCleared()
    }
}