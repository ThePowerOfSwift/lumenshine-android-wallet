package com.soneso.stellargate.domain.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.soneso.stellargate.persistence.DbNames

@Entity(tableName = DbNames.TABLE_LOGIN_SESSION)
data class LoginSession
@Ignore constructor(

        @PrimaryKey
        @ColumnInfo(name = DbNames.COLUMN_USERNAME)
        var username: String,

        @ColumnInfo(name = DbNames.COLUMN_PASSWORD)
        var password: String,

        @ColumnInfo(name = DbNames.COLUMN_TFA_SECRET)
        var tfaSecret: String,

        @ColumnInfo(name = DbNames.COLUMN_JWT_TOKEN)
        var jwtToken: String
) {
    constructor() : this("", "", "", "")
}