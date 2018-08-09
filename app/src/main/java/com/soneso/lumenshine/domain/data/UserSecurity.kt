package com.soneso.lumenshine.domain.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.soneso.lumenshine.persistence.DbNames

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

        @ColumnInfo(name = DbNames.COLUMN_ENCRYPTED_MNEMONIC_MASTER_KEY)
        val encryptedMnemonicMasterKey: ByteArray,

        @ColumnInfo(name = DbNames.COLUMN_MMK_ENCRYPTION_IV)
        val mnemonicMasterKeyEncryptionIv: ByteArray,

        @ColumnInfo(name = DbNames.COLUMN_ENCRYPTED_MNEMONIC)
        val encryptedMnemonic: ByteArray,

        @ColumnInfo(name = DbNames.COLUMN_MNEMONIC_ENCRYPTION_IV)
        val mnemonicEncryptionIv: ByteArray,

        @ColumnInfo(name = DbNames.COLUMN_ENCRYPTED_WORD_LIST_MASTER_KEY)
        val encryptedWordListMasterKey: ByteArray,

        @ColumnInfo(name = DbNames.COLUMN_WLMK_ENCRYPTION_IV)
        val wordListMasterKeyEncryptionIv: ByteArray,

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
                ByteArray(0),
                ByteArray(0),
                ByteArray(0)
        )
    }
}