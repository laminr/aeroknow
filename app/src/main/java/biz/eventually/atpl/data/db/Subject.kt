package biz.eventually.atpl.data.db

import android.arch.persistence.room.*

/**
 * Created by thibault on 20/03/17.
 */
@Entity(
        tableName = "subject",
        indices= arrayOf(Index(value = "source_id", name = "idx_subject_source_id")),
        foreignKeys = arrayOf(
                ForeignKey(entity = Source::class, parentColumns = arrayOf("idWeb"), childColumns = arrayOf("source_id"))
        )
)
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

