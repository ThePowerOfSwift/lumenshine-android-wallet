package com.soneso.lumenshine.model.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.soneso.lumenshine.persistence.room.DbNames

@Entity(tableName = DbNames.TABLE_REGISTRATION_STATUS)
data class RegistrationStatus(

        @PrimaryKey
        @ColumnInfo(name = DbNames.COLUMN_USERNAME)
        val username: String,

        @ColumnInfo(name = DbNames.COLUMN_MAIL_CONFIRMATION)
        val mailConfirmed: Boolean,

        @ColumnInfo(name = DbNames.COLUMN_TFA_CONFIRMATION)
        val tfaConfirmed: Boolean,

        @ColumnInfo(name = DbNames.COLUMN_MNEMONIC_CONFIRMATION)
        val mnemonicConfirmed: Boolean
) {
    override fun equals(other: Any?): Boolean {

        val o = other as? RegistrationStatus ?: return false
        return o.username == username
                && o.mailConfirmed == mailConfirmed
                && o.tfaConfirmed == tfaConfirmed
                && o.mnemonicConfirmed == mnemonicConfirmed
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + mailConfirmed.hashCode()
        result = 31 * result + tfaConfirmed.hashCode()
        result = 31 * result + mnemonicConfirmed.hashCode()
        return result
    }
}