package com.soneso.stellargate.persistence

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.soneso.stellargate.domain.data.LoginSession
import com.soneso.stellargate.domain.data.UserSecurity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg loginSession: LoginSession)

    @Query("update ${DbNames.TABLE_LOGIN_SESSION} set ${DbNames.COLUMN_JWT_TOKEN} = :jwtToken, ${DbNames.COLUMN_PASSWORD} = :password where ${DbNames.COLUMN_USERNAME} = :username")
    fun updateLoginSession(username: String, password: String, jwtToken: String)

    @Query("update ${DbNames.TABLE_LOGIN_SESSION} set ${DbNames.COLUMN_JWT_TOKEN} = :jwtToken where ${DbNames.COLUMN_USERNAME} = :username")
    fun updateJwtToken(username: String, jwtToken: String)

    @Query("update ${DbNames.TABLE_LOGIN_SESSION} set ${DbNames.COLUMN_TFA_SECRET} = :tfaSecret where ${DbNames.COLUMN_USERNAME} = :username")
    fun updateTfaSecret(username: String, tfaSecret: String)

    @Query("select * from ${DbNames.TABLE_LOGIN_SESSION} where ${DbNames.COLUMN_USERNAME} = :username")
    fun loadLoginSession(username: String): LoginSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg userSecurity: UserSecurity)

    @Query("select * from ${DbNames.TABLE_USER_SECURITY} where ${DbNames.COLUMN_USERNAME} = :username")
    fun loadUserSecurity(username: String): UserSecurity?
}