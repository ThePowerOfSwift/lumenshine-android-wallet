package com.soneso.lumenshine.model.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.soneso.lumenshine.persistence.room.DbNames

@Entity(tableName = DbNames.TABLE_REGISTRATION_STATUS)
data class RegistrationInfo(

        @PrimaryKey
        @ColumnInfo(name = DbNames.COLUMN_USERNAME)
        val username: String,

        @ColumnInfo(name = DbNames.COLUMN_MAIL_CONFIRMATION)
        val mailConfirmed: Boolean,

        @ColumnInfo(name = DbNames.COLUMN_TFA_CONFIRMATION)
        val tfaConfirmed: Boolean,

        @ColumnInfo(name = DbNames.COLUMN_MNEMONIC_CONFIRMATION)
        val mnemonicConfirmed: Boolean
)