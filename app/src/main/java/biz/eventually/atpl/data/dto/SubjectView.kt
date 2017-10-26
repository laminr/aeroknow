package biz.eventually.atpl.data.dto

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import biz.eventually.atpl.data.db.Subject
import biz.eventually.atpl.data.db.Topic

/**
 * Created by Thibault de Lambilly on 25/10/17.
 */
class SubjectView(
        @Embedded
        var subject: Subject = Subject(-1, -1, ""),

        @Relation(parentColumn = "idWeb", entityColumn = "subject_id", entity = Topic::class)
        var topics: List<Topic> = listOf()
)