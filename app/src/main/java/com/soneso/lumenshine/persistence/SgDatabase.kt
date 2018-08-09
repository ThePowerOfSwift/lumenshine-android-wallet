package com.soneso.lumenshine.persistence

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.soneso.lumenshine.domain.data.UserSecurity

@Database(entities = [
    UserSecurity::class
],
        version = 1)
abstract class SgDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
}