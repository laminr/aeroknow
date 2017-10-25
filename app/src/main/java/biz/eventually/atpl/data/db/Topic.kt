package biz.eventually.atpl.data.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import io.realm.annotations.Ignore

/**
 * Created by thibault on 20/03/17.
 */
@Entity(tableName = "topic")
class Topic(
        @PrimaryKey
        @ColumnInfo(name = "idWeb")
        val idWeb: Long,
        @ColumnInfo(name = "subject_id")
        val subjectId: Long,
        var name: String = "",
        var questions: Int = 0,
        var follow: Int = 0,
        var focus: Int = 0
) {

    @Ignore
    var mean: Double = 0.0
}