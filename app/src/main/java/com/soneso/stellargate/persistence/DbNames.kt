package com.soneso.stellargate.persistence

object DbNames {

    const val DB_NAME = "secure-sg-db"

    const val TABLE_LOGIN_SESSION = "login_sessions"
    const val COLUMN_USERNAME = "username"
    const val COLUMN_TFA_SECRET = "tfa_secret"
    const val COLUMN_JWT_TOKEN = "jwt_token"
    const val COLUMN_PASSWORD = "password"

    const val TABLE_USER_SECURITY = "user_securities"
    const val COLUMN_PK_0 = "public_key_index_0"
    const val COLUMN_PK_188 = "public_key_index_188"
    const val COLUMN_PASS_KDF_SALT = "ppassword_kdf_salt"
    const val COLUMN_ENCRYPTED_MASTER_KEY = "encrypted_master_key"
    const val COLUMN_MK_ENCRYPTION_IV = "master_key_encryption_iv"
    const val COLUMN_ENCRYPTED_MNEMONIC = "encrypted_mnemonic"
    const val COLUMN_MNEMONIC_ENCRYPTION_IV = "mnemonic_encryption_iv"
    const val COLUMN_ENCRYPTED_WORD_LIST = "encrypted_word_list"
    const val COLUMN_WORD_LIST_ENCRYPTION_IV = "word_list_encryption_iv"
}