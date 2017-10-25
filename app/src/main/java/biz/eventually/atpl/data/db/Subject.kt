package biz.eventually.atpl.data.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

/**
 * Created by thibault on 20/03/17.
 */
@Entity(tableName = "subject")
class Subject(

    @PrimaryKey
    @ColumnInfo(name = "idWeb")
    var idWeb: Long,

    @ColumnInfo(name = "source_id")
    var sourceId: Long,

    var name: String
) {

    @Ignore
    var topics: List<Topic>? = null
}

