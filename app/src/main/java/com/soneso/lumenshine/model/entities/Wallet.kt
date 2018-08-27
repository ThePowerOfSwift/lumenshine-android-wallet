package com.soneso.lumenshine.model.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.soneso.lumenshine.persistence.room.DbNames

@Entity(tableName = DbNames.TABLE_WALLETS)
data class Wallet(

        @PrimaryKey
        @ColumnInfo(name = DbNames.COLUMN_ID)
        val id: Long,

        @ColumnInfo(name = DbNames.COLUMN_NAME)
        val name: String,

        @ColumnInfo(name = DbNames.COLUMN_FEDERATION_ADDRESS)
        val federationAddress: String,

        @ColumnInfo(name = DbNames.COLUMN_IN_HOME_SCREEN)
        val shownInHomeScreen: Boolean
)