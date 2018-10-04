package com.soneso.lumenshine.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.soneso.lumenshine.persistence.room.DbNames

@Entity(tableName = DbNames.TABLE_STELLAR_ACCOUNTS)
data class StellarWallet(

        @PrimaryKey
        @ColumnInfo(name = DbNames.COLUMN_PK_0)
        val publicKey0: String


)