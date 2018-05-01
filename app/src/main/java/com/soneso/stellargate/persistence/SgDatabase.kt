package com.soneso.stellargate.persistence

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.soneso.stellargate.domain.data.LoginSession
import com.soneso.stellargate.domain.data.StellarAccount

@Database(entities = [
    StellarAccount::class,
    LoginSession::class
],
        version = 1)
abstract class SgDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
}