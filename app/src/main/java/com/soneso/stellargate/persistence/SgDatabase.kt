package com.soneso.stellargate.persistence

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.soneso.stellargate.domain.data.StellarAccount

@Database(entities = [
    StellarAccount::class
],
        version = 1)
abstract class SgDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        const val DB_NAME = "secure-sg-db"
    }
}