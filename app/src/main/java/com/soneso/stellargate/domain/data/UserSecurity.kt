package com.soneso.stellargate.domain.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.soneso.stellargate.persistence.DbNames

@Entity(tableName = DbNames.TABLE_USER_SECURITY)
class UserSecurity(

        @PrimaryKey
        @ColumnInfo(name = DbNames.COLUMN_USERNAME)
        val username: String,

        @ColumnInfo(name = DbNames.COLUMN_PK_0)
        val publicKeyIndex0: String,

        @ColumnInfo(name = DbNames.COLUMN_PK_188)
        var publicKeyIndex188: String,

        @ColumnInfo(name = DbNames.COLUMN_PASS_KDF_SALT)
        val passwordKdfSalt: ByteArray,

        @ColumnInfo(name = DbNames.COLUMN_ENCRYPTED_MASTER_KEY)
        val encryptedMasterKey: ByteArray,

        @ColumnInfo(name = DbNames.COLUMN_MK_ENCRYPTION_IV)
        val masterKeyEncryptionIv: ByteArray,

        @ColumnInfo(name = DbNames.COLUMN_ENCRYPTED_MNEMONIC)
        val encryptedMnemonic: ByteArray,

        @ColumnInfo(name = DbNames.COLUMN_MNEMONIC_ENCRYPTION_IV)
        val mnemonicEncryptionIv: ByteArray,

        @ColumnInfo(name = DbNames.COLUMN_ENCRYPTED_WORD_LIST)
        val encryptedWordList: ByteArray,

        @ColumnInfo(name = DbNames.COLUMN_WORD_LIST_ENCRYPTION_IV)
        val wordListEncryptionIv: ByteArray
) {
    companion object {
        fun mockInstance() = UserSecurity(
                "",
                "",
                "",
                ByteArray(0),
                ByteArray(0),
                ByteArray(0),
                ByteArray(0),
                ByteArray(0),
                ByteArray(0),
                ByteArray(0)
        )
    }
}