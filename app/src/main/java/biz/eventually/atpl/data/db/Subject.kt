package biz.eventually.atpl.data.db

import android.arch.persistence.room.*

/**
 * Created by Thibault de Lambilly on 20/03/17.
 *
 */
@Entity(
        tableName = "subject",
        indices = [Index(value = ["source_id"], name = "idx_subject_source_id")],
        foreignKeys = [ForeignKey(
                entity = Source::class,
                parentColumns = ["idWeb"],
                childColumns = ["source_id"],
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE
        )]
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

