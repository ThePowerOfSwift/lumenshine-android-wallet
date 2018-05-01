package com.soneso.stellargate.persistence

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.soneso.stellargate.domain.data.LoginSession

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg loginSession: LoginSession)

    @Query("update ${DbNames.TABLE_LOGIN_SESSION} set ${DbNames.COLUMN_AUTH_TOKEN} = :username where ${DbNames.COLUMN_USERNAME} = :jwtToken")
    fun updateLoginSession(username: String, jwtToken: String)

    @Query("select * from ${DbNames.TABLE_LOGIN_SESSION} where ${DbNames.COLUMN_USERNAME} = :username")
    fun loadLoginSession(username: String): LoginSession?
}