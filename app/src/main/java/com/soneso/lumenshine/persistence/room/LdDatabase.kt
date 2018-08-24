package com.soneso.lumenshine.persistence.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.soneso.lumenshine.model.entities.UserSecurity

@Database(entities = [
    UserSecurity::class
],
        version = 1)
abstract class LdDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun walletDao(): WalletDao
}