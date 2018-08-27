package com.soneso.lumenshine.persistence.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.soneso.lumenshine.model.entities.Wallet
import io.reactivex.Flowable

@Dao
interface WalletDao {

    @Query("SELECT * FROM ${DbNames.TABLE_WALLETS}")
    fun getAllWallets(): Flowable<List<Wallet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(wallets: List<Wallet>)
}