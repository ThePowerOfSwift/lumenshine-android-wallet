package com.soneso.stellargate.persistence

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.soneso.stellargate.domain.data.UserSecurity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg userSecurity: UserSecurity)

    @Query("select * from ${DbNames.TABLE_USER_SECURITY} where ${DbNames.COLUMN_USERNAME} = :username")
    fun loadUserSecurity(username: String): UserSecurity?
}