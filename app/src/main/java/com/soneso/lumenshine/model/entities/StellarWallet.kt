package com.soneso.lumenshine.model.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.soneso.lumenshine.persistence.room.DbNames

@Entity(tableName = DbNames.TABLE_STELLAR_ACCOUNTS)
data class StellarWallet(

        @PrimaryKey
        @ColumnInfo(name = DbNames.COLUMN_PK_0)
        val publicKey0: String


)