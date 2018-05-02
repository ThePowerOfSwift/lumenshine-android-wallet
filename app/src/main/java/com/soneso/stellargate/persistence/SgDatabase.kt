package com.soneso.stellargate.persistence

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.soneso.stellargate.domain.data.LoginSession
import com.soneso.stellargate.domain.data.UserSecurity

@Database(entities = [
    LoginSession::class,
    UserSecurity::class
],
        version = 1)
abstract class SgDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
}