package com.soneso.lumenshine.persistence.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.soneso.lumenshine.model.entities.RegistrationStatus
import com.soneso.lumenshine.model.entities.UserSecurity
import com.soneso.lumenshine.model.entities.Wallet

@Database(
        entities = [UserSecurity::class, Wallet::class, RegistrationStatus::class],
        version = 1
)
abstract class LsDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun walletDao(): WalletDao
}