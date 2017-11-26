package biz.eventually.atpl.data.dto

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import biz.eventually.atpl.data.db.Answer
import biz.eventually.atpl.data.db.Question
import biz.eventually.atpl.data.db.Subject
import biz.eventually.atpl.data.db.Topic

/**
 * Created by Thibault de Lambilly on 25/10/17.
 */
class QuestionView(
        @Embedded
        var question: Question = Question(-1, -1, "", ""),

        @Relation(parentColumn = "idWeb", entityColumn = "question_id", entity = Answer::class)
        var answers: List<Answer>? = null
)