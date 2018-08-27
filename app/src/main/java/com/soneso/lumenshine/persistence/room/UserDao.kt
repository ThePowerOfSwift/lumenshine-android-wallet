package com.soneso.lumenshine.persistence.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.soneso.lumenshine.model.entities.RegistrationInfo
import com.soneso.lumenshine.model.entities.UserSecurity
import io.reactivex.Flowable

@Dao
interface UserDao {

    @Query("SELECT * FROM ${DbNames.TABLE_USER_DATA} WHERE ${DbNames.COLUMN_USERNAME} = :username")
    fun getUserDataById(username: String): Flowable<UserSecurity>

    @Query("SELECT * FROM ${DbNames.TABLE_REGISTRATION_STATUS} WHERE ${DbNames.COLUMN_USERNAME} = :username")
    fun getRegistrationStatus(username: String): Flowable<RegistrationInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveRegistrationStatus(status: RegistrationInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUserData(userData: UserSecurity)
}