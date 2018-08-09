package com.soneso.lumenshine.persistence

object DbNames {

    const val DB_NAME = "secure-sg-db"

    const val COLUMN_USERNAME = "username"
    const val TABLE_USER_SECURITY = "user_securities"
    const val COLUMN_PK_0 = "public_key_index_0"
    const val COLUMN_PK_188 = "public_key_index_188"
    const val COLUMN_PASS_KDF_SALT = "ppassword_kdf_salt"
    const val COLUMN_ENCRYPTED_MNEMONIC_MASTER_KEY = "encrypted_mnemonic_master_key"
    const val COLUMN_MMK_ENCRYPTION_IV = "mnemonic_master_key_encryption_iv"
    const val COLUMN_ENCRYPTED_MNEMONIC = "encrypted_mnemonic"
    const val COLUMN_MNEMONIC_ENCRYPTION_IV = "mnemonic_encryption_iv"
    const val COLUMN_ENCRYPTED_WORD_LIST_MASTER_KEY = "encrypted_word_list_master_key"
    const val COLUMN_WLMK_ENCRYPTION_IV = "word_list_master_key_encryption_iv"
    const val COLUMN_ENCRYPTED_WORD_LIST = "encrypted_word_list"
    const val COLUMN_WORD_LIST_ENCRYPTION_IV = "word_list_encryption_iv"
}