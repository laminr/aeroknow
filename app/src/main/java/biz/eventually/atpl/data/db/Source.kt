package biz.eventually.atpl.data.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by thibault on 20/03/17.
 */
@Entity(tableName = "source")
data class Source(

        @PrimaryKey
        @ColumnInfo(name = "idWeb")
        var idWeb: Long? = null,

        @ColumnInfo(name = "name")
        var name: String
)
