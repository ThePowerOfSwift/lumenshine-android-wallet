package com.soneso.stellargate.model.user

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.soneso.stellargate.domain.data.UserLogin

@Dao
interface UserDao {

    @Query("select * from ${UserLogin.TABLE_NAME} where ${UserLogin.COLUMN_EMAIL} = :arg0")
    fun loadUserLogin(arg0: String): UserLogin

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUserLogin(vararg userLogin: UserLogin)
}