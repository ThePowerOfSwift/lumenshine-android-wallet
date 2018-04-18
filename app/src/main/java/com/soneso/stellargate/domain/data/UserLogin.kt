package com.soneso.stellargate.domain.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = UserLogin.TABLE_NAME)
class UserLogin(

        @PrimaryKey
        @ColumnInfo(name = COLUMN_EMAIL)
        val email: String,

        val jwtToken: String,

        val token2fa: String
) {

    companion object {
        const val TABLE_NAME = "user_login"
        const val COLUMN_EMAIL = "email"
    }
}