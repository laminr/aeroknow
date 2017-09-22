package biz.eventually.atpl.data.model

import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

/**
 * Created by thibault on 20/03/17.
 */
open class Topic() : RealmObject() {

    @PrimaryKey
    @Required
    var id : String = UUID.randomUUID().toString()

    var idWeb: Int = -1

    var name: String = ""

    var questions: Int = 0

    var follow: Int = 0

    var focus: Int = 0

    @Ignore
    var mean: Double = 0.0

    constructor(idWeb: Int, name: String): this() {
        this.idWeb = idWeb
        this.name = name
    }

    constructor(idWeb: Int, name: String, questions: Int?, follow: Int?, focus: Int?): this() {
        this.idWeb = idWeb
        this.name = name
        this.questions = questions ?: 0
        this.follow = follow ?: 0
        this.focus = focus ?: 0
    }

    constructor(idWeb: Int, name: String, questions: Int?, follow: Int?, focus: Int?, mean: Double?)
            : this(idWeb, name, questions, follow, focus) {
        this.mean = mean ?: 0.0
    }
}